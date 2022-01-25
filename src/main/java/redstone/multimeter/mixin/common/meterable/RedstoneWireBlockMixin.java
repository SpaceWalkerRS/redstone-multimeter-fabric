package redstone.multimeter.mixin.common.meterable;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.block.BlockState;
import net.minecraft.block.RedstoneWireBlock;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import redstone.multimeter.block.MeterableBlock;
import redstone.multimeter.block.PowerSource;

@Mixin(RedstoneWireBlock.class)
public abstract class RedstoneWireBlockMixin implements MeterableBlock, PowerSource {
	
	@Shadow protected abstract int method_27842(World world, BlockPos pos);
	
	@Inject(
			method = "method_27842",
			at = @At(
					value = "RETURN"
			)
	)
	private void onPowerCheck(World world, BlockPos pos, CallbackInfoReturnable<Integer> cir) {
		logPoweredRSMM(world, pos, cir.getReturnValue() > MIN_POWER);
	}
	
	@Override
	public boolean logPoweredOnBlockUpdateRSMM() {
		return false;
	}
	
	@Override
	public boolean isPoweredRSMM(World world, BlockPos pos, BlockState state) {
		return method_27842(world, pos) > MIN_POWER;
	}
	
	@Override
	public boolean isActiveRSMM(World world, BlockPos pos, BlockState state) {
		return state.get(Properties.POWER) > MIN_POWER;
	}
	
	@Override
	public int getPowerLevelRSMM(World world, BlockPos pos, BlockState state) {
		return state.get(Properties.POWER);
	}
}
