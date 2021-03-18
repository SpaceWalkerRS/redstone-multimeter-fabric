package rsmm.fabric.common;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import rsmm.fabric.interfaces.mixin.IServerWorld;
import rsmm.fabric.server.MultimeterServer;

public interface MeterableBlock extends Meterable {
	
	default void onBlockUpdate(World world, BlockPos pos	, boolean powered) {
		if (!world.isClient()) {
			MultimeterServer server = ((IServerWorld)world).getMultimeterServer();
			Multimeter multimeter = server.getMultimeter();
			
			multimeter.blockUpdate(world, pos, powered);
		}
	}
}
