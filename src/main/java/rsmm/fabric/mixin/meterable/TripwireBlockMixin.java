package rsmm.fabric.mixin.meterable;

import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.block.BlockState;
import net.minecraft.block.TripwireBlock;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import rsmm.fabric.server.MeterableBlock;

@Mixin(TripwireBlock.class)
public class TripwireBlockMixin implements MeterableBlock {

	@Override
	public boolean isActive(World world, BlockPos pos, BlockState state) {
		return state.get(Properties.POWERED);
	}
}
