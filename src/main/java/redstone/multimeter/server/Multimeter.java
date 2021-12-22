package redstone.multimeter.server;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.BlockAction;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.TickableEntry;
import net.minecraft.world.World;

import redstone.multimeter.block.Meterable;
import redstone.multimeter.block.PowerSource;
import redstone.multimeter.common.DimPos;
import redstone.multimeter.common.meter.Meter;
import redstone.multimeter.common.meter.MeterGroup;
import redstone.multimeter.common.meter.MeterProperties;
import redstone.multimeter.common.meter.event.EventType;
import redstone.multimeter.common.network.packets.ClearMeterGroupPacket;
import redstone.multimeter.common.network.packets.MeterGroupDefaultPacket;
import redstone.multimeter.common.network.packets.MeterGroupRefreshPacket;
import redstone.multimeter.common.network.packets.MeterGroupSubscriptionPacket;
import redstone.multimeter.interfaces.mixin.IBlock;
import redstone.multimeter.server.meter.ServerMeterGroup;
import redstone.multimeter.server.meter.ServerMeterPropertiesManager;
import redstone.multimeter.server.meter.event.MeterEventPredicate;
import redstone.multimeter.server.meter.event.MeterEventSupplier;
import redstone.multimeter.server.option.Options;
import redstone.multimeter.server.option.OptionsManager;
import redstone.multimeter.util.Direction;

public class Multimeter {
	
	private final MultimeterServer server;
	private final Map<String, ServerMeterGroup> meterGroups;
	private final Map<UUID, ServerMeterGroup> subscriptions;
	private final ServerMeterPropertiesManager meterPropertiesManager;
	
	public Options options;
	
	public Multimeter(MultimeterServer server) {
		this.server = server;
		this.meterGroups = new LinkedHashMap<>();
		this.subscriptions = new HashMap<>();
		this.meterPropertiesManager = new ServerMeterPropertiesManager(this);
		
		reloadOptions();
	}
	
