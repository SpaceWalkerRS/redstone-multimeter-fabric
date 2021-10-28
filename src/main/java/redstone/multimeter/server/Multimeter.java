package redstone.multimeter.server;

import java.text.NumberFormat;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.function.BiPredicate;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.BlockEvent;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.ScheduledTick;
import net.minecraft.world.World;

import redstone.multimeter.block.Meterable;
import redstone.multimeter.common.WorldPos;
import redstone.multimeter.common.meter.Meter;
import redstone.multimeter.common.meter.MeterGroup;
import redstone.multimeter.common.meter.MeterProperties;
import redstone.multimeter.common.meter.event.EventType;
import redstone.multimeter.common.meter.event.MeterEvent;
import redstone.multimeter.common.network.packets.MeterGroupRefreshPacket;
import redstone.multimeter.common.network.packets.MeterGroupSubscriptionPacket;
import redstone.multimeter.common.network.packets.ClearMeterGroupPacket;
import redstone.multimeter.common.network.packets.MeterGroupDefaultPacket;
import redstone.multimeter.interfaces.mixin.IBlock;
import redstone.multimeter.server.meter.ServerMeterGroup;
import redstone.multimeter.server.meter.ServerMeterPropertiesManager;

public class Multimeter {
	
	private final MultimeterServer server;
	private final Map<String, ServerMeterGroup> meterGroups;
	private final Map<UUID, ServerMeterGroup> subscriptions;
	private final ServerMeterPropertiesManager meterPropertiesManager;
	
	public Multimeter(MultimeterServer server) {
		this.server = server;
		this.meterGroups = new LinkedHashMap<>();
		this.subscriptions = new HashMap<>();
		this.meterPropertiesManager = new ServerMeterPropertiesManager(this);
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
	
	public void tickStart(boolean paused) {
		if (!paused) {
			meterGroups.values().removeIf(meterGroup -> {
				return meterGroup.isIdle() && (!meterGroup.hasMembers() || meterGroup.getIdleTime() > 72000);
			});
			
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
		
		if (meterGroup != null && !addMeter(meterGroup, properties)) {
			refreshMeterGroup(meterGroup, player);
		}
	}
	
	public boolean addMeter(ServerMeterGroup meterGroup, MeterProperties properties) {
		if (!meterPropertiesManager.validate(properties) || !meterGroup.addMeter(properties)) {
			return false;
		}
		
		WorldPos pos = properties.getPos();
		World world = server.getWorldOf(pos);
		BlockPos blockPos = pos.getBlockPos();
		BlockState state = world.getBlockState(blockPos);
		Block block = state.getBlock();
		
		boolean powered = ((IBlock)block).isPowered(world, blockPos, state);
		boolean active = ((IBlock)block).isMeterable() && ((Meterable)block).isActive(world, blockPos, state);
		
		meterGroup.tryLogEvent(pos, EventType.POWERED, powered ? 1 : 0, (_meterGroup, meter) -> {
			return meter.setPowered(powered);
		});
		meterGroup.tryLogEvent(pos, EventType.ACTIVE, active ? 1 : 0, (_meterGroup, meter) -> {
			return meter.setActive(active);
		});
		
		return true;
	}
	
	public void removeMeter(ServerPlayerEntity player, long id) {
		ServerMeterGroup meterGroup = getSubscription(player);
		
		if (meterGroup != null && !meterGroup.removeMeter(id)) {
			refreshMeterGroup(meterGroup, player);
		}
	}
	
	public void moveMeter(ServerPlayerEntity player, long id, WorldPos newPos) {
		ServerMeterGroup meterGroup = getSubscription(player);
		
		if (meterGroup != null) {
			moveMeter(meterGroup, id, newPos);
		}
	}
	
	public void moveMeter(ServerMeterGroup meterGroup, long id, WorldPos newPos) {
		World world = server.getWorldOf(newPos);
		
		if (world != null) {
			meterGroup.tryMoveMeter(id, newPos, false);
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
		
		if (prevSubscription != meterGroup) {
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
		server.getMinecraftServer().getPlayerManager().sendCommandTree(player);
	}
	
	public void clearMembersOfMeterGroup(ServerMeterGroup meterGroup) {
		for (UUID playerUUID : meterGroup.getMembers()) {
			removeMemberFromMeterGroup(meterGroup, playerUUID);
		}
	}
	
	public void addMemberToMeterGroup(ServerMeterGroup meterGroup, UUID playerUUID) {
		if (meterGroup.hasMember(playerUUID)) {
			return;
		}
		
		ServerPlayerEntity player = server.getPlayer(playerUUID);
		
		if (player == null || meterGroup.isOwnedBy(player)) {
			return;
		}
		
		meterGroup.addMember(playerUUID);
		
		Text message = new LiteralText("").
			append(new LiteralText(String.format("You have been invited to meter group \'%s\' - click ", meterGroup.getName()))).
			append(new LiteralText("[here]").styled(style -> {
				return style.
					withColor(Formatting.GREEN).
					withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new LiteralText(String.format("Subscribe to meter group \'%s\'", meterGroup.getName())))).
					withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, String.format("/metergroup subscribe %s", meterGroup.getName())));
			})).
			append(new LiteralText(" to subscribe to it."));
		player.sendMessage(message, false);
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
				player.sendMessage(message, false);
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
		ServerMeterGroup meterGroup = getSubscription(player);
		
		if (meterGroup != null) {
			Meter meter = meterGroup.getMeter(id);
			
			if (meter != null) {
				WorldPos pos = meter.getPos();
				ServerWorld newWorld = server.getWorldOf(pos);
				
				if (newWorld != null) {
					ServerWorld oldWorld = player.getServerWorld();
					double oldX = player.getX();
					double oldY = player.getY();
					double oldZ = player.getZ();
					
					BlockPos blockPos = pos.getBlockPos();
					
					double newX = blockPos.getX() + 0.5D;
					double newY = blockPos.getY();
					double newZ = blockPos.getZ() + 0.5D;
					float yaw = player.getYaw();
					float pitch = player.getPitch();
					
					player.teleport(newWorld, newX, newY, newZ, yaw, pitch);
					sendClickableReturnMessage(oldWorld, oldX, oldY, oldZ, yaw, pitch, player);
				}
			}
		}
	}
	
