package rsmm.fabric.mixin.meterable;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.block.BlockState;
import net.minecraft.block.PistonBlock;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import rsmm.fabric.common.MeterableBlock;
import rsmm.fabric.interfaces.mixin.IBlock;

@Mixin(PistonBlock.class)
public abstract class PistonBlockMixin implements MeterableBlock, IBlock {
	
	@Shadow protected abstract boolean shouldExtend(World world, BlockPos pos, Direction facing);
	
	@Inject(
			method = "shouldExtend",
			at = @At(
					value = "INVOKE"
			)
	)
	private void onShouldExtendInjectAtReturn(World world, BlockPos pos, Direction facing, CallbackInfoReturnable<Boolean> cir) {
		onBlockUpdate(world, pos, cir.getReturnValue());
	}
	
	@Override
	public boolean isActive(World world, BlockPos pos, BlockState state) {
		return state.get(Properties.EXTENDED);
	}
	
	@Override
	public boolean standardIsPowered() {
		return false;
	}
	
	@Override
	public boolean isPowered(World world, BlockPos pos, BlockState state) {
		return shouldExtend(world, pos, state.get(Properties.FACING));
	}
}
