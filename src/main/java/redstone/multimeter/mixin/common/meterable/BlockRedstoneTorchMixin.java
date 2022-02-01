package redstone.multimeter.mixin.common.meterable;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.block.BlockRedstoneTorch;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import redstone.multimeter.block.MeterableBlock;
import redstone.multimeter.block.PowerSource;

@Mixin(BlockRedstoneTorch.class)
public abstract class BlockRedstoneTorchMixin implements MeterableBlock, PowerSource {
	
	@Shadow @Final private boolean isOn;
	
	@Shadow protected abstract boolean shouldBeOff(World world, BlockPos pos, IBlockState state);
	
	@Inject(
			method = "shouldBeOff",
			at = @At(
					value = "RETURN"
			)
	)
	private void onPowerCheck(World world, BlockPos pos, IBlockState state, CallbackInfoReturnable<Boolean> cir) {
		logPoweredRSMM(world, pos, cir.getReturnValue());
	}
	
	@Override
	public boolean logPoweredOnBlockUpdateRSMM() {
		return false;
	}
	
	@Override
	public boolean isPoweredRSMM(World world, BlockPos pos, IBlockState state) {
		return shouldBeOff(world, pos, state);
	}
	
	@Override
	public boolean isActiveRSMM(World world, BlockPos pos, IBlockState state) {
		return isOn;
	}
	
	@Override
	public int getPowerLevelRSMM(World world, BlockPos pos, IBlockState state) {
		return isOn ? MAX_POWER : MIN_POWER;
	}
}
