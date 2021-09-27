package rsmm.fabric.interfaces.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import rsmm.fabric.block.Meterable;
import rsmm.fabric.block.PowerSource;

public interface IBlock {
	
	default boolean isMeterable() {
		return this instanceof Meterable;
	}
	
	default boolean isPowerSource() {
		return this instanceof PowerSource;
	}
	
	default boolean standardIsPowered() {
		return true;
	}
	
	default boolean isPowered(World world, BlockPos pos, BlockState state) {
		return world.isReceivingRedstonePower(pos);
	}
}
