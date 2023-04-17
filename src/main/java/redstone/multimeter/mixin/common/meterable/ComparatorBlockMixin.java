package redstone.multimeter.mixin.common.meterable;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.ComparatorBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ComparatorBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import redstone.multimeter.block.MeterableBlock;
import redstone.multimeter.block.PowerSource;

@Mixin(ComparatorBlock.class)
public abstract class ComparatorBlockMixin implements MeterableBlock, PowerSource {

	@Inject(
		method = "calculateOutputSignal",
		at = @At(
			value = "RETURN"
		)
	)
	private void logPowered(Level level, BlockPos pos, BlockState state, CallbackInfoReturnable<Integer> cir) {
		rsmm$logPowered(level, pos, cir.getReturnValue() > MIN_POWER);
	}

	@Override
	public boolean rsmm$logPowerChangeOnStateChange() {
		return false;
	}

	@Override
	public int rsmm$getPowerLevel(Level level, BlockPos pos, BlockState state) {
		BlockEntity blockEntity = level.getBlockEntity(pos);

		if (blockEntity instanceof ComparatorBlockEntity) {
			return ((ComparatorBlockEntity)blockEntity).getOutputSignal();
		}

		return MIN_POWER;
	}
}
