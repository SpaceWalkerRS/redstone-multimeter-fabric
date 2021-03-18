package rsmm.fabric.server;

import java.util.List;
import java.util.Map.Entry;
import java.util.function.BooleanSupplier;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;

import rsmm.fabric.common.MeterGroup;
import rsmm.fabric.common.Multimeter;
import rsmm.fabric.common.WorldPos;
import rsmm.fabric.common.packet.types.MeterGroupDataPacket;
import rsmm.fabric.common.packet.types.MultimeterTasksPacket;
import rsmm.fabric.common.packet.types.TimeSyncPacket;
import rsmm.fabric.common.task.MultimeterTask;
import rsmm.fabric.common.task.RecolorMeterTask;
import rsmm.fabric.common.task.RemoveMetersTask;
import rsmm.fabric.common.task.RenameMeterTask;
import rsmm.fabric.common.task.ToggleMeterTask;

public class MultimeterServer {
	
	private final MinecraftServer server;
	private final ServerPacketHandler packetHandler;
	private final Multimeter multimeter;
	
	public MultimeterServer(MinecraftServer server) {
		this.server = server;
		this.packetHandler = new ServerPacketHandler(this);
		this.multimeter = new Multimeter();
		
		multimeter.syncTime(server.getTicks());
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
	
	public void tick(BooleanSupplier shouldKeepTicking) {
		// Clear the logs of the previous tick
		multimeter.clearMeterLogs();
		multimeter.tick();
		
		syncMultimeterTasks();
		
		long currentTick = multimeter.getTime();
		
		if (currentTick % 20 == 0) {
			TimeSyncPacket packet = new TimeSyncPacket(currentTick);
			
			for (PlayerEntity player : multimeter.getPlayers()) {
				packetHandler.sendPacketToPlayer(packet, (ServerPlayerEntity)player);
			}
		}
	}
	
	public void syncClientLogs() {
		
	}
	
	private void syncMultimeterTasks() {
		for (Entry<MeterGroup, List<MultimeterTask>> entry : multimeter.getLoggedTasks().entrySet()) {
			MeterGroup meterGroup = entry.getKey();
			
			if (meterGroup.hasSubscribers()) {
				List<MultimeterTask> logs = entry.getValue();
				MultimeterTasksPacket packet = new MultimeterTasksPacket(logs);
				
				for (PlayerEntity player : meterGroup.getSubscribers()) {
					packetHandler.sendPacketToPlayer(packet, (ServerPlayerEntity)player);
				}
			}
		}
		
		multimeter.clearTaskLogs();
	}
	
	public void onPlayerJoin(ServerPlayerEntity player) {
		TimeSyncPacket packet = new TimeSyncPacket(multimeter.getTime());
		packetHandler.sendPacketToPlayer(packet, player);
	}
	
	public void onPlayerLeave(ServerPlayerEntity player) {
		multimeter.removeSubscription(player);
	}
	
	public void toggleMeter(WorldPos pos, ServerPlayerEntity player) {
		ToggleMeterTask task = new ToggleMeterTask(this, pos);
		MeterGroup meterGroup = multimeter.getSubscription(player);
		
		multimeter.scheduleTask(task, meterGroup);
	}
	
	public void renameMeter(int index, String name, ServerPlayerEntity player) {
		RenameMeterTask task = new RenameMeterTask(index, name);
		MeterGroup meterGroup = multimeter.getSubscription(player);
		
		multimeter.scheduleTask(task, meterGroup);
	}
	
	public void recolorMeter(int index, int color, ServerPlayerEntity player) {
		RecolorMeterTask task = new RecolorMeterTask(index, color);
		MeterGroup meterGroup = multimeter.getSubscription(player);
		
		multimeter.scheduleTask(task, meterGroup);
	}
	
	public void removeAllMeters(ServerPlayerEntity player) {
		RemoveMetersTask task = new RemoveMetersTask();
		MeterGroup meterGroup = multimeter.getSubscription(player);
		
		multimeter.scheduleTask(task, meterGroup);
	}
	
	public void subscribeToMeterGroup(String name, ServerPlayerEntity player) {
		MeterGroup currentSubscription = multimeter.getSubscription(player);
		
		if (currentSubscription != null) {
			multimeter.removeSubscription(player, currentSubscription);
		}
		
		MeterGroup meterGroup = multimeter.getMeterGroup(name);
		
		if (meterGroup == null) {
			meterGroup = new MeterGroup(name);
			
			multimeter.addMeterGroup(meterGroup);
		}
		
		multimeter.addSubscription(player, meterGroup);
		
		MeterGroupDataPacket packet = new MeterGroupDataPacket(meterGroup);
		packetHandler.sendPacketToPlayer(packet, player);
	}
}
