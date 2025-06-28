package redstone.multimeter.server;

import java.text.NumberFormat;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Supplier;

import com.google.common.base.Suppliers;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.TickPriority;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.dimension.DimensionType;

import redstone.multimeter.block.Meterable;
import redstone.multimeter.block.PowerSource;
import redstone.multimeter.client.gui.text.ClickEvent;
import redstone.multimeter.client.gui.text.Formatting;
import redstone.multimeter.client.gui.text.HoverEvent;
import redstone.multimeter.client.gui.text.Text;
import redstone.multimeter.client.gui.text.TextColor;
import redstone.multimeter.client.gui.text.Texts;
import redstone.multimeter.common.DimPos;
import redstone.multimeter.common.meter.Meter;
import redstone.multimeter.common.meter.MeterGroup;
import redstone.multimeter.common.meter.MeterProperties;
import redstone.multimeter.common.meter.MeterProperties.MutableMeterProperties;
import redstone.multimeter.common.meter.event.EventType;
import redstone.multimeter.common.network.packets.ClearMeterGroupPacket;
import redstone.multimeter.common.network.packets.MeterGroupDefaultPacket;
import redstone.multimeter.common.network.packets.MeterGroupRefreshPacket;
import redstone.multimeter.common.network.packets.MeterGroupSubscriptionPacket;
import redstone.multimeter.interfaces.mixin.IBlock;
import redstone.multimeter.server.meter.ServerMeterGroup;
import redstone.multimeter.server.meter.ServerMeterPropertiesManager;
import redstone.multimeter.server.meter.event.MeterEventPredicate;
import redstone.multimeter.server.option.Options;
import redstone.multimeter.server.option.OptionsManager;

public class Multimeter {

	private static final NumberFormat NUMBER_FORMAT = NumberFormat.getNumberInstance(Locale.US);

	private final MultimeterServer server;
	private final Map<String, ServerMeterGroup> meterGroups;
	private final Map<UUID, ServerMeterGroup> subscriptions;
	private final Set<ServerMeterGroup> activeMeterGroups;
	private final Set<ServerMeterGroup> idleMeterGroups;
	private final ServerMeterPropertiesManager meterPropertiesManager;

	public Options options;

	public Multimeter(MultimeterServer server) {
		this.server = server;
		this.meterGroups = new LinkedHashMap<>();
		this.subscriptions = new HashMap<>();
		this.activeMeterGroups = new HashSet<>();
		this.idleMeterGroups = new HashSet<>();
		this.meterPropertiesManager = new ServerMeterPropertiesManager(this);

		reloadOptions();
	}

	public MultimeterServer getServer() {
		return server;
	}

	public Collection<ServerMeterGroup> getMeterGroups() {
		return Collections.unmodifiableCollection(meterGroups.values());
	}

	public ServerMeterGroup getMeterGroup(String name) {
		return meterGroups.get(name);
	}

	public boolean hasMeterGroup(String name) {
		return meterGroups.containsKey(name);
	}

	public ServerMeterGroup getSubscription(ServerPlayer player) {
		return subscriptions.get(player.getUUID());
	}

	public boolean hasSubscription(ServerPlayer player) {
		return subscriptions.containsKey(player.getUUID());
	}

	public boolean isOwnerOfSubscription(ServerPlayer player) {
		ServerMeterGroup meterGroup = getSubscription(player);
		return meterGroup != null && meterGroup.isOwnedBy(player);
	}

	public void reloadOptions() {
		if (server.isDedicatedServer()) {
			options = OptionsManager.load(server.getConfigDirectory());
		} else {
			options = new Options();
		}
	}

	public void tickStart(boolean paused) {
		if (!paused) {
			removeIdleMeterGroups();

			for (ServerMeterGroup meterGroup : meterGroups.values()) {
				meterGroup.tick();
			}
		}
	}

	public void tickEnd(boolean paused) {
		broadcastMeterUpdates();

		if (!paused) {
			broadcastMeterLogs();
		}
	}

	private void removeIdleMeterGroups() {
		Iterator<ServerMeterGroup> it = idleMeterGroups.iterator();

		while (it.hasNext()) {
			ServerMeterGroup meterGroup = it.next();

			if (removeIdleMeterGroup(meterGroup)) {
				it.remove();
			}
		}
	}

	private boolean removeIdleMeterGroup(ServerMeterGroup meterGroup) {
		if (meterGroup.hasMeters() && !meterGroup.isPastIdleTimeLimit()) {
			return false;
		}

		meterGroups.remove(meterGroup.getName(), meterGroup);

		if (meterGroup.hasMeters()) {
			notifyOwnerOfRemoval(meterGroup);
		}

		return true;
	}

