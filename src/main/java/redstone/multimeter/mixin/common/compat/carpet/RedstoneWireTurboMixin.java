package redstone.multimeter.mixin.common.compat.carpet;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;

import carpet.helpers.RedstoneWireTurbo;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import redstone.multimeter.block.PowerSource;
import redstone.multimeter.interfaces.mixin.IServerLevel;

@Pseudo
@Mixin(RedstoneWireTurbo.class)
public class RedstoneWireTurboMixin {

	@WrapOperation(
		method = "calculateCurrentChanges",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/level/Level;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z"
		)
	)
	private boolean logPowered(Level level, BlockPos pos, BlockState state, int flags, Operation<Boolean> operation, @Local(ordinal = 1) int newPower) {
		if (!level.isClientSide()) {
			((IServerLevel)level).getMultimeter().logPowered(level, pos, newPower > PowerSource.MIN_POWER);
		}

		return operation.call(level, pos, state, flags);
	}
}
