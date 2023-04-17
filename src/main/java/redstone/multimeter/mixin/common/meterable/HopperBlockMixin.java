package redstone.multimeter.mixin.common.meterable;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.HopperBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.HopperBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import redstone.multimeter.block.MeterableBlock;
import redstone.multimeter.interfaces.mixin.IHopperBlockEntity;

@Mixin(HopperBlock.class)
public class HopperBlockMixin implements MeterableBlock {

	@Inject(
		method = "checkPoweredState",
		locals = LocalCapture.CAPTURE_FAILHARD,
		at = @At(
			value = "FIELD",
			ordinal = 0,
			shift = Shift.BEFORE,
			target = "Lnet/minecraft/world/level/block/HopperBlock;ENABLED:Lnet/minecraft/world/level/block/state/properties/BooleanProperty;"
		)
	)
	private void logPowered(Level level, BlockPos pos, BlockState state, CallbackInfo ci, boolean shouldBeEnabled) {
		rsmm$logPowered(level, pos, !shouldBeEnabled);
	}

	@Override
	public boolean rsmm$logPoweredOnBlockUpdate() {
		return false;
	}

	@Override
	public boolean rsmm$isActive(Level level, BlockPos pos, BlockState state) {
		BlockEntity blockEntity = level.getBlockEntity(pos);

		if (blockEntity instanceof HopperBlockEntity) {
			return !((IHopperBlockEntity)blockEntity).rsmm$isOnCooldown();
		}

		return false;
	}
}