	private void notifyOwnerOfRemoval(ServerMeterGroup meterGroup) {
		UUID ownerUuid = meterGroup.getOwner();
		ServerPlayer owner = server.getPlayerList().get(ownerUuid);

		if (owner != null) {
			Text message = Texts.literal("One of your meter groups, \'%s\', was idle for more than %d ticks and has been removed.", meterGroup.getName(), options.meter_group.max_idle_time);
			server.sendMessage(owner, message, false);
		}
	}

	private void broadcastMeterUpdates() {
		for (ServerMeterGroup meterGroup : meterGroups.values()) {
			meterGroup.broadcastUpdates();
		}
	}

	private void broadcastMeterLogs() {
		for (ServerMeterGroup meterGroup : meterGroups.values()) {
			meterGroup.getLogManager().broadcastLogs();
		}
	}

	public void onPlayerJoin(ServerPlayer player) {
		server.refreshTickPhaseTree(player);
		server.getPlayerList().updatePermissions(player);
	}

	public void onPlayerLeave(ServerPlayer player) {
		ServerMeterGroup meterGroup = getSubscription(player);

		if (meterGroup != null) {
			removeSubscriberFromMeterGroup(meterGroup, player);
		}
	}

	public void addMeter(ServerPlayer player, MeterProperties properties) {
		ServerMeterGroup meterGroup = getSubscription(player);

		if (meterGroup != null) {
			if (meterGroup.isPastMeterLimit()) {
				Text message = Texts.literal("meter limit (%d) reached!", options.meter_group.meter_limit);
				server.sendMessage(player, message, true);
			} else if (!addMeter(meterGroup, properties)) {
				refreshMeterGroup(meterGroup, player);
			}
		}
	}

	public boolean addMeter(ServerMeterGroup meterGroup, MeterProperties meterProperties) {
		MutableMeterProperties properties = meterProperties.mutable();

		if (!meterPropertiesManager.validate(properties) || !meterGroup.addMeter(properties)) {
			return false;
		}

		DimPos pos = properties.getPos();
		Level level = server.getLevel(pos);
		BlockPos blockPos = pos.getBlockPos();
		BlockState state = level.getBlockState(blockPos);

		logPowered(level, blockPos, state);
		logActive(level, blockPos, state);

		return true;
	}

	public void removeMeter(ServerPlayer player, long id) {
		ServerMeterGroup meterGroup = getSubscription(player);

		if (meterGroup != null && !meterGroup.removeMeter(id)) {
			refreshMeterGroup(meterGroup, player);
		}
	}

	public void updateMeter(ServerPlayer player, long id, MeterProperties newProperties) {
		ServerMeterGroup meterGroup = getSubscription(player);

		if (meterGroup != null && !meterGroup.updateMeter(id, newProperties)) {
			refreshMeterGroup(meterGroup, player);
		}
	}

	public void setMeterIndex(ServerPlayer player, long id, int index) {
		ServerMeterGroup meterGroup = getSubscription(player);

		if (meterGroup != null && !meterGroup.setMeterIndex(id, index)) {
			refreshMeterGroup(meterGroup, player);
		}
	}

	public void clearMeterGroup(ServerPlayer player) {
		ServerMeterGroup meterGroup = getSubscription(player);

		if (meterGroup != null) {
			clearMeterGroup(meterGroup);
		}
	}

	public void clearMeterGroup(ServerMeterGroup meterGroup) {
		meterGroup.clear();

		ClearMeterGroupPacket packet = new ClearMeterGroupPacket();
		server.getPlayerList().send(packet, meterGroup);
	}

	public void setMeters(ServerPlayer player, List<MeterProperties> meters) {
		ServerMeterGroup meterGroup = getSubscription(player);

		if (meterGroup != null) {
			if (meterGroup.isOwnedBy(player)) {
				setMeters(meterGroup, meters);
			} else {
				Text message = Texts.literal("Could not set meters for meter group \"%s\": you are not the owner of that meter group!", meterGroup.getName());
				server.sendMessage(player, message, false);
			}
		}
	}

	public void setMeters(ServerMeterGroup meterGroup, List<MeterProperties> meters) {
		clearMeterGroup(meterGroup);

		for (MeterProperties meter : meters) {
			addMeter(meterGroup, meter);
		}
	}

	public void createMeterGroup(ServerPlayer player, String name) {
		if (!MeterGroup.isValidName(name) || meterGroups.containsKey(name)) {
			return;
		}

		ServerMeterGroup meterGroup = new ServerMeterGroup(this, name, player);
		meterGroups.put(name, meterGroup);

		subscribeToMeterGroup(meterGroup, player);
	}

