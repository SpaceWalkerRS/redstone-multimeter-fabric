package redstone.multimeter.mixin.common.meterable;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.block.BlockState;
import net.minecraft.block.HopperBlock;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import redstone.multimeter.block.MeterableBlock;

@Mixin(HopperBlock.class)
public abstract class HopperBlockMixin implements MeterableBlock {
	
	@Inject(
			method = "updateEnabled",
			locals = LocalCapture.CAPTURE_FAILHARD,
			at = @At(
					value = "FIELD",
					ordinal = 0,
					shift = Shift.BEFORE,
					target = "Lnet/minecraft/block/HopperBlock;ENABLED:Lnet/minecraft/state/property/BooleanProperty;"
			)
	)
	private void onUpdateEnabled(World world, BlockPos pos, BlockState state, CallbackInfo ci, boolean shouldBeEnabled) {
		logPoweredRSMM(world, pos, !shouldBeEnabled);
	}
	
	@Override
	public boolean logPoweredOnBlockUpdateRSMM() {
		return false;
	}
	
	@Override
	public boolean isActiveRSMM(World world, BlockPos pos, BlockState state) {
		return state.get(Properties.ENABLED);
	}
}
