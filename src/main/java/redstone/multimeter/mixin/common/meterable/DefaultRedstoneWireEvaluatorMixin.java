package redstone.multimeter.mixin.common.meterable;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.redstone.DefaultRedstoneWireEvaluator;

import redstone.multimeter.block.PowerSource;
import redstone.multimeter.interfaces.mixin.IServerLevel;

@Mixin(DefaultRedstoneWireEvaluator.class)
public class DefaultRedstoneWireEvaluatorMixin {

	@Inject(
		method = "calculateTargetStrength",
		at = @At(
			value = "RETURN"
		)
	)
	private void logPowered(Level level, BlockPos pos, CallbackInfoReturnable<Integer> cir) {
		if (!level.isClientSide()) {
			((IServerLevel)level).getMultimeter().logPowered(level, pos, cir.getReturnValue() > PowerSource.MIN_POWER);
		}
	}
}
