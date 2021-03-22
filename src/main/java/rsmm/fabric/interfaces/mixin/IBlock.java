package rsmm.fabric.interfaces.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import rsmm.fabric.server.Meterable;

public interface IBlock {
	
	public boolean standardIsPowered();
	
	default boolean isMeterable() {
		return this instanceof Meterable;
	}
	
	default boolean isPowered(World world, BlockPos pos, BlockState state) {
		return world.isReceivingRedstonePower(pos);
	}
}