	/**
	 * Send the player a message they can click to return
	 * to the location they were at before teleporting to
	 * a meter.
	 */
	private void sendClickableReturnMessage(ServerWorld world, double _x, double _y, double _z, float _yaw, float _pitch, ServerPlayerEntity player) {
		NumberFormat f = NumberFormat.getNumberInstance(Locale.US); // use . as decimal separator
		
		String worldId = world.getRegistryKey().getValue().toString();
		String x = f.format(_x);
		String y = f.format(_y);
		String z = f.format(_z);
		String yaw = f.format(_yaw);
		String pitch = f.format(_pitch);
		
		Text message = new LiteralText("Click ").
			append(new LiteralText("[here]").styled((style) -> {
				return style.
					withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new LiteralText("Teleport to").
						append(MeterEvent.formatTextForTooltip("\n  dimension", worldId)).
						append(MeterEvent.formatTextForTooltip("\n  x", x)).
						append(MeterEvent.formatTextForTooltip("\n  y", y)).
						append(MeterEvent.formatTextForTooltip("\n  z", z)))).
					withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, String.format("/execute in %s run tp @s %s %s %s %s %s", worldId, x, y, z, yaw, pitch))).
					withColor(Formatting.GREEN);
			})).
			append(new LiteralText(" to return to your previous location"));
		
		player.sendMessage(message, false);
	}
	
	public void logPowered(World world, BlockPos pos, boolean powered) {
		tryLogEvent(world, pos, EventType.POWERED, powered ? 1 : 0, (meterGroup, meter) -> meter.setPowered(powered));
	}
	
	public void logActive(World world, BlockPos pos, boolean active) {
		tryLogEvent(world, pos, EventType.ACTIVE, active ? 1 : 0, (meterGroup, meter) -> meter.setActive(active));
	}
	
	public void logMoved(World world, BlockPos pos, Direction dir) {
		tryLogEvent(world, pos, EventType.MOVED, dir.getId(), (meterGroup, meter) -> {
			meterGroup.tryMoveMeter(meter.getId(), new WorldPos(world, pos.offset(dir)), true);
			return true;
		});
	}
	
	public void logPowerChange(World world, BlockPos pos, int oldPower, int newPower) {
		if (oldPower != newPower) {
			tryLogEvent(world, pos, EventType.POWER_CHANGE, (oldPower << 8) | newPower);
		}
	}
	
	public void logRandomTick(World world, BlockPos pos) {
		tryLogEvent(world, pos, EventType.RANDOM_TICK, 0);
	}
	
	public <T> void logScheduledTick(World world, ScheduledTick<T> scheduledTick) {
		tryLogEvent(world, scheduledTick.pos, EventType.SCHEDULED_TICK, scheduledTick.priority.getIndex());
	}
	
	public void logBlockEvent(World world, BlockEvent blockEvent) {
		tryLogEvent(world, blockEvent.getPos(), EventType.BLOCK_EVENT, blockEvent.getType());
	}
	
	public void logEntityTick(World world, Entity entity) {
		tryLogEvent(world, entity.getBlockPos(), EventType.ENTITY_TICK, 0);
	}
	
	public void logBlockEntityTick(World world, BlockEntity blockEntity) {
		tryLogEvent(world, blockEntity.getPos(), EventType.BLOCK_ENTITY_TICK, 0);
	}
	
	public void logBlockUpdate(World world, BlockPos pos) {
		tryLogEvent(world, pos, EventType.BLOCK_UPDATE, 0);
	}
	
	public void logComparatorUpdate(World world, BlockPos pos) {
		tryLogEvent(world, pos, EventType.COMPARATOR_UPDATE, 0);
	}
	
	public void logShapeUpdate(World world, BlockPos pos, Direction dir) {
		tryLogEvent(world, pos, EventType.SHAPE_UPDATE, dir.getId());
	}
	
	private void tryLogEvent(World world, BlockPos pos, EventType type, int metaData) {
		tryLogEvent(world, pos, type, metaData, (meterGroup, meter) -> true);
	}
	
	private void tryLogEvent(World world, BlockPos blockPos, EventType type, int metaData, BiPredicate<ServerMeterGroup, Meter> meterPredicate) {
		WorldPos pos = new WorldPos(world, blockPos);
		
		for (ServerMeterGroup meterGroup : meterGroups.values()) {
			if (meterGroup.isIdle()) {
				continue;
			}
			
			meterGroup.tryLogEvent(pos, type, metaData, meterPredicate);
		}
	}
}
