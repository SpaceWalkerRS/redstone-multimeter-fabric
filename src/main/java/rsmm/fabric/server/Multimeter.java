package rsmm.fabric.server;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;

import rsmm.fabric.common.Meter;
import rsmm.fabric.common.TickPhase;
import rsmm.fabric.common.WorldPos;
import rsmm.fabric.common.event.EventType;
import rsmm.fabric.common.packet.types.AddMeterPacket;
import rsmm.fabric.common.packet.types.MeterChangesPacket;
import rsmm.fabric.common.packet.types.MeterGroupDataPacket;
import rsmm.fabric.common.packet.types.MeterLogsDataPacket;
import rsmm.fabric.common.packet.types.RemoveMeterPacket;
import rsmm.fabric.common.packet.types.RemoveAllMetersPacket;
import rsmm.fabric.interfaces.mixin.IBlock;
import rsmm.fabric.util.ColorUtils;
import rsmm.fabric.util.NBTUtils;

public class Multimeter {
	
	private final MultimeterServer server;
	private final Map<String, ServerMeterGroup> meterGroups;
	private final Map<ServerPlayerEntity, ServerMeterGroup> subscriptions;
	
	private TickPhase currentTickPhase = TickPhase.UNKNOWN;
	
	public Multimeter(MultimeterServer server) {
		this.server = server;
		this.meterGroups = new LinkedHashMap<>();
		this.subscriptions = new HashMap<>();
	}
	
	public MultimeterServer getMultimeterServer() {
		return server;
	}
	
	public Collection<ServerMeterGroup> getMeterGroups() {
		return Collections.unmodifiableCollection(meterGroups.values());
	}
	
	public Set<String> getMeterGroupNames() {
		return Collections.unmodifiableSet(meterGroups.keySet());
	}
	
	public ServerMeterGroup getMeterGroup(String name) {
		return meterGroups.get(name);
	}
	
	public ServerMeterGroup getSubscription(ServerPlayerEntity player) {
		return subscriptions.get(player);
	}
	
	public boolean hasSubscription(ServerPlayerEntity player) {
		return subscriptions.containsKey(player);
	}

	public TickPhase getCurrentTickPhase() {
		return currentTickPhase;
	}
	
	public void onTickPhase(TickPhase phase) {
		currentTickPhase = phase;
	}
	
	public void tick() {
		for (ServerMeterGroup meterGroup : meterGroups.values()) {
			meterGroup.getLogManager().tick();
		}
	}
	
	/**
	 * This is called at the end of every server tick,
	 * and sends meter changes of the past tick to
	 * clients.
	 */
	public void broadcastMeterData() {
		for (ServerMeterGroup meterGroup : meterGroups.values()) {
			if (meterGroup.isDirty()) {
				if (meterGroup.hasSubscribers()) {
					CompoundTag data = meterGroup.collectMeterChanges();
					MeterChangesPacket packet = new MeterChangesPacket(data);
					server.getPacketHandler().sendPacketToPlayers(packet, meterGroup.getSubscribers());
				}
				
				meterGroup.cleanUp();
			}
		}
	}
	
	/**
	 * This is called at the end of every server tick,
	 * and sends all the logged events of the past tick
	 * to clients.
	 */
	public void broadcastMeterLogs() {
		for (ServerMeterGroup meterGroup : meterGroups.values()) {
			if (meterGroup.hasNewLogs()) {
				if (meterGroup.hasSubscribers()) {
					CompoundTag data = meterGroup.getLogManager().collectMeterLogs();
					MeterLogsDataPacket packet = new MeterLogsDataPacket(data);
					server.getPacketHandler().sendPacketToPlayers(packet, meterGroup.getSubscribers());
				}
				
				meterGroup.cleanLogs();
			}
		}
	}
	
	public void onPlayerJoin(ServerPlayerEntity player) {
		
	}
	
	public void onPlayerLeave(ServerPlayerEntity player) {
		ServerMeterGroup meterGroup = subscriptions.remove(player);
		
		if (meterGroup != null) {
			meterGroup.removeSubscriber(player);
		}
	}
	
	/**
	 * Add a meter at the position the player is looking at
	 * or remove it if there already is one.
	 */
	public void toggleMeter(CompoundTag properties, ServerPlayerEntity player) {
		ServerMeterGroup meterGroup = subscriptions.get(player);
		
		if (meterGroup == null) {
			return;
		}
		
		WorldPos pos = NBTUtils.tagToWorldPos(properties.getCompound("pos"));
		boolean movable = properties.getBoolean("movable");
		
		if (meterGroup.hasMeterAt(pos)) {
			removeMeter(meterGroup.indexOfMeterAt(pos), player);
		} else {
			addMeter(pos, movable, null, null, player);
		}
	}
	
