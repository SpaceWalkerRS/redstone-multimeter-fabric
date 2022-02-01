package redstone.multimeter.block;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import redstone.multimeter.interfaces.mixin.IWorldServer;

public interface MeterableBlock extends Meterable {
	
	default void logPoweredRSMM(World world, BlockPos pos, boolean powered) {
		if (!world.isRemote) {
			((IWorldServer)world).getMultimeter().logPowered(world, pos, powered);
		}
	}
	
	default void logPoweredRSMM(World world, BlockPos pos, IBlockState state) {
		if (!world.isRemote) {
			((IWorldServer)world).getMultimeter().logPowered(world, pos, state);
		}
	}
}
