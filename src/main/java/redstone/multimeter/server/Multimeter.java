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

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;

import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.server.entity.living.player.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
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
import redstone.multimeter.util.Direction;

public class Multimeter {

	private static final NumberFormat NUMBER_FORMAT = NumberFormat.getNumberInstance(Locale.US);

	private final MultimeterServer server;
	private final Map<String, ServerMeterGroup> meterGroups;
	private final Map<String, ServerMeterGroup> subscriptions;
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
		return subscriptions.get(player.getDisplayName());
	}

	public boolean hasSubscription(ServerPlayerEntity player) {
		return subscriptions.containsKey(player.getDisplayName());
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
		String ownerName = meterGroup.getOwner();
		ServerPlayerEntity owner = server.getPlayerList().get(ownerName);

		if (owner != null) {
			String message = String.format("One of your meter groups, \'%s\', was idle for more than %d ticks and has been removed.", meterGroup.getName(), options.meter_group.max_idle_time);
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
				String message = String.format("meter limit (%d) reached!", options.meter_group.meter_limit);
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
		int x = pos.getX();
		int y = pos.getY();
		int z = pos.getZ();
		int block = world.getBlock(x, y, z);
		int metadata = world.getBlockMetadata(x, y, z);

		logPowered(world, x, y, z, block, metadata);
		logActive(world, x, y, z, block, metadata);

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
				String message = String.format("Could not set meters for meter group \"%s\": you are not the owner of that meter group!", meterGroup.getName());
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
		String playerName = player.getDisplayName();

		subscriptions.put(playerName, meterGroup);
		meterGroup.addSubscriber(playerName);

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
		String playerName = player.getDisplayName();

		subscriptions.remove(playerName, meterGroup);
		meterGroup.removeSubscriber(playerName);

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
	}

	public void clearMembersOfMeterGroup(ServerMeterGroup meterGroup) {
		for (String playerName : meterGroup.getMembers()) {
			removeMemberFromMeterGroup(meterGroup, playerName);
		}
	}

	public void addMemberToMeterGroup(ServerMeterGroup meterGroup, String playerName) {
		if (meterGroup.hasMember(playerName) || meterGroup.isOwnedBy(playerName)) {
			return;
		}

		ServerPlayerEntity player = server.getPlayerList().get(playerName);

		if (player == null) {
			return;
		}

		meterGroup.addMember(playerName);

		String message = String.format("You have been invited to meter group \'%s\' - run \'/metergroup subscribe %s\' to subscribe to it.", meterGroup.getName(), meterGroup.getName());
		server.sendMessage(player, message, false);
	}

	public void removeMemberFromMeterGroup(ServerMeterGroup meterGroup, String playerName) {
		if (!meterGroup.hasMember(playerName)) {
			return;
		}

		meterGroup.removeMember(playerName);

		if (meterGroup.isPrivate()) {
			ServerPlayerEntity player = server.getPlayerList().get(playerName);

			if (player != null && meterGroup.hasSubscriber(playerName)) {
				unsubscribeFromMeterGroup(meterGroup, player);

				String message = String.format("The owner of meter group \'%s\' has removed you as a member!", meterGroup.getName());
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
			String message = "This server does not allow meter teleporting!";
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
					int blockX = pos.getX();
					int blockY = pos.getY();
					int blockZ = pos.getZ();

					double newX = blockX + 0.5D;
					double newY = blockY;
					double newZ = blockZ + 0.5D;
					float newYaw = player.yaw;
					float newPitch = player.pitch;

					player.teleportToDimension(newWorld.dimension.id);
					player.networkHandler.teleport(newX, newY, newZ, newYaw, newPitch);

					String text = String.format("Teleported to meter \"%s\"", meter.getName());
					server.sendMessage(player, text, false);
				}
			}
		}
	}


	public void onBlockChange(World world, int x, int y, int z, int oldBlock, int oldMetadata, int newBlock, int newMetadata) {
		if (oldBlock != 0 && Block.BY_ID[oldBlock].is(newBlock)) {
			if (((IBlock)Block.BY_ID[newBlock]).rsmm$isPowerSource() && ((PowerSource)Block.BY_ID[newBlock]).rsmm$logPowerChangeOnStateChange()) {
				logPowerChange(world, x, y, z, oldBlock, oldMetadata, newBlock, newMetadata);
			}

			logDataChange(world, x, y, z, oldMetadata, newMetadata);
		}

		boolean wasMeterable = oldBlock != 0 && ((IBlock)Block.BY_ID[oldBlock]).rsmm$isMeterable();
		boolean isMeterable = newBlock != 0 && ((IBlock)Block.BY_ID[newBlock]).rsmm$isMeterable();

		if (wasMeterable || isMeterable) {
			logActive(world, x, y, z, newBlock, newMetadata);
		}
	}

	public void logPowered(World world, int x, int y, int z, boolean powered) {
		tryLogEvent(world, x, y, z, EventType.POWERED, powered ? 1 : 0, (meterGroup, meter, event) -> meter.setPowered(powered));
	}

	public void logPowered(World world, int x, int y, int z, int block, int metadata) {
		tryLogEvent(world, x, y, z, EventType.POWERED, () -> {
			return ((IBlock)Block.BY_ID[block]).rsmm$isPowered(world, x, y, z, metadata) ? 1 : 0;
		}, (meterGroup, meter, event) -> {
			return meter.setPowered(event.getMetadata() != 0);
		});
	}

	public void logActive(World world, int x, int y, int z, boolean active) {
		tryLogEvent(world, x, y, z, EventType.ACTIVE, active ? 1 : 0, (meterGroup, meter, event) -> meter.setActive(active));
	}

	public void logActive(World world, int x, int y, int z, int block, int metadata) {
		tryLogEvent(world, x, y, z, EventType.ACTIVE, () -> {
			return ((IBlock)Block.BY_ID[block]).rsmm$isMeterable() && ((Meterable)Block.BY_ID[block]).rsmm$isActive(world, x, y, z, metadata) ? 1 : 0;
		}, (meterGroup, meter, event) -> meter.setActive(event.getMetadata() != 0));
	}

	public void logMoved(World world, int x, int y, int z, Direction dir) {
		tryLogEvent(world, x, y, z, EventType.MOVED, dir.getIndex());
	}

	public void moveMeters(World world, int x, int y, int z, Direction dir) {
		DimPos pos = new DimPos(world, x, y, z);

		for (ServerMeterGroup meterGroup : activeMeterGroups) {
			meterGroup.tryMoveMeter(pos, dir);
		}
	}

	public void logPowerChange(World world, int x, int y, int z, int oldPower, int newPower) {
		if (oldPower != newPower) {
			tryLogEvent(world, x, y, z, EventType.POWER_CHANGE, (oldPower << 8) | newPower);
		}
	}

	public void logPowerChange(World world, int x, int y, int z, int oldBlock, int oldMetadata, int newBlock, int newMetadata) {
		tryLogEvent(world, x, y, z, EventType.POWER_CHANGE, () -> {
			int oldPower = (oldBlock == 0) ? 0 : ((PowerSource)Block.BY_ID[oldBlock]).rsmm$getPowerLevel(world, x, y, z, oldMetadata);
			int newPower = (newBlock == 0) ? 0 : ((PowerSource)Block.BY_ID[newBlock]).rsmm$getPowerLevel(world, x, y, z, newMetadata);

			return (oldPower << 8) | newPower;
		}, (meterGroup, meter, event) -> {
			int data = event.getMetadata();
			int oldPower = (data >> 8) & 0xFF;
			int newPower = data & 0xFF;

			return oldPower != newPower;
		});
	}

	public void logRandomTick(World world, int x, int y, int z) {
		tryLogEvent(world, x, y, z, EventType.RANDOM_TICK);
	}

	public void logScheduledTick(World world, int x, int y, int z, int priority, boolean scheduling) {
		tryLogEvent(world, x, y, z, EventType.SCHEDULED_TICK, (scheduling ? (1 << 30) : 0) | (priority + 3));
	}

	public void logBlockEvent(World world, int x, int y, int z, int type, int depth, boolean queueing) {
		tryLogEvent(world, x, y, z, EventType.BLOCK_EVENT, (queueing ? (1 << 30) : 0) | (depth << 4) | type);
	}

	public void logEntityTick(World world, Entity entity) {
		tryLogEvent(world, (int)entity.x, (int)entity.y, (int)entity.z, EventType.ENTITY_TICK);
	}

	public void logBlockEntityTick(World world, BlockEntity blockEntity) {
		tryLogEvent(world, blockEntity.x, blockEntity.y, blockEntity.z, EventType.BLOCK_ENTITY_TICK);
	}

	public void logBlockUpdate(World world, int x, int y, int z) {
		tryLogEvent(world, x, y, z, EventType.BLOCK_UPDATE);
	}

	public void logComparatorUpdate(World world, int x, int y, int z) {
		tryLogEvent(world, x, y, z, EventType.COMPARATOR_UPDATE);
	}

	public void logShapeUpdate(World world, int x, int y, int z, Direction dir) {
		tryLogEvent(world, x, y, z, EventType.SHAPE_UPDATE, dir.getIndex());
	}

	public void logObserverUpdate(World world, int x, int y, int z) {
		tryLogEvent(world, x, y, z, EventType.OBSERVER_UPDATE);
	}

	public void logInteractBlock(World world, int x, int y, int z) {
		tryLogEvent(world, x, y, z, EventType.INTERACT_BLOCK);
	}

	public void logDataChange(World world, int x, int y, int z, int oldMetadata, int newMetadata) {
		tryLogEvent(world, x, y, z, EventType.BLOCK_DATA_CHANGE, (oldMetadata << 8) | newMetadata, (meterGroup, meter, event) -> {
			int data = event.getMetadata();
			int oldData = (data >> 8) & 0xFF;
			int newData = data & 0xFF;

			return oldData != newData;
		});
	}

	private void tryLogEvent(World world, int x, int y, int z, EventType type) {
		tryLogEvent(world, x, y, z, type, 0);
	}

	private void tryLogEvent(World world, int x, int y, int z, EventType type, int data) {
		tryLogEvent(world, x, y, z, type, data, (meterGroup, meter, event) -> true);
	}

	private void tryLogEvent(World world, int x, int y, int z, EventType type, int data, MeterEventPredicate predicate) {
		tryLogEvent(world, x, y, z, type, Suppliers.memoize(() -> data), predicate);
	}

	private void tryLogEvent(World world, int x, int y, int z, EventType type, Supplier<Integer> data, MeterEventPredicate predicate) {
		if (options.hasEventType(type)) {
			for (ServerMeterGroup meterGroup : activeMeterGroups) {
				meterGroup.tryLogEvent(world, x, y, z, type, data, predicate);
			}
		}
	}

	static {

		NUMBER_FORMAT.setGroupingUsed(false);

	}
}
