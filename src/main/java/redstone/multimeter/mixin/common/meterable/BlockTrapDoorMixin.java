package redstone.multimeter.mixin.common.meterable;

import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.block.BlockTrapDoor;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import redstone.multimeter.block.MeterableBlock;

@Mixin(BlockTrapDoor.class)
public class BlockTrapDoorMixin implements MeterableBlock {
	
	@Override
	public boolean isActiveRSMM(World world, BlockPos pos, IBlockState state) {
		return state.getValue(BlockTrapDoor.OPEN);
	}
}
