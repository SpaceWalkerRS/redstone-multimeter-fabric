package rsmm.fabric.mixin.meterable;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.block.AbstractRedstoneGateBlock;
import net.minecraft.block.BlockState;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import rsmm.fabric.block.MeterableBlock;
import rsmm.fabric.interfaces.mixin.IBlock;

@Mixin(AbstractRedstoneGateBlock.class)
public abstract class AbstractRedstoneGateBlockMixin implements IBlock, MeterableBlock {
	
	@Shadow protected abstract boolean hasPower(World world, BlockPos pos, BlockState state);
	
	@Inject(
			method = "hasPower",
			at = @At(
					value = "RETURN"
			)
	)
	private void onHasPowerInjectAtReturn(World world, BlockPos pos, BlockState state, CallbackInfoReturnable<Boolean> cir) {
		logPowered(world, pos, cir.getReturnValue()); // repeaters
	}
	
	@Override
	public boolean standardIsPowered() {
		return false;
	}
	
	@Override
	public boolean isPowered(World world, BlockPos pos, BlockState state) {
		return hasPower(world, pos, state);
	}
	
	@Override
	public boolean isActive(World world, BlockPos pos, BlockState state) {
		return state.get(Properties.POWERED);
	}
}
