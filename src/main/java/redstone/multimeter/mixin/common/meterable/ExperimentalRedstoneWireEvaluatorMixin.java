package redstone.multimeter.mixin.common.meterable;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.llamalad7.mixinextras.sugar.Local;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.redstone.ExperimentalRedstoneWireEvaluator;
import net.minecraft.world.level.redstone.Orientation;

import redstone.multimeter.block.PowerSource;
import redstone.multimeter.interfaces.mixin.IServerLevel;

@Mixin(ExperimentalRedstoneWireEvaluator.class)
public class ExperimentalRedstoneWireEvaluatorMixin {

	@Inject(
		method = "calculateCurrentChanges",
		slice = @Slice(
			from = @At(
				value = "INVOKE",
				ordinal = 0,
				target = "Lnet/minecraft/world/level/redstone/ExperimentalRedstoneWireEvaluator;unpackPower(I)I"
			)
		),
		at = @At(
			value = "INVOKE",
			ordinal = 0,
			target = "Ljava/lang/Math;max(II)I"
		)
	)
	private void rsmm$logPoweredOff(Level level, BlockPos pos, Orientation orientation, CallbackInfo ci, @Local(ordinal = 0) int blockSignal, @Local(ordinal = 1) int wireSignal) {
		if (!level.isClientSide()) {
			((IServerLevel)level).getMultimeter().logPowered(level, pos, Math.max(blockSignal, wireSignal) > PowerSource.MIN_POWER);
		}
	}

	@Inject(
		method = "calculateCurrentChanges",
		slice = @Slice(
			from = @At(
				value = "INVOKE",
				ordinal = 1,
				target = "Lnet/minecraft/world/level/redstone/ExperimentalRedstoneWireEvaluator;unpackPower(I)I"
			)
		),
		at = @At(
			value = "INVOKE",
			ordinal = 0,
			target = "Ljava/lang/Math;max(II)I"
		)
	)
	private void rsmm$logPoweredOn(Level level, BlockPos pos, Orientation orientation, CallbackInfo ci, @Local(ordinal = 0) int blockSignal, @Local(ordinal = 1) int wireSignal) {
		if (!level.isClientSide()) {
			((IServerLevel)level).getMultimeter().logPowered(level, pos, Math.max(blockSignal, wireSignal) > PowerSource.MIN_POWER);
		}
	}
}
