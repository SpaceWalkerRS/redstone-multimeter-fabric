package redstone.multimeter.mixin.common.meterable;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RedstoneLampBlock;
import net.minecraft.world.level.block.state.BlockState;

import redstone.multimeter.block.MeterableBlock;

@Mixin(RedstoneLampBlock.class)
public class RedstoneLampBlockMixin implements MeterableBlock {

	@Inject(
		method = "tick",
		at = @At(
			value = "HEAD"
		)
	)
	private void logPowered(BlockState state, ServerLevel level, BlockPos pos, RandomSource random, CallbackInfo ci) {
		rsmm$logPowered(level, pos, state);
	}

	@Override
	public boolean rsmm$isActive(Level level, BlockPos pos, BlockState state) {
		return state.getValue(RedstoneLampBlock.LIT);
	}
}