	public void subscribeToMeterGroup(ServerMeterGroup meterGroup, ServerPlayer player) {
		ServerMeterGroup prevSubscription = getSubscription(player);

		if (prevSubscription == meterGroup) {
			refreshMeterGroup(meterGroup, player);
		} else {
			if (prevSubscription != null) {
				removeSubscriberFromMeterGroup(prevSubscription, player);
			}

			addSubscriberToMeterGroup(meterGroup, player);
			onSubscriptionChanged(player, prevSubscription, meterGroup);
		}
	}

	public void subscribeToDefaultMeterGroup(ServerPlayer player) {
		MeterGroupDefaultPacket packet = new MeterGroupDefaultPacket();
		server.getPlayerList().send(packet, player);
	}

	private void addSubscriberToMeterGroup(ServerMeterGroup meterGroup, ServerPlayer player) {
		UUID playerUuid = player.getUUID();

		subscriptions.put(playerUuid, meterGroup);
		meterGroup.addSubscriber(playerUuid);

		if (meterGroup.updateIdleState()) {
			activeMeterGroups.add(meterGroup);
			idleMeterGroups.remove(meterGroup);
		}
	}

	public void unsubscribeFromMeterGroup(ServerPlayer player) {
		ServerMeterGroup meterGroup = getSubscription(player);

		if (meterGroup != null) {
			unsubscribeFromMeterGroup(meterGroup, player);
		}
	}

	public void unsubscribeFromMeterGroup(ServerMeterGroup meterGroup, ServerPlayer player) {
		if (meterGroup.hasSubscriber(player)) {
			removeSubscriberFromMeterGroup(meterGroup, player);
			onSubscriptionChanged(player, meterGroup, null);
		}
	}

	private void removeSubscriberFromMeterGroup(ServerMeterGroup meterGroup, ServerPlayer player) {
		UUID playerUuid = player.getUUID();

		subscriptions.remove(playerUuid, meterGroup);
		meterGroup.removeSubscriber(playerUuid);

		if (meterGroup.updateIdleState()) {
			activeMeterGroups.remove(meterGroup);
			idleMeterGroups.add(meterGroup);
		}
	}

	private void onSubscriptionChanged(ServerPlayer player, ServerMeterGroup prevSubscription, ServerMeterGroup newSubscription) {
		MeterGroupSubscriptionPacket packet;

		if (newSubscription == null) {
			packet = new MeterGroupSubscriptionPacket(prevSubscription.getName(), false);
		} else {
			packet = new MeterGroupSubscriptionPacket(newSubscription.getName(), true);
		}

		server.getPlayerList().send(packet, player);
		server.getPlayerList().updatePermissions(player);
	}

	public void clearMembersOfMeterGroup(ServerMeterGroup meterGroup) {
		for (UUID playerUuid : meterGroup.getMembers()) {
			removeMemberFromMeterGroup(meterGroup, playerUuid);
		}
	}

	public void addMemberToMeterGroup(ServerMeterGroup meterGroup, UUID playerUuid) {
		if (meterGroup.hasMember(playerUuid) || meterGroup.isOwnedBy(playerUuid)) {
			return;
		}

		ServerPlayer player = server.getPlayerList().get(playerUuid);

		if (player == null) {
			return;
		}

		meterGroup.addMember(playerUuid);

		Text message = Texts.composite(
			String.format("You have been invited to meter group \'%s\' - click ", meterGroup.getName()),
			Texts.literal("[here]").format(style ->
				style.withHoverEvent(HoverEvent.showText(
					Texts.literal("Subscribe to meter group \'%s\'", meterGroup.getName())
				)).withClickEvent(ClickEvent.runCommand(
					String.format("/metergroup subscribe %s", meterGroup.getName())
				)).withColor(Formatting.GREEN)
			),
			" to subscribe to it."
		);

		server.sendMessage(player, message, false);
	}

	public void removeMemberFromMeterGroup(ServerMeterGroup meterGroup, UUID playerUuid) {
		if (!meterGroup.hasMember(playerUuid)) {
			return;
		}

		meterGroup.removeMember(playerUuid);

		if (meterGroup.isPrivate()) {
			ServerPlayer player = server.getPlayerList().get(playerUuid);

			if (player != null && meterGroup.hasSubscriber(playerUuid)) {
				unsubscribeFromMeterGroup(meterGroup, player);

				Text message = Texts.literal("The owner of meter group \'%s\' has removed you as a member!", meterGroup.getName());
				server.sendMessage(player, message, false);
			}
		}
	}

