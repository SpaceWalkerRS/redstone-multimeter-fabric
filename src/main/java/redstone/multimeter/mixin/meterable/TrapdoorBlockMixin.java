package redstone.multimeter.mixin.meterable;

import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.block.BlockState;
import net.minecraft.block.TrapdoorBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import redstone.multimeter.block.MeterableBlock;

@Mixin(TrapdoorBlock.class)
public class TrapdoorBlockMixin implements MeterableBlock {
	
	@Override
	public boolean isActive(World world, BlockPos pos, BlockState state) {
		return state.get(TrapdoorBlock.OPEN);
	}
}
