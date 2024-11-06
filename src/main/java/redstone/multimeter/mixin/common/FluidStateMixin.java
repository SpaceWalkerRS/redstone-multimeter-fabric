package redstone.multimeter.mixin.common;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
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
	private void logRandomTick(ServerLevel level, BlockPos pos, RandomSource random, CallbackInfo ci) {
		((IServerLevel)level).getMultimeter().logRandomTick(level, pos);
	}
}
