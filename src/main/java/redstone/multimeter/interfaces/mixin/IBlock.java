package redstone.multimeter.interfaces.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
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
	
	default boolean isPowered(World world, BlockPos pos, BlockState state) {
		return world.isReceivingRedstonePower(pos);
	}
}
