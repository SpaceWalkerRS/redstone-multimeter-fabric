package redstone.multimeter.block;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import redstone.multimeter.interfaces.mixin.IBlock;

public interface Meterable extends IBlock {
	
	@Override
	default boolean isMeterableRSMM() {
		return true;
	}
	
	public boolean isActiveRSMM(World world, BlockPos pos, BlockState state);
	
}
