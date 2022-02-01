package redstone.multimeter.mixin.common.meterable;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.block.BlockRedstoneDiode;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import redstone.multimeter.block.MeterableBlock;

@Mixin(BlockRedstoneDiode.class)
public abstract class BlockRedstoneDiodeMixin implements MeterableBlock {
	
	@Shadow private boolean isRepeaterPowered;
	
	@Shadow protected abstract boolean shouldBePowered(World world, BlockPos pos, IBlockState state);
	
	@Inject(
			method = "shouldBePowered",
			at = @At(
					value = "RETURN"
			)
	)
	private void onPowerCheck(World world, BlockPos pos, IBlockState state, CallbackInfoReturnable<Boolean> cir) {
		logPoweredRSMM(world, pos, cir.getReturnValue()); // repeaters
	}
	
	@Override
	public boolean logPoweredOnBlockUpdateRSMM() {
		return false;
	}
	
	@Override
	public boolean isPoweredRSMM(World world, BlockPos pos, IBlockState state) {
		return shouldBePowered(world, pos, state);
	}
	
	@Override
	public boolean isActiveRSMM(World world, BlockPos pos, IBlockState state) {
		return isRepeaterPowered;
	}
}
