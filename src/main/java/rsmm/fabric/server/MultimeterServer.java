package rsmm.fabric.server;

import java.util.LinkedList;
import java.util.List;
import java.util.function.BooleanSupplier;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;

import rsmm.fabric.common.Meter;
import rsmm.fabric.common.MeterGroup;
import rsmm.fabric.common.Multimeter;
import rsmm.fabric.common.WorldPos;

public class MultimeterServer {
	
	private final MinecraftServer server;
	private final ServerPacketHandler packetHandler;
	private final Multimeter multimeter;
	private final List<MultimeterTask> scheduledTasks;
	
	public MultimeterServer(MinecraftServer server) {
		this.server = server;
		this.packetHandler = new ServerPacketHandler(this);
		this.multimeter = new Multimeter();
		this.scheduledTasks = new LinkedList<>();
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
	
	private void scheduleMultimeterTask(Runnable runnable) {
		scheduledTasks.add(new MultimeterTask(runnable));
	}
	
	public void tick(BooleanSupplier shouldKeepTicking) {
		// Clear the logs of the previous tick
		multimeter.clearLogs();
		
		for (MultimeterTask task : scheduledTasks) {
			task.run();
		}
		
		scheduledTasks.clear();
	}
	
	public void syncClientLogs() {
		
	}
	
	public void onPlayerJoin(ServerPlayerEntity player) {
		
	}
	
	public void onPlayerLeave(ServerPlayerEntity player) {
		
	}
	
	public void toggleMeter(WorldPos pos, ServerPlayerEntity player) {
		scheduleMultimeterTask(() -> {
			MeterGroup meterGroup = multimeter.getSubscription(player);
			
			if (meterGroup != null) {
				Meter meter = meterGroup.getMeterAt(pos);
				
				if (meter == null) {
					String name = meterGroup.nextMeterName();
					int color = meterGroup.nextMeterColor();
					
					meter = new Meter(pos, name, color);
					
					meterGroup.addMeter(meter);
				} else {
					meterGroup.removeMeter(meter);
				}
			}
		});
	}
	
	public void renameMeter(int index, String name, ServerPlayerEntity player) {
		scheduleMultimeterTask(() -> {
			MeterGroup meterGroup = multimeter.getSubscription(player);
			
			if (meterGroup != null) {
				meterGroup.renameMeter(index, name);
			}
		});
	}
	
	public void recolorMeter(int index, int color, ServerPlayerEntity player) {
		scheduleMultimeterTask(() -> {
			MeterGroup meterGroup = multimeter.getSubscription(player);
			
			if (meterGroup != null) {
				meterGroup.recolorMeter(index, color);
			}
		});
	}
	
	public void removeAllMeters(ServerPlayerEntity player) {
		scheduleMultimeterTask(() -> {
			MeterGroup meterGroup = multimeter.getSubscription(player);
			
			if (meterGroup != null) {
				meterGroup.removeMeters();
			}
		});
	}
	
	public void subscribeToMeterGroup(String name, ServerPlayerEntity player) {
		scheduleMultimeterTask(() -> {
			MeterGroup meterGroup = multimeter.getMeterGroup(name);
			
			if (meterGroup == null) {
				meterGroup = new MeterGroup(name);
				
				multimeter.addMeterGroup(meterGroup);
			}
			
			multimeter.addSubscription(player, meterGroup);
		});
	}
}
