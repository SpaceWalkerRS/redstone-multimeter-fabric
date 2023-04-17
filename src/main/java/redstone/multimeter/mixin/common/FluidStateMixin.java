package redstone.multimeter.mixin.common;

import java.util.Random;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.FluidState;

import redstone.multimeter.interfaces.mixin.IServerLevel;

@Mixin(FluidState.class)
public class FluidStateMixin {

	@Inject(
		method = "randomTick",
		at = @At(
			value = "HEAD"
		)
	)
	private void logRandomTick(Level level, BlockPos pos, Random random, CallbackInfo ci) {
		if (!level.isClientSide()) {
			((IServerLevel)level).getMultimeter().logRandomTick(level, pos);
		}
	}
}
