package redstone.multimeter.block;

import net.minecraft.world.World;

import redstone.multimeter.interfaces.mixin.IServerWorld;

public interface MeterableBlock extends Meterable {
	
	default void logPowered(World world, int x, int y, int z, boolean powered) {
		if (!world.isClient) {
			((IServerWorld)world).getMultimeter().logPowered(world, x, y, z, powered);
		}
	}
}