	public void refreshMeterGroup(ServerPlayer player) {
		ServerMeterGroup meterGroup = getSubscription(player);

		if (meterGroup != null) {
			refreshMeterGroup(meterGroup, player);
		}
	}

	private void refreshMeterGroup(ServerMeterGroup meterGroup, ServerPlayer player) {
		MeterGroupRefreshPacket packet = new MeterGroupRefreshPacket(meterGroup);
		server.getPlayerList().send(packet, player);
	}

	public void teleportToMeter(ServerPlayer player, long id) {
		if (!options.meter.allow_teleports) {
			Text message = Texts.literal("This server does not allow meter teleporting!");
			server.sendMessage(player, message, false);

			return;
		}

		ServerMeterGroup meterGroup = getSubscription(player);

		if (meterGroup != null) {
			Meter meter = meterGroup.getMeter(id);

			if (meter != null) {
				DimPos pos = meter.getPos();
				ServerLevel newLevel = server.getLevel(pos);

				if (newLevel != null) {
					ServerLevel oldLevel = player.getLevel();
					double oldX = player.x;
					double oldY = player.y;
					double oldZ = player.z;

					BlockPos blockPos = pos.getBlockPos();

					double newX = blockPos.getX() + 0.5D;
					double newY = blockPos.getY();
					double newZ = blockPos.getZ() + 0.5D;
					float newYRot = player.yRot;
					float newXRot = player.xRot;

					player.teleportTo(newLevel, newX, newY, newZ, newYRot, newXRot);

					Text text = Texts.literal(String.format("Teleported to meter \"%s\"", meter.getName()));
					server.sendMessage(player, text, false);

					sendClickableReturnMessage(oldLevel, oldX, oldY, oldZ, newYRot, newXRot, player);
				}
			}
		}
	}

	/**
	 * Send the player a message they can click to return to the location they were
	 * at before teleporting to a meter.
	 */
	private void sendClickableReturnMessage(ServerLevel level, double _x, double _y, double _z, float _yaw, float _pitch, ServerPlayer player) {
		String dimension = DimensionType.getName(level.dimension.getType()).toString();
		String x = NUMBER_FORMAT.format(_x);
		String y = NUMBER_FORMAT.format(_y);
		String z = NUMBER_FORMAT.format(_z);
		String yaw = NUMBER_FORMAT.format(_yaw);
		String pitch = NUMBER_FORMAT.format(_pitch);

		Text message = Texts.composite(
			"Click",
			Texts.literal("[here]").format(style ->
				style.withHoverEvent(HoverEvent.showText(
					Texts.composite(
						"Teleport to",
						Texts.keyValue("\n  dimension", dimension),
						Texts.keyValue("\n  x", x),
						Texts.keyValue("\n  y", y),
						Texts.keyValue("\n  z", z)
					)
				)).withClickEvent(ClickEvent.runCommand(
					String.format("/execute in %s run tp @s %s %s %s %s %s", dimension, x, y, z, yaw, pitch)
				)).withColor(TextColor.GREEN)
			),
			" to return to your previous location"
		);

		server.sendMessage(player, message, false);
	}

	public void onBlockChange(Level level, BlockPos pos, BlockState oldState, BlockState newState) {
		Block oldBlock = oldState.getBlock();
		Block newBlock = newState.getBlock();

		if (oldBlock == newBlock && ((IBlock)newBlock).rsmm$isPowerSource() && ((PowerSource)newBlock).rsmm$logPowerChangeOnStateChange()) {
			logPowerChange(level, pos, oldState, newState);
		}

		boolean wasMeterable = ((IBlock)oldBlock).rsmm$isMeterable();
		boolean isMeterable = ((IBlock)newBlock).rsmm$isMeterable();

		if (wasMeterable || isMeterable) {
			logActive(level, pos, newState);
		}
	}

	public void logPowered(Level level, BlockPos pos, boolean powered) {
		tryLogEvent(level, pos, EventType.POWERED, powered ? 1 : 0, (meterGroup, meter, event) -> meter.setPowered(powered));
	}

	public void logPowered(Level level, BlockPos pos, BlockState state) {
		tryLogEvent(level, pos, EventType.POWERED, () -> {
			return ((IBlock)state.getBlock()).rsmm$isPowered(level, pos, state) ? 1 : 0;
		}, (meterGroup, meter, event) -> {
			return meter.setPowered(event.getMetadata() != 0);
		});
	}

	public void logActive(Level level, BlockPos pos, boolean active) {
		tryLogEvent(level, pos, EventType.ACTIVE, active ? 1 : 0, (meterGroup, meter, event) -> meter.setActive(active));
	}

