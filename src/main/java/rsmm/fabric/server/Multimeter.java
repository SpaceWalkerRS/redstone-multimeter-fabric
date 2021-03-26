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
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;

import rsmm.fabric.common.Meter;
import rsmm.fabric.common.WorldPos;
import rsmm.fabric.common.log.LogManager;
import rsmm.fabric.common.packet.types.AddMeterPacket;
import rsmm.fabric.common.packet.types.MeterGroupDataPacket;
import rsmm.fabric.common.packet.types.MeterLogsDataPacket;
import rsmm.fabric.common.packet.types.RecolorMeterPacket;
import rsmm.fabric.common.packet.types.RemoveMeterPacket;
import rsmm.fabric.common.packet.types.RemoveAllMetersPacket;
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
			meterGroup.getLogManager().tick();
		}
	}
	
	public void broadcastMeterLogs() {
		for (ServerMeterGroup meterGroup : meterGroups.values()) {
			LogManager logManager = meterGroup.getLogManager();
			
			if (meterGroup.hasSubscribers()) {
				PacketByteBuf data = logManager.collectMeterLogs();
				
				MeterLogsDataPacket packet = new MeterLogsDataPacket(data);
				server.getPacketHandler().sendPacketToPlayers(packet, meterGroup.getSubscribers());
			}
			
			logManager.clearLogs();
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
	
	public void toggleMeter(WorldPos pos, boolean movable, ServerPlayerEntity player) {
		ServerMeterGroup meterGroup = subscriptions.get(player);
		
		if (meterGroup == null) {
			return;
		}
		
		if (meterGroup.hasMeterAt(pos)) {
			Meter meter = meterGroup.getMeterAt(pos);
			meterGroup.removeMeter(meter);
			
			RemoveMeterPacket packet = new RemoveMeterPacket(pos);
			server.getPacketHandler().sendPacketToPlayers(packet, meterGroup.getSubscribers());
		} else {
			World world = server.getMinecraftServer().getWorld(RegistryKey.of(Registry.DIMENSION, pos.getWorldId()));
			
			if (world != null) {
				String name = meterGroup.getNextMeterName();
				int color = meterGroup.getNextMeterColor();
				
				boolean powered = false;
				boolean active = false;
				
				BlockState state = world.getBlockState(pos);
				Block block = state.getBlock();
				
				powered = ((IBlock)block).isPowered(world, pos, state);
				
				if (((IBlock)block).isMeterable()) {
					active = ((Meterable)block).isActive(world, pos, state);
				}
				
				Meter meter = new Meter(pos, name, color, movable, powered, active);
				meterGroup.addMeter(meter);
				
				AddMeterPacket packet = new AddMeterPacket(pos, name, color, movable, powered, active);
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
			newSubscription = new ServerMeterGroup(name);
			
			meterGroups.put(name, newSubscription);
			newSubscription.getLogManager().syncTime(server.getMinecraftServer().getTicks());
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
			meterGroup = new ServerMeterGroup(name);
			meterGroup.decode(data);
			
			meterGroups.put(name, meterGroup);
			meterGroup.getLogManager().syncTime(server.getMinecraftServer().getTicks());
		} else {
			data = new PacketByteBuf(Unpooled.buffer());
			meterGroup.encode(data);
			
			MeterGroupDataPacket packet = new MeterGroupDataPacket(name, data);
			server.getPacketHandler().sendPacketToPlayer(packet, player);
		}
		
		subscriptions.put(player, meterGroup);
		meterGroup.addSubscriber(player);
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
