package redstone.multimeter.block;

import net.minecraft.block.Block;
import net.minecraft.world.World;

import redstone.multimeter.interfaces.mixin.IServerWorld;

public interface MeterableBlock extends Meterable {

	default void rsmm$logPowered(World world, int x, int y, int z, boolean powered) {
		if (!world.isMultiplayer) {
			((IServerWorld)world).getMultimeter().logPowered(world, x, y, z, powered);
		}
	}

	default void rsmm$logPowered(World world, int x, int y, int z, Block block, int metadata) {
		if (!world.isMultiplayer) {
			((IServerWorld)world).getMultimeter().logPowered(world, x, y, z, block, metadata);
		}
	}
}
