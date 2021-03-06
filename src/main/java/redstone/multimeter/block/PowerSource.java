package redstone.multimeter.block;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import redstone.multimeter.interfaces.mixin.IBlock;

public interface PowerSource extends IBlock {
	
	public static final int MIN_POWER = 0;
	public static final int MAX_POWER = 15;
	
	@Override
	default boolean isPowerSourceRSMM() {
		return true;
	}
	
	default boolean logPowerChangeOnStateChangeRSMM() {
		return true;
	}
	
	public int getPowerLevelRSMM(World world, BlockPos pos, BlockState state);
	
}
