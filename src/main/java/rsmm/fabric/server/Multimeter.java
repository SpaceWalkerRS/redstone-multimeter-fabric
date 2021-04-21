package rsmm.fabric.server;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import io.netty.buffer.Unpooled;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;

import rsmm.fabric.common.DimPos;
import rsmm.fabric.common.Meter;
import rsmm.fabric.common.event.EventType;
import rsmm.fabric.common.packet.types.AddMeterPacket;
import rsmm.fabric.common.packet.types.MeterGroupDataPacket;
import rsmm.fabric.common.packet.types.MeterLogsDataPacket;
import rsmm.fabric.common.packet.types.MeteredEventsPacket;
import rsmm.fabric.common.packet.types.RecolorMeterPacket;
import rsmm.fabric.common.packet.types.RemoveAllMetersPacket;
import rsmm.fabric.common.packet.types.RemoveMeterPacket;
import rsmm.fabric.common.packet.types.RenameMeterPacket;
import rsmm.fabric.interfaces.mixin.IBlock;

public class Multimeter {
	
	private final MultimeterServer server;
	private final Map<String, ServerMeterGroup> meterGroups;
	private final Map<ServerPlayerEntity, ServerMeterGroup> subscriptions;
	
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
	
	public void tick() {
		for (ServerMeterGroup meterGroup : meterGroups.values()) {
			meterGroup.getLogManager().resetSubTickCount();
		}
	}
	
	/**
	 * This is called at the end of every server tick,
	 * and sends all the logged events of the past tick
	 * to the clients.
	 */
	public void broadcastMeterLogs() {
		for (ServerMeterGroup meterGroup : meterGroups.values()) {
			ServerLogManager logManager = meterGroup.getLogManager();
			
			if (logManager.hasLogs()) {
				if (meterGroup.hasSubscribers()) {
					PacketByteBuf data = logManager.collectMeterLogs();
					MeterLogsDataPacket packet = new MeterLogsDataPacket(data);
					server.getPacketHandler().sendPacketToPlayers(packet, meterGroup.getSubscribers());
				}
				
				logManager.clearLogs();
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
	public void toggleMeter(DimPos dimPos, boolean movable, ServerPlayerEntity player) {
		ServerMeterGroup meterGroup = subscriptions.get(player);
		
		if (meterGroup == null) {
			return;
		}
		
		if (meterGroup.hasMeterAt(dimPos)) {
			Meter meter = meterGroup.getMeterAt(dimPos);
			meterGroup.removeMeter(meter);
			
			RemoveMeterPacket packet = new RemoveMeterPacket(dimPos);
			server.getPacketHandler().sendPacketToPlayers(packet, meterGroup.getSubscribers());
		} else {
			World world = server.getMinecraftServer().getWorld(DimensionType.byRawId(dimPos.getDimensionId()));
			BlockPos pos = dimPos.getBlockPos();
			
			if (world != null) {
				String name = meterGroup.getNextMeterName();
				int color = meterGroup.getNextMeterColor();
				
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
				
				Meter meter = new Meter(dimPos, name, color, movable, meteredEvents, powered, active);
				meterGroup.addMeter(meter);
				
				AddMeterPacket packet = new AddMeterPacket(dimPos, name, color, movable, meteredEvents, powered, active);
				server.getPacketHandler().sendPacketToPlayers(packet, meterGroup.getSubscribers());
			}
		}
	}
	
	public void renameMeter(int index, String name, ServerPlayerEntity player) {
		ServerMeterGroup meterGroup = subscriptions.get(player);
		
		if (meterGroup != null) {
			Meter meter = meterGroup.getMeter(index);
			
			if (meter != null) {
				meter.setName(name);
				
				RenameMeterPacket packet = new RenameMeterPacket(index, name);
				server.getPacketHandler().sendPacketToPlayers(packet, meterGroup.getSubscribers());
			}
		}
	}
	
	public void recolorMeter(int index, int color, ServerPlayerEntity player) {
		ServerMeterGroup meterGroup = subscriptions.get(player);
		
		if (meterGroup != null) {
			Meter meter = meterGroup.getMeter(index);
			
			if (meter != null) {
				meter.setColor(color);
				
				RecolorMeterPacket packet = new RecolorMeterPacket(index, color);
				server.getPacketHandler().sendPacketToPlayers(packet, meterGroup.getSubscribers());
			}
		}
	}
	
	public void updateMeteredEvents(int index, EventType type, boolean start, ServerPlayerEntity player) {
		ServerMeterGroup meterGroup = subscriptions.get(player);
		
		if (meterGroup != null) {
			Meter meter = meterGroup.getMeter(index);
			
			if (meter != null) {
				if (start) {
					meter.startMetering(type);
				} else {
					meter.stopMetering(type);
				}
				
				MeteredEventsPacket packet = new MeteredEventsPacket(index, type, start);
				server.getPacketHandler().sendPacketToPlayers(packet, meterGroup.getSubscribers());
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
		ServerMeterGroup prevSubscription = subscriptions.remove(player);
		
		if (prevSubscription != null) {
			prevSubscription.removeSubscriber(player);
		}
		
		ServerMeterGroup newSubscription = meterGroups.get(name);
		
		if (newSubscription == null) {
			newSubscription = new ServerMeterGroup(this, name);
			
			meterGroups.put(name, newSubscription);
		}
		
		subscriptions.put(player, newSubscription);
		newSubscription.addSubscriber(player);
		
		PacketByteBuf data = new PacketByteBuf(Unpooled.buffer());
		newSubscription.encode(data);
		
		MeterGroupDataPacket packet = new MeterGroupDataPacket(name, data);
		server.getPacketHandler().sendPacketToPlayer(packet, player);
	}
	
	public void meterGroupDataReceived(String name, PacketByteBuf data, ServerPlayerEntity player) {
		ServerMeterGroup meterGroup = meterGroups.get(name);
		
		if (meterGroup == null) {
			meterGroup = new ServerMeterGroup(this, name);
			meterGroup.decode(data);
			
			meterGroups.put(name, meterGroup);
		} else {
			data = new PacketByteBuf(Unpooled.buffer());
			meterGroup.encode(data);
			
			MeterGroupDataPacket packet = new MeterGroupDataPacket(name, data);
			server.getPacketHandler().sendPacketToPlayer(packet, player);
		}
		
		subscriptions.put(player, meterGroup);
		meterGroup.addSubscriber(player);
	}
	
	public void blockUpdate(DimPos pos, boolean powered) {
		for (ServerMeterGroup meterGroup : meterGroups.values()) {
			meterGroup.blockUpdate(pos, powered);
		}
	}
	
	public void blockUpdate(World world, BlockPos pos, boolean powered) {
		blockUpdate(new DimPos(world, pos), powered);
	}
	
	public void stateChanged(DimPos pos, boolean active) {
		for (ServerMeterGroup meterGroup : meterGroups.values()) {
			meterGroup.stateChanged(pos, active);
		}
	}
	
	public void stateChanged(World world, BlockPos pos, boolean active) {
		stateChanged(new DimPos(world, pos), active);
	}
	
	public void blockMoved(DimPos pos, Direction dir) {
		for (ServerMeterGroup meterGroup : meterGroups.values()) {
			meterGroup.blockMoved(pos, dir);
		}
	}
	
	public void blockMoved(World world, BlockPos pos, Direction dir) {
		blockMoved(new DimPos(world, pos), dir);
	}
}
