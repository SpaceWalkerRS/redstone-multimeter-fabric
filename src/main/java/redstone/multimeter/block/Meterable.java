package redstone.multimeter.block;

import net.minecraft.world.World;

import redstone.multimeter.interfaces.mixin.IBlock;

public interface Meterable extends IBlock {

	@Override
	default boolean rsmm$isMeterable() {
		return true;
	}

	public boolean rsmm$isActive(World world, int x, int y, int z, int metadata);

}
