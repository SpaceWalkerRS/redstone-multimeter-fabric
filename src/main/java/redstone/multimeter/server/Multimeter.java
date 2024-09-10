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

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;

import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.state.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.server.entity.living.player.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.Formatting;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import redstone.multimeter.block.Meterable;
import redstone.multimeter.block.PowerSource;
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

	public ServerMeterGroup getSubscription(ServerPlayerEntity player) {
		return subscriptions.get(player.getUuid());
	}

	public boolean hasSubscription(ServerPlayerEntity player) {
		return subscriptions.containsKey(player.getUuid());
	}

	public boolean isOwnerOfSubscription(ServerPlayerEntity player) {
		ServerMeterGroup meterGroup = getSubscription(player);
		return meterGroup != null && meterGroup.isOwnedBy(player);
	}

	public void reloadOptions() {
		if (server.isDedicated()) {
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
		ServerPlayerEntity owner = server.getPlayerList().get(ownerUuid);

		if (owner != null) {
			Text message = new LiteralText(String.format("One of your meter groups, \'%s\', was idle for more than %d ticks and has been removed.", meterGroup.getName(), options.meter_group.max_idle_time));
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

	public void onPlayerJoin(ServerPlayerEntity player) {
		server.refreshTickPhaseTree(player);
		server.getPlayerList().updatePermissions(player);
	}

	public void onPlayerLeave(ServerPlayerEntity player) {
		ServerMeterGroup meterGroup = getSubscription(player);

		if (meterGroup != null) {
			removeSubscriberFromMeterGroup(meterGroup, player);
		}
	}

	public void addMeter(ServerPlayerEntity player, MeterProperties properties) {
		ServerMeterGroup meterGroup = getSubscription(player);

		if (meterGroup != null) {
			if (meterGroup.isPastMeterLimit()) {
				Text message = new LiteralText(String.format("meter limit (%d) reached!", options.meter_group.meter_limit));
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
		World world = server.getWorld(pos);
		BlockPos blockPos = pos.getBlockPos();
		BlockState state = world.getBlockState(blockPos);

		logPowered(world, blockPos, state);
		logActive(world, blockPos, state);

		return true;
	}

	public void removeMeter(ServerPlayerEntity player, long id) {
		ServerMeterGroup meterGroup = getSubscription(player);

		if (meterGroup != null && !meterGroup.removeMeter(id)) {
			refreshMeterGroup(meterGroup, player);
		}
	}

	public void updateMeter(ServerPlayerEntity player, long id, MeterProperties newProperties) {
		ServerMeterGroup meterGroup = getSubscription(player);

		if (meterGroup != null && !meterGroup.updateMeter(id, newProperties)) {
			refreshMeterGroup(meterGroup, player);
		}
	}

	public void setMeterIndex(ServerPlayerEntity player, long id, int index) {
		ServerMeterGroup meterGroup = getSubscription(player);

		if (meterGroup != null && !meterGroup.setMeterIndex(id, index)) {
			refreshMeterGroup(meterGroup, player);
		}
	}

	public void clearMeterGroup(ServerPlayerEntity player) {
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

	public void setMeters(ServerPlayerEntity player, List<MeterProperties> meters) {
		ServerMeterGroup meterGroup = getSubscription(player);

		if (meterGroup != null) {
			if (meterGroup.isOwnedBy(player)) {
				setMeters(meterGroup, meters);
			} else {
				Text message = new LiteralText(String.format("Could not set meters for meter group \"%s\": you are not the owner of that meter group!", meterGroup.getName()));
				server.sendMessage(player, message, true);
			}
		}
	}

	public void setMeters(ServerMeterGroup meterGroup, List<MeterProperties> meters) {
		clearMeterGroup(meterGroup);

		for (MeterProperties meter : meters) {
			addMeter(meterGroup, meter);
		}
	}

	public void createMeterGroup(ServerPlayerEntity player, String name) {
		if (!MeterGroup.isValidName(name) || meterGroups.containsKey(name)) {
			return;
		}

		ServerMeterGroup meterGroup = new ServerMeterGroup(this, name, player);
		meterGroups.put(name, meterGroup);

		subscribeToMeterGroup(meterGroup, player);
	}

	public void subscribeToMeterGroup(ServerMeterGroup meterGroup, ServerPlayerEntity player) {
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

	public void subscribeToDefaultMeterGroup(ServerPlayerEntity player) {
		MeterGroupDefaultPacket packet = new MeterGroupDefaultPacket();
		server.getPlayerList().send(packet, player);
	}

	private void addSubscriberToMeterGroup(ServerMeterGroup meterGroup, ServerPlayerEntity player) {
		UUID playerUuid = player.getUuid();

		subscriptions.put(playerUuid, meterGroup);
		meterGroup.addSubscriber(playerUuid);

		if (meterGroup.updateIdleState()) {
			activeMeterGroups.add(meterGroup);
			idleMeterGroups.remove(meterGroup);
		}
	}

	public void unsubscribeFromMeterGroup(ServerPlayerEntity player) {
		ServerMeterGroup meterGroup = getSubscription(player);

		if (meterGroup != null) {
			unsubscribeFromMeterGroup(meterGroup, player);
		}
	}

	public void unsubscribeFromMeterGroup(ServerMeterGroup meterGroup, ServerPlayerEntity player) {
		if (meterGroup.hasSubscriber(player)) {
			removeSubscriberFromMeterGroup(meterGroup, player);
			onSubscriptionChanged(player, meterGroup, null);
		}
	}

	private void removeSubscriberFromMeterGroup(ServerMeterGroup meterGroup, ServerPlayerEntity player) {
		UUID playerUuid = player.getUuid();

		subscriptions.remove(playerUuid, meterGroup);
		meterGroup.removeSubscriber(playerUuid);

		if (meterGroup.updateIdleState()) {
			activeMeterGroups.remove(meterGroup);
			idleMeterGroups.add(meterGroup);
		}
	}

	private void onSubscriptionChanged(ServerPlayerEntity player, ServerMeterGroup prevSubscription, ServerMeterGroup newSubscription) {
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

		ServerPlayerEntity player = server.getPlayerList().get(playerUuid);

		if (player == null) {
			return;
		}

		meterGroup.addMember(playerUuid);

		Text message = new LiteralText("")
			.append(new LiteralText(String.format("You have been invited to meter group \'%s\' - click ", meterGroup.getName())))
			.append(new LiteralText("[here]").withStyle(style -> {
				style.setColor(Formatting.GREEN)
					.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
						new LiteralText(String.format("Subscribe to meter group \'%s\'", meterGroup.getName()))))
					.setClickEvent(
						new ClickEvent(ClickEvent.Action.RUN_COMMAND, String.format("/metergroup subscribe %s", meterGroup.getName())));
			})).append(new LiteralText(" to subscribe to it."));
		server.sendMessage(player, message, false);
	}

	public void removeMemberFromMeterGroup(ServerMeterGroup meterGroup, UUID playerUuid) {
		if (!meterGroup.hasMember(playerUuid)) {
			return;
		}

		meterGroup.removeMember(playerUuid);

		if (meterGroup.isPrivate()) {
			ServerPlayerEntity player = server.getPlayerList().get(playerUuid);

			if (player != null && meterGroup.hasSubscriber(playerUuid)) {
				unsubscribeFromMeterGroup(meterGroup, player);

				Text message = new LiteralText(String.format("The owner of meter group \'%s\' has removed you as a member!", meterGroup.getName()));
				server.sendMessage(player, message, false);
			}
		}
	}

	public void refreshMeterGroup(ServerPlayerEntity player) {
		ServerMeterGroup meterGroup = getSubscription(player);

		if (meterGroup != null) {
			refreshMeterGroup(meterGroup, player);
		}
	}

	private void refreshMeterGroup(ServerMeterGroup meterGroup, ServerPlayerEntity player) {
		MeterGroupRefreshPacket packet = new MeterGroupRefreshPacket(meterGroup);
		server.getPlayerList().send(packet, player);
	}

	public void teleportToMeter(ServerPlayerEntity player, long id) {
		if (!options.meter.allow_teleports) {
			Text message = new LiteralText("This server does not allow meter teleporting!");
			server.sendMessage(player, message, false);

			return;
		}

		ServerMeterGroup meterGroup = getSubscription(player);

		if (meterGroup != null) {
			Meter meter = meterGroup.getMeter(id);

			if (meter != null) {
				DimPos pos = meter.getPos();
				ServerWorld newWorld = server.getWorld(pos);

				if (newWorld != null) {
					BlockPos blockPos = pos.getBlockPos();

					double newX = blockPos.getX() + 0.5D;
					double newY = blockPos.getY();
					double newZ = blockPos.getZ() + 0.5D;
					float newYaw = player.yaw;
					float newPitch = player.pitch;

					player.teleportToDimension(newWorld.dimension.getType().getId());
					player.networkHandler.teleport(newX, newY, newZ, newYaw, newPitch);

					Text text = new LiteralText(String.format("Teleported to meter \"%s\"", meter.getName()));
					server.sendMessage(player, text, false);
				}
			}
		}
	}

	public void onBlockChange(World world, BlockPos pos, BlockState oldState, BlockState newState) {
		Block oldBlock = oldState.getBlock();
		Block newBlock = newState.getBlock();

		if (oldBlock == newBlock && ((IBlock)newBlock).rsmm$isPowerSource() && ((PowerSource)newBlock).rsmm$logPowerChangeOnStateChange()) {
			logPowerChange(world, pos, oldState, newState);
		}

		boolean wasMeterable = ((IBlock)oldBlock).rsmm$isMeterable();
		boolean isMeterable = ((IBlock)newBlock).rsmm$isMeterable();

		if (wasMeterable || isMeterable) {
			logActive(world, pos, newState);
		}
	}

	public void logPowered(World world, BlockPos pos, boolean powered) {
		tryLogEvent(world, pos, EventType.POWERED, powered ? 1 : 0, (meterGroup, meter, event) -> meter.setPowered(powered));
	}

	public void logPowered(World world, BlockPos pos, BlockState state) {
		tryLogEvent(world, pos, EventType.POWERED, () -> {
			return ((IBlock)state.getBlock()).rsmm$isPowered(world, pos, state) ? 1 : 0;
		}, (meterGroup, meter, event) -> {
			return meter.setPowered(event.getMetadata() != 0);
		});
	}

	public void logActive(World world, BlockPos pos, boolean active) {
		tryLogEvent(world, pos, EventType.ACTIVE, active ? 1 : 0, (meterGroup, meter, event) -> meter.setActive(active));
	}

	public void logActive(World world, BlockPos pos, BlockState state) {
		tryLogEvent(world, pos, EventType.ACTIVE, () -> {
			Block block = state.getBlock();
			return ((IBlock)block).rsmm$isMeterable() && ((Meterable)block).rsmm$isActive(world, pos, state) ? 1 : 0;
		}, (meterGroup, meter, event) -> meter.setActive(event.getMetadata() != 0));
	}

	public void logMoved(World world, BlockPos blockPos, Direction dir) {
		tryLogEvent(world, blockPos, EventType.MOVED, dir.getId());
	}

	public void moveMeters(World world, BlockPos blockPos, Direction dir) {
		DimPos pos = new DimPos(world, blockPos);

		for (ServerMeterGroup meterGroup : activeMeterGroups) {
			meterGroup.tryMoveMeter(pos, dir);
		}
	}

	public void logPowerChange(World world, BlockPos pos, int oldPower, int newPower) {
		if (oldPower != newPower) {
			tryLogEvent(world, pos, EventType.POWER_CHANGE, (oldPower << 8) | newPower);
		}
	}

	public void logPowerChange(World world, BlockPos pos, BlockState oldState, BlockState newState) {
		tryLogEvent(world, pos, EventType.POWER_CHANGE, () -> {
			PowerSource block = (PowerSource)newState.getBlock();
			int oldPower = block.rsmm$getPowerLevel(world, pos, oldState);
			int newPower = block.rsmm$getPowerLevel(world, pos, newState);

			return (oldPower << 8) | newPower;
		}, (meterGroup, meter, event) -> {
			int data = event.getMetadata();
			int oldPower = (data >> 8) & 0xFF;
			int newPower = data & 0xFF;

			return oldPower != newPower;
		});
	}

	public void logRandomTick(World world, BlockPos pos) {
		tryLogEvent(world, pos, EventType.RANDOM_TICK);
	}

	public void logScheduledTick(World world, BlockPos pos, int priority, boolean scheduling) {
		tryLogEvent(world, pos, EventType.SCHEDULED_TICK, (scheduling ? (1 << 30) : 0) | (priority + 3));
	}

	public void logBlockEvent(World world, BlockPos pos, int type, int depth, boolean queueing) {
		tryLogEvent(world, pos, EventType.BLOCK_EVENT, (queueing ? (1 << 30) : 0) | (depth << 4) | type);
	}

	public void logEntityTick(World world, Entity entity) {
		tryLogEvent(world, entity.getSourceBlockPos(), EventType.ENTITY_TICK);
	}

	public void logBlockEntityTick(World world, BlockEntity blockEntity) {
		tryLogEvent(world, blockEntity.getPos(), EventType.BLOCK_ENTITY_TICK);
	}

	public void logBlockUpdate(World world, BlockPos pos) {
		tryLogEvent(world, pos, EventType.BLOCK_UPDATE);
	}

	public void logComparatorUpdate(World world, BlockPos pos) {
		tryLogEvent(world, pos, EventType.COMPARATOR_UPDATE);
	}

	public void logShapeUpdate(World world, BlockPos pos, Direction dir) {
		tryLogEvent(world, pos, EventType.SHAPE_UPDATE, dir.getId());
	}

	public void logObserverUpdate(World world, BlockPos pos) {
		tryLogEvent(world, pos, EventType.OBSERVER_UPDATE);
	}

	public void logInteractBlock(World world, BlockPos pos) {
		tryLogEvent(world, pos, EventType.INTERACT_BLOCK);
	}

	private void tryLogEvent(World world, BlockPos pos, EventType type) {
		tryLogEvent(world, pos, type, 0);
	}

	private void tryLogEvent(World world, BlockPos pos, EventType type, int data) {
		tryLogEvent(world, pos, type, data, (meterGroup, meter, event) -> true);
	}

	private void tryLogEvent(World world, BlockPos pos, EventType type, int data, MeterEventPredicate predicate) {
		tryLogEvent(world, pos, type, Suppliers.memoize(() -> data), predicate);
	}

	private void tryLogEvent(World world, BlockPos pos, EventType type, Supplier<Integer> data, MeterEventPredicate predicate) {
		if (options.hasEventType(type)) {
			for (ServerMeterGroup meterGroup : activeMeterGroups) {
				meterGroup.tryLogEvent(world, pos, type, data, predicate);
			}
		}
	}

	static {

		NUMBER_FORMAT.setGroupingUsed(false);

	}
}
