package rsmm.fabric.common.task;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;

import rsmm.fabric.common.MeterGroup;
import rsmm.fabric.common.Meterable;
import rsmm.fabric.common.WorldPos;
import rsmm.fabric.interfaces.mixin.IBlock;
import rsmm.fabric.server.MultimeterServer;

public class ToggleMeterTask implements MultimeterTask {
	
	// This task can only be handled on the server
	// It schedules and AddMeter or RemoveMeter task
	// that is then synced with clients
	private MultimeterServer server;
	private WorldPos pos;
	
	public ToggleMeterTask(MultimeterServer server, WorldPos pos) {
		this.server = server;
		this.pos = pos;
	}
	
	@Override
	public boolean equals(Object other) {
		if (other instanceof ToggleMeterTask) {
			ToggleMeterTask task = (ToggleMeterTask)other;
			
			return pos.equals(task.pos);
		}
		
		return false;
	}
	
	@Override
	public void encode(PacketByteBuf buffer) {
		
	}
	
	@Override
	public void decode(PacketByteBuf buffer) {
		
	}
	
	@Override
	public boolean run(MeterGroup meterGroup) {
		// In case this task is accidentally sent to clients,
		// do nothing
		if (pos == null) {
			return false;
		}
		
		if (meterGroup.hasMeterAt(pos)) {
			String name = meterGroup.nextMeterName();
			int color = meterGroup.nextMeterColor();
			
			boolean powered = false;
			boolean active = false;
			
			RegistryKey<World> key = RegistryKey.of(Registry.DIMENSION, pos.getWorldId());
			World world = server.getMinecraftServer().getWorld(key);
			
			if (world != null) {
				BlockState state = world.getBlockState(pos);
				Block block = state.getBlock();
				
				powered = ((IBlock)block).isPowered(world, pos, state);
				
				if (((IBlock)block).isMeterable()) {
					active = ((Meterable)block).isActive(world, pos, state);
				}
			}
			
			AddMeterTask task = new AddMeterTask(pos, name, color, powered, active);
			server.getMultimeter().runTask(task, meterGroup);
		} else {
			RemoveMeterTask task = new RemoveMeterTask(pos);
			server.getMultimeter().runTask(task, meterGroup);
		}
		
		return false;
	}
}