	public void addMeter(WorldPos pos, boolean movable, String name, Integer color, ServerPlayerEntity player) {
		ServerMeterGroup meterGroup = subscriptions.get(player);
		
		if (meterGroup == null) {
			return;
		}
		
		World world = server.getMinecraftServer().getWorld(RegistryKey.of(Registry.DIMENSION, pos.getWorldId()));
		
		if (world != null) {
			if (name == null) {
				name = meterGroup.getNextMeterName();
			}
			
			int nextColor = ColorUtils.nextColor();
			
			if (color == null) {
				color = nextColor;
			}
			
			int meteredEvents = EventType.POWERED.flag() | EventType.MOVED.flag();
			boolean powered = false;
			boolean active = false;
			
			BlockState state = world.getBlockState(pos);
			Block block = state.getBlock();
			
			powered = ((IBlock)block).isPowered(world, pos, state);
			
			if (((IBlock)block).isMeterable()) {
				meteredEvents = ((Meterable)block).getDefaultMeteredEvents();
				active = ((Meterable)block).isActive(world, pos, state);
			}
			
			Meter meter = new Meter(pos, name, color, movable, meteredEvents, powered, active);
			meterGroup.addMeter(meter);
			
			AddMeterPacket packet = new AddMeterPacket(meter);
			server.getPacketHandler().sendPacketToPlayers(packet, meterGroup.getSubscribers());
		}
	}
	
	public void removeMeter(int index, ServerPlayerEntity player) {
		ServerMeterGroup meterGroup = subscriptions.get(player);
		
		if (meterGroup != null && meterGroup.removeMeter(index)) {
			RemoveMeterPacket packet = new RemoveMeterPacket(index);
			server.getPacketHandler().sendPacketToPlayers(packet, meterGroup.getSubscribers());
		}
	}
	
	public void moveMeter(int index, WorldPos pos, ServerPlayerEntity player) {
		ServerMeterGroup meterGroup = subscriptions.get(player);
		
		if (meterGroup != null) {
			moveMeter(index, pos, meterGroup);
		}
	}
	
	public void moveMeter(int index, WorldPos pos, ServerMeterGroup meterGroup) {
		World world = server.getMinecraftServer().getWorld(RegistryKey.of(Registry.DIMENSION, pos.getWorldId()));
		
		if (world != null) {
			meterGroup.moveMeter(index, pos);
		}
	}
	
	public void renameMeter(int index, String name, ServerPlayerEntity player) {
		changeMeter(index, meter -> meter.setName(name), player);
	}
	
	public void recolorMeter(int index, int color, ServerPlayerEntity player) {
		changeMeter(index, meter -> meter.setColor(color), player);
	}
	
	public void changeMeterMovability(int index, boolean movable, ServerPlayerEntity player) {
		changeMeter(index, meter -> meter.setIsMovable(movable), player);
	}
	
	public void toggleEventType(int index, EventType type, ServerPlayerEntity player) {
		changeMeter(index, meter -> meter.toggleEventType(type), player);
	}
	
	private void changeMeter(int index, Consumer<Meter> update, ServerPlayerEntity player) {
		ServerMeterGroup meterGroup = subscriptions.get(player);
		
		if (meterGroup != null) {
			Meter meter = meterGroup.getMeter(index);
			
			if (meter != null) {
				update.accept(meter);
				meter.markDirty();
			}
		}
	}
	
	public void removeAllMeters(ServerPlayerEntity player) {
		ServerMeterGroup meterGroup = subscriptions.get(player);
		
		if (meterGroup != null) {
			meterGroup.clear();
			
			RemoveAllMetersPacket packet = new RemoveAllMetersPacket();
			server.getPacketHandler().sendPacketToPlayers(packet, meterGroup.getSubscribers());
		}
	}
	
	public void subscribeToMeterGroup(String name, ServerPlayerEntity player) {
		ServerMeterGroup meterGroup = meterGroups.get(name);
		
		if (meterGroup == null) {
			meterGroup = new ServerMeterGroup(this, name);
			meterGroups.put(name, meterGroup);
		}
		
		subscribeToMeterGroup(meterGroup, player);
	}
	
	public void subscribeToMeterGroup(ServerMeterGroup meterGroup, ServerPlayerEntity player) {
		ServerMeterGroup prevSubscription = subscriptions.remove(player);
		
		if (prevSubscription != null) {
			prevSubscription.removeSubscriber(player);
		}
		
		subscriptions.put(player, meterGroup);
		meterGroup.addSubscriber(player);
		
		MeterGroupDataPacket packet = new MeterGroupDataPacket(meterGroup);
		server.getPacketHandler().sendPacketToPlayer(packet, player);
	}
	
	public void meterGroupDataReceived(String name, CompoundTag data, ServerPlayerEntity player) {
		ServerMeterGroup meterGroup = meterGroups.get(name);
		
		if (meterGroup == null) {
			meterGroup = new ServerMeterGroup(this, name);
			meterGroup.fromTag(data);
			
			meterGroups.put(name, meterGroup);
		}
		
		subscribeToMeterGroup(meterGroup, player);
	}
	
	public void blockUpdate(WorldPos pos, boolean powered) {
		for (ServerMeterGroup meterGroup : meterGroups.values()) {
			meterGroup.blockUpdate(pos, powered);
		}
	}
	
	public void blockUpdate(World world, BlockPos pos, boolean powered) {
		blockUpdate(new WorldPos(world, pos), powered);
	}
	
	public void stateChanged(WorldPos pos, boolean active) {
		for (ServerMeterGroup meterGroup : meterGroups.values()) {
			meterGroup.stateChanged(pos, active);
		}
	}
	
	public void stateChanged(World world, BlockPos pos, boolean active) {
		stateChanged(new WorldPos(world, pos), active);
	}
	
	public void blockMoved(WorldPos pos, Direction dir) {
		for (ServerMeterGroup meterGroup : meterGroups.values()) {
			meterGroup.blockMoved(pos, dir);
		}
	}
	
	public void blockMoved(World world, BlockPos pos, Direction dir) {
		blockMoved(new WorldPos(world, pos), dir);
	}
}
