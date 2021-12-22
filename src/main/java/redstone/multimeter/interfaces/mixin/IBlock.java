package redstone.multimeter.interfaces.mixin;

import net.minecraft.world.World;

public interface IBlock {
	
	default boolean isMeterable() {
		return false;
	}
	
	default boolean isPowerSource() {
		return false;
	}
	
	default boolean logPoweredOnBlockUpdate() {
		return true;
	}
	
	default boolean isPowered(World world, int x, int y, int z, int metadata) {
		return world.method_3739(x, y, z);
	}
}
