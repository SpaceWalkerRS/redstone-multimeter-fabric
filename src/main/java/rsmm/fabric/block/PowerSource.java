package rsmm.fabric.block;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface PowerSource {
	
	public static final int MAX_POWER = 15;
	
	default boolean standardLogPowerChange() {
		return true;
	}
	
	public int getPowerLevel(World world, BlockPos pos, BlockState state);
	
}