	public void logActive(Level level, BlockPos pos, BlockState state) {
		tryLogEvent(level, pos, EventType.ACTIVE, () -> {
			Block block = state.getBlock();
			return ((IBlock)block).rsmm$isMeterable() && ((Meterable)block).rsmm$isActive(level, pos, state) ? 1 : 0;
		}, (meterGroup, meter, event) -> meter.setActive(event.getMetadata() != 0));
	}

	public void logMoved(Level level, BlockPos blockPos, Direction dir) {
		tryLogEvent(level, blockPos, EventType.MOVED, dir.get3DDataValue());
	}

	public void moveMeters(Level level, BlockPos blockPos, Direction dir) {
		DimPos pos = new DimPos(level, blockPos);

		for (ServerMeterGroup meterGroup : activeMeterGroups) {
			meterGroup.tryMoveMeter(pos, dir);
		}
	}

	public void logPowerChange(Level level, BlockPos pos, int oldPower, int newPower) {
		if (oldPower != newPower) {
			tryLogEvent(level, pos, EventType.POWER_CHANGE, (oldPower << 8) | newPower);
		}
	}

	public void logPowerChange(Level level, BlockPos pos, BlockState oldState, BlockState newState) {
		tryLogEvent(level, pos, EventType.POWER_CHANGE, () -> {
			PowerSource block = (PowerSource)newState.getBlock();
			int oldPower = block.rsmm$getPowerLevel(level, pos, oldState);
			int newPower = block.rsmm$getPowerLevel(level, pos, newState);

			return (oldPower << 8) | newPower;
		}, (meterGroup, meter, event) -> {
			int data = event.getMetadata();
			int oldPower = (data >> 8) & 0xFF;
			int newPower = data & 0xFF;

			return oldPower != newPower;
		});
	}

	public void logRandomTick(Level level, BlockPos pos) {
		tryLogEvent(level, pos, EventType.RANDOM_TICK);
	}

	public void logScheduledTick(Level level, BlockPos pos, TickPriority priority, boolean scheduling) {
		tryLogEvent(level, pos, EventType.SCHEDULED_TICK, (scheduling ? (1 << 30) : 0) | (priority.getValue() + 3));
	}

	public void logBlockEvent(Level level, BlockPos pos, int type, int depth, boolean queueing) {
		tryLogEvent(level, pos, EventType.BLOCK_EVENT, (queueing ? (1 << 30) : 0) | (depth << 4) | type);
	}

	public void logEntityTick(Level level, Entity entity) {
		tryLogEvent(level, entity.getCommandSenderBlockPosition(), EventType.ENTITY_TICK);
	}

	public void logBlockEntityTick(Level level, BlockEntity blockEntity) {
		tryLogEvent(level, blockEntity.getBlockPos(), EventType.BLOCK_ENTITY_TICK);
	}

	public void logBlockUpdate(Level level, BlockPos pos) {
		tryLogEvent(level, pos, EventType.BLOCK_UPDATE);
	}

	public void logComparatorUpdate(Level level, BlockPos pos) {
		tryLogEvent(level, pos, EventType.COMPARATOR_UPDATE);
	}

	public void logShapeUpdate(Level level, BlockPos pos, Direction dir) {
		tryLogEvent(level, pos, EventType.SHAPE_UPDATE, dir.get3DDataValue());
	}

	public void logObserverUpdate(Level level, BlockPos pos) {
		tryLogEvent(level, pos, EventType.OBSERVER_UPDATE);
	}

	public void logInteractBlock(Level level, BlockPos pos) {
		tryLogEvent(level, pos, EventType.INTERACT_BLOCK);
	}

	private void tryLogEvent(Level level, BlockPos pos, EventType type) {
		tryLogEvent(level, pos, type, 0);
	}

	private void tryLogEvent(Level level, BlockPos pos, EventType type, int data) {
		tryLogEvent(level, pos, type, data, (meterGroup, meter, event) -> true);
	}

	private void tryLogEvent(Level level, BlockPos pos, EventType type, int data, MeterEventPredicate predicate) {
		tryLogEvent(level, pos, type, Suppliers.memoize(() -> data), predicate);
	}

	private void tryLogEvent(Level level, BlockPos pos, EventType type, Supplier<Integer> data, MeterEventPredicate predicate) {
		if (options.hasEventType(type)) {
			for (ServerMeterGroup meterGroup : activeMeterGroups) {
				meterGroup.tryLogEvent(level, pos, type, data, predicate);
			}
		}
	}

	static {

		NUMBER_FORMAT.setGroupingUsed(false);

	}
}
