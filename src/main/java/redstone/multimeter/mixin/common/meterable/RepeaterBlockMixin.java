package redstone.multimeter.mixin.common.meterable;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.RepeaterBlock;
import net.minecraft.world.level.block.state.BlockState;

import redstone.multimeter.block.MeterableBlock;
import redstone.multimeter.block.PowerSource;

@Mixin(RepeaterBlock.class)
public abstract class RepeaterBlockMixin implements MeterableBlock, PowerSource {

	@Inject(
		method = "isLocked",
		at = @At(
			value = "RETURN"
		)
	)
	private void logPowered(LevelReader level, BlockPos pos, BlockState state, CallbackInfoReturnable<Boolean> cir) {
		if (cir.getReturnValue() && level instanceof ServerLevel) {
			rsmm$logPowered((ServerLevel)level, pos, state);
		}
	}

	@Override
	public int rsmm$getPowerLevel(Level level, BlockPos pos, BlockState state) {
		return state.getValue(RepeaterBlock.POWERED) ? MAX_POWER : MIN_POWER;
	}
}