	public MultimeterServer getMultimeterServer() {
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
			options = OptionsManager.load(server.getConfigFolder());
		} else {
			options = new Options();
		}
	}
	
	public void tickStart(boolean paused) {
		if (!paused) {
			if (options.meter_group.max_idle_time >= 0) {
				meterGroups.values().removeIf(meterGroup -> {
					return meterGroup.isIdle() && (!meterGroup.hasMeters() || meterGroup.getIdleTime() > options.meter_group.max_idle_time);
				});
			}
			
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
	
	private void broadcastMeterUpdates() {
		for (ServerMeterGroup meterGroup : meterGroups.values()) {
			meterGroup.flushUpdates();
		}
	}
	
	private void broadcastMeterLogs() {
		for (ServerMeterGroup meterGroup : meterGroups.values()) {
			meterGroup.getLogManager().flushLogs();
		}
	}
	
	public void onPlayerJoin(ServerPlayerEntity player) {
		
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
	
	public boolean addMeter(ServerMeterGroup meterGroup, MeterProperties properties) {
		if (!meterPropertiesManager.validate(properties) || !meterGroup.addMeter(properties)) {
			return false;
		}
		
		DimPos pos = properties.getPos();
		World world = server.getWorldOf(pos);
		int x = pos.getX();
		int y = pos.getY();
		int z = pos.getZ();
		
		Block block = world.method_3774(x, y, z);
		int metadata = world.method_3777(x, y, z);
		
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
	
	public void clearMeterGroup(ServerPlayerEntity player) {
		ServerMeterGroup meterGroup = getSubscription(player);
		
		if (meterGroup != null) {
			meterGroup.clear();
			
			ClearMeterGroupPacket packet = new ClearMeterGroupPacket();
			server.getPacketHandler().sendToSubscribers(packet, meterGroup);
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
		server.getPacketHandler().sendToPlayer(packet, player);
	}
	
	private void addSubscriberToMeterGroup(ServerMeterGroup meterGroup, ServerPlayerEntity player) {
		UUID playerUUID = player.getUuid();
		
		subscriptions.put(playerUUID, meterGroup);
		meterGroup.addSubscriber(playerUUID);
		meterGroup.updateIdleState();
	}
	
	public void unsubscribeFromMeterGroup(ServerMeterGroup meterGroup, ServerPlayerEntity player) {
		if (meterGroup.hasSubscriber(player)) {
			removeSubscriberFromMeterGroup(meterGroup, player);
			onSubscriptionChanged(player, meterGroup, null);
		}
	}
	
	private void removeSubscriberFromMeterGroup(ServerMeterGroup meterGroup, ServerPlayerEntity player) {
		UUID playerUUID = player.getUuid();
		
		subscriptions.remove(playerUUID, meterGroup);
		meterGroup.removeSubscriber(playerUUID);
		meterGroup.updateIdleState();
	}
	
	private void onSubscriptionChanged(ServerPlayerEntity player, ServerMeterGroup prevSubscription, ServerMeterGroup newSubscription) {
		MeterGroupSubscriptionPacket packet;
		
		if (newSubscription == null) {
			packet = new MeterGroupSubscriptionPacket(prevSubscription.getName(), false);
		} else {
			packet = new MeterGroupSubscriptionPacket(newSubscription.getName(), true);
		}
		
		server.getPacketHandler().sendToPlayer(packet, player);
	}
	
	public void clearMembersOfMeterGroup(ServerMeterGroup meterGroup) {
		for (UUID playerUUID : meterGroup.getMembers()) {
			removeMemberFromMeterGroup(meterGroup, playerUUID);
		}
	}
	
	public void addMemberToMeterGroup(ServerMeterGroup meterGroup, UUID playerUUID) {
		if (meterGroup.hasMember(playerUUID) || meterGroup.isOwnedBy(playerUUID)) {
			return;
		}
		
		ServerPlayerEntity player = server.getPlayer(playerUUID);
		
		if (player == null) {
			return;
		}
		
		meterGroup.addMember(playerUUID);
		
		Text message = new LiteralText(String.format("You have been invited to meter group \'%s\' ", meterGroup.getName()));
		server.sendMessage(player, message, false);
	}
	
	public void removeMemberFromMeterGroup(ServerMeterGroup meterGroup, UUID playerUUID) {
		if (!meterGroup.hasMember(playerUUID)) {
			return;
		}
		
		meterGroup.removeMember(playerUUID);
		
		if (meterGroup.isPrivate()) {
			ServerPlayerEntity player = server.getPlayer(playerUUID);
			
			if (player != null && meterGroup.hasSubscriber(playerUUID)) {
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
		server.getPacketHandler().sendToPlayer(packet, player);
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
				ServerWorld newWorld = server.getWorldOf(pos);
				
				if (newWorld != null) {
					double newX = pos.getX() + 0.5D;
					double newY = pos.getY();
					double newZ = pos.getZ() + 0.5D;
					float yaw = player.yaw;
					float pitch = player.pitch;
					
					if (newWorld != player.world) {
						player.teleportToDimension(newWorld.dimension.dimensionType);
					}
					player.networkHandler.requestTeleport(newX, newY, newZ, yaw, pitch);
				}
			}
		}
	}
	
	public void onBlockChange(World world, int x, int y, int z, Block oldBlock, int oldMetadata, Block newBlock, int newMetadata) {
		if (oldBlock == newBlock && ((IBlock)newBlock).isPowerSource() && ((PowerSource)newBlock).logPowerChangeOnStateChange()) {
			logPowerChange(world, x, y, z, oldBlock, oldMetadata, newBlock, newMetadata);
		}
		
		boolean wasMeterable = ((IBlock)oldBlock).isMeterable();
		boolean isMeterable = ((IBlock)newBlock).isMeterable();
		
		if (wasMeterable || isMeterable) {
			logActive(world, x, y, z, newBlock, newMetadata);
		}
	}
	
	public void logPowered(World world, int x, int y, int z, boolean powered) {
		tryLogEvent(world, x, y, z, EventType.POWERED, powered ? 1 : 0, (meterGroup, meter, event) -> meter.setPowered(powered));
	}
	
	public void logPowered(World world, int x, int y, int z, Block block, int meta) {
		tryLogEvent(world, x, y, z, (meterGroup, meter, event) -> meter.setPowered(event.getMetadata() != 0), new MeterEventSupplier(EventType.POWERED, () -> {
			return ((IBlock)block).isPowered(world, x, y, z, meta) ? 1 : 0;
		}));
	}
	
	public void logActive(World world, int x, int y, int z, boolean active) {
		tryLogEvent(world, x, y, z, EventType.ACTIVE, active ? 1 : 0, (meterGroup, meter, event) -> meter.setActive(active));
	}
	
	public void logActive(World world, int x, int y, int z, Block block, int meta) {
		tryLogEvent(world, x, y, z, (meterGroup, meter, event) -> meter.setActive(event.getMetadata() != 0), new MeterEventSupplier(EventType.ACTIVE, () -> {
			return ((IBlock)block).isMeterable() && ((Meterable)block).isActive(world, x, y, z, meta) ? 1 : 0;
		}));
	}
	
	public void logMoved(World world, int x, int y, int z, Direction dir) {
		tryLogEvent(world, x, y, z, EventType.MOVED, dir.getIndex());
	}
	
	public void moveMeters(World world, int x, int y, int z, Direction dir) {
		DimPos pos = new DimPos(world, x, y, z);
		
		for (ServerMeterGroup meterGroup : meterGroups.values()) {
			meterGroup.tryMoveMeter(pos, dir);
		}
	}
	
	public void logPowerChange(World world, int x, int y, int z, int oldPower, int newPower) {
		if (oldPower != newPower) {
			tryLogEvent(world, x, y, z, EventType.POWER_CHANGE, (oldPower << 8) | newPower);
		}
	}
	
	public void logPowerChange(World world, int x, int y, int z, Block oldBlock, int oldMeta, Block newBlock, int newMeta) {
		tryLogEvent(world, x, y, z, (meterGroup, meter, event) -> {
			int data = event.getMetadata();
			int oldPower = (data >> 8) & 0xFF;
			int newPower =  data       & 0xFF;
			
			return oldPower != newPower;
		}, new MeterEventSupplier(EventType.POWER_CHANGE, () -> {
			PowerSource block = (PowerSource)newBlock;
			int oldPower = block.getPowerLevel(world, x, y, z, oldMeta);
			int newPower = block.getPowerLevel(world, x, y, z, newMeta);
			
			return (oldPower << 8) | newPower;
		}));
	}
	
	public void logRandomTick(World world, int x, int y, int z) {
		tryLogEvent(world, x, y, z, EventType.RANDOM_TICK, 0);
	}
	
	public void logScheduledTick(World world, TickableEntry scheduledTick) {
		tryLogEvent(world, scheduledTick.field_4600, scheduledTick.field_4601, scheduledTick.field_4602, EventType.SCHEDULED_TICK, scheduledTick.priority);
	}
	
	public void logBlockEvent(World world, BlockAction blockEvent) {
		tryLogEvent(world, blockEvent.method_3810(), blockEvent.method_3811(), blockEvent.method_3812(), EventType.BLOCK_EVENT, blockEvent.getType());
	}
	
	public void logEntityTick(World world, Entity entity) {
		tryLogEvent(world, (int)entity.x, (int)entity.y, (int)entity.z, EventType.ENTITY_TICK, 0);
	}
	
	public void logBlockEntityTick(World world, BlockEntity blockEntity) {
		tryLogEvent(world, blockEntity.field_566, blockEntity.field_567, blockEntity.field_568, EventType.BLOCK_ENTITY_TICK, 0);
	}
	
	public void logBlockUpdate(World world, int x, int y, int z) {
		tryLogEvent(world, x, y, z, EventType.BLOCK_UPDATE, 0);
	}
	
	public void logComparatorUpdate(World world, int x, int y, int z) {
		tryLogEvent(world, x, y, z, EventType.COMPARATOR_UPDATE, 0);
	}
	
	public void logShapeUpdate(World world, int x, int y, int z, Direction dir) {
		tryLogEvent(world, x, y, z, EventType.SHAPE_UPDATE, dir.getIndex());
	}
	
	public void logObserverUpdate(World world, int x, int y, int z) {
		tryLogEvent(world, x, y, z, EventType.OBSERVER_UPDATE, 0);
	}
	
	public void logInteractBlock(World world, int x, int y, int z) {
		tryLogEvent(world, x, y, z, EventType.INTERACT_BLOCK, 0);
	}
	
	private void tryLogEvent(World world, int x, int y, int z, EventType type, int data) {
		tryLogEvent(world, x, y, z, type, data, (meterGroup, meter, event) -> true);
	}
	
	private void tryLogEvent(World world, int x, int y, int z, EventType type, int data, MeterEventPredicate predicate) {
		tryLogEvent(world, x, y, z, predicate, new MeterEventSupplier(type, () -> data));
	}
	
	private void tryLogEvent(World world, int x, int y, int z, MeterEventPredicate predicate, MeterEventSupplier supplier) {
		if (options.hasEventType(supplier.type())) {
			DimPos pos = new DimPos(world, x, y, z);
			
			for (ServerMeterGroup meterGroup : meterGroups.values()) {
				if (!meterGroup.isIdle()) {
					meterGroup.tryLogEvent(pos, predicate, supplier);
				}
			}
		}
	}
}
