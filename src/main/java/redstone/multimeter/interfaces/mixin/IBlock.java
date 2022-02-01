package redstone.multimeter.interfaces.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IBlock {
	
	default boolean isMeterableRSMM() {
		return false;
	}
	
	default boolean isPowerSourceRSMM() {
		return false;
	}
	
	default boolean logPoweredOnBlockUpdateRSMM() {
		return true;
	}
	
	default boolean isPoweredRSMM(World world, BlockPos pos, BlockState state) {
		return world.isReceivingRedstonePower(pos);
	}
}
