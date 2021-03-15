package rsmm.fabric.server;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

import rsmm.fabric.common.MeterGroup;
import rsmm.fabric.interfaces.mixin.IMinecraftServer;

public class Multimeter {
	
	private final ServerWorld world;
	private final Map<String, MeterGroup> meterGroups;
	
	public Multimeter(ServerWorld world) {
		this.world = world;
		this.meterGroups = new HashMap<>();
		
		getMultimeterServer().registerMultimeter(this);
	}
	
	private MultimeterServer getMultimeterServer() {
		return ((IMinecraftServer)world.getServer()).getMultimeterServer();
	}
	
	public ServerWorld getWorld() {
		return world;
	}
	
	public void blockUpdate(BlockPos pos, boolean powered) {
		for (MeterGroup meterGroup : meterGroups.values()) {
			meterGroup.blockUpdate(pos, powered);
		}
	}
	
	public void stateChanged(BlockPos pos, boolean active) {
		for (MeterGroup meterGroup : meterGroups.values()) {
			meterGroup.stateChanged(pos, active);
		}
	}
	
	public void toggleMeter(BlockPos pos, ServerPlayerEntity player) {
		for (MeterGroup meterGroup : meterGroups.values()) {
			if (meterGroup.isSubscribed(player)) {
				meterGroup.toggleMeter(pos);
			}
		}
	}
}
