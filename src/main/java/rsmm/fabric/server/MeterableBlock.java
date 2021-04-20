package rsmm.fabric.server;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import rsmm.fabric.common.event.EventType;
import rsmm.fabric.interfaces.mixin.IServerWorld;

public interface MeterableBlock extends Meterable {
	
	@Override
	default int getDefaultMeteredEvents() {
		return EventType.POWERED.flag() | EventType.ACTIVE.flag() | EventType.MOVED.flag();
	}
	
	default void onBlockUpdate(World world, BlockPos pos, boolean powered) {
		if (!world.isClient()) {
			MultimeterServer server = ((IServerWorld)world).getMultimeterServer();
			Multimeter multimeter = server.getMultimeter();
			
			multimeter.blockUpdate(world, pos, powered);
		}
	}
}
