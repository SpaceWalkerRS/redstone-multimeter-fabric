package rsmm.fabric.server;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.BooleanSupplier;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;

import rsmm.fabric.common.Meter;
import rsmm.fabric.common.MeterGroup;
import rsmm.fabric.common.Meterable;
import rsmm.fabric.common.Multimeter;
import rsmm.fabric.common.WorldPos;
import rsmm.fabric.common.packet.types.MultimeterTasksPacket;
import rsmm.fabric.common.task.AddMeterTask;
import rsmm.fabric.common.task.MultimeterTask;
import rsmm.fabric.common.task.RecolorMeterTask;
import rsmm.fabric.common.task.RemoveMeterTask;
import rsmm.fabric.common.task.RemoveMetersTask;
import rsmm.fabric.common.task.RenameMeterTask;
import rsmm.fabric.interfaces.mixin.IBlock;

public class MultimeterServer {
	
	private final MinecraftServer server;
	private final ServerPacketHandler packetHandler;
	private final Multimeter multimeter;
	private final Map<MeterGroup, List<MultimeterTask>> loggedMultimeterTasks;
	
	public MultimeterServer(MinecraftServer server) {
		this.server = server;
		this.packetHandler = new ServerPacketHandler(this);
		this.multimeter = new Multimeter();
		this.loggedMultimeterTasks = new HashMap<>();
	}
	
	public MinecraftServer getMinecraftServer() {
		return server;
	}
	
	public ServerPacketHandler getPacketHandler() {
		return packetHandler;
	}
	
	public Multimeter getMultimeter() {
		return multimeter;
	}
	
	private void logMultimeterTask(MeterGroup meterGroup, MultimeterTask task) {
		List<MultimeterTask> logs = loggedMultimeterTasks.get(meterGroup);
		
		if (logs == null) {
			logs = new LinkedList<>();
			loggedMultimeterTasks.put(meterGroup, logs);
		}
		
		logs.add(task);
	}
	
	public void tick(BooleanSupplier shouldKeepTicking) {
		// Clear the logs of the previous tick
		multimeter.clearLogs();
		multimeter.tick(server.getTicks());
		
		syncMultimeterTasks();
	}
	
	public void syncClientLogs() {
		
	}
	
	private void syncMultimeterTasks() {
		for (Entry<MeterGroup, List<MultimeterTask>> entry : loggedMultimeterTasks.entrySet()) {
			MeterGroup meterGroup = entry.getKey();
			
			if (meterGroup.hasSubscribers()) {
				List<MultimeterTask> logs = entry.getValue();
				MultimeterTasksPacket packet = new MultimeterTasksPacket(logs);
				
				for (PlayerEntity player : meterGroup.getSubscribers()) {
					packetHandler.sendPacketToPlayer(packet, (ServerPlayerEntity)player);
				}
			}
		}
		
		loggedMultimeterTasks.clear();
	}
	
	public void onPlayerJoin(ServerPlayerEntity player) {
		
	}
	
	public void onPlayerLeave(ServerPlayerEntity player) {
		multimeter.removeSubscription(player);
	}
	
	public void toggleMeter(WorldPos pos, ServerPlayerEntity player) {
		multimeter.scheduleTask(() -> {
			MeterGroup meterGroup = multimeter.getSubscription(player);
			
			if (meterGroup != null) {
				if (meterGroup.hasMeterAt(pos)) {
					String name = meterGroup.nextMeterName();
					int color = meterGroup.nextMeterColor();
					
					boolean powered = false;
					boolean active = false;
					
					RegistryKey<World> key = RegistryKey.of(Registry.DIMENSION, pos.getWorldId());
					World world = server.getWorld(key);
					
					if (world != null) {
						BlockState state = world.getBlockState(pos);
						Block block = state.getBlock();
						
						powered = ((IBlock)block).isPowered(world, pos, state);
						
						if (((IBlock)block).isMeterable()) {
							active = ((Meterable)block).isActive(world, pos, state);
						}
					}
					
					Meter meter = new Meter(pos, name, color, powered, active);
					AddMeterTask task = new AddMeterTask(pos, name, color, powered, active);
					
					meterGroup.addMeter(meter);
					logMultimeterTask(meterGroup, task);
				} else {
					Meter meter = meterGroup.getMeterAt(pos);
					RemoveMeterTask task = new RemoveMeterTask(pos);
					
					meterGroup.removeMeter(meter);
					logMultimeterTask(meterGroup, task);
				}
			}
		});
	}
	
	public void renameMeter(int index, String name, ServerPlayerEntity player) {
		multimeter.scheduleTask(() -> {
			MeterGroup meterGroup = multimeter.getSubscription(player);
			
			if (meterGroup != null) {
				RenameMeterTask task = new RenameMeterTask(index, name);
				
				meterGroup.renameMeter(index, name);
				logMultimeterTask(meterGroup, task);
			}
		});
	}
	
	public void recolorMeter(int index, int color, ServerPlayerEntity player) {
		multimeter.scheduleTask(() -> {
			MeterGroup meterGroup = multimeter.getSubscription(player);
			
			if (meterGroup != null) {
				RecolorMeterTask task = new RecolorMeterTask(index, color);
				
				meterGroup.recolorMeter(index, color);
				logMultimeterTask(meterGroup, task);
			}
		});
	}
	
	public void removeAllMeters(ServerPlayerEntity player) {
		multimeter.scheduleTask(() -> {
			MeterGroup meterGroup = multimeter.getSubscription(player);
			
			if (meterGroup != null) {
				RemoveMetersTask task = new RemoveMetersTask();
				
				meterGroup.removeMeters();
				logMultimeterTask(meterGroup, task);
			}
		});
	}
	
	public void subscribeToMeterGroup(String name, ServerPlayerEntity player) {
		multimeter.scheduleTask(() -> {
			MeterGroup meterGroup = multimeter.getMeterGroup(name);
			
			if (meterGroup == null) {
				meterGroup = new MeterGroup(name);
				
				multimeter.addMeterGroup(meterGroup);
			}
			
			multimeter.addSubscription(player, meterGroup);
		});
	}
}
