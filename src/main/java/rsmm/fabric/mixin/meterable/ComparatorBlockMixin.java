package rsmm.fabric.mixin.meterable;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.block.BlockState;
import net.minecraft.block.ComparatorBlock;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import rsmm.fabric.server.MeterableBlock;

@Mixin(ComparatorBlock.class)
public class ComparatorBlockMixin implements MeterableBlock {
	
	@Inject(
			method = "getPower",
			at = @At(
					value = "RETURN"
			)
	)
	private void onHasPowerInjectAtReturn(World world, BlockPos pos, BlockState state, CallbackInfoReturnable<Integer> cir) {
		logPowered(world, pos, cir.getReturnValue() > 0);
	}
	
	@Override
	public boolean isActive(World world, BlockPos pos, BlockState state) {
		return state.get(Properties.POWERED);
	}
}
