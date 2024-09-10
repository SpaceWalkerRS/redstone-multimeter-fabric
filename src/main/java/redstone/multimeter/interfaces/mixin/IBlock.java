package redstone.multimeter.interfaces.mixin;

import net.minecraft.world.World;

public interface IBlock {

	default boolean rsmm$isMeterable() {
		return false;
	}

	default boolean rsmm$isPowerSource() {
		return false;
	}

	default boolean rsmm$logPoweredOnBlockUpdate() {
		return true;
	}

	default boolean rsmm$isPowered(World world, int x, int y, int z, int metadata) {
		return world.hasNeighborSignal(x, y, z);
	}
}
