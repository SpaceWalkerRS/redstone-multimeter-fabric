package redstone.multimeter.mixin.common.meterable;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RedStoneWireBlock;
import net.minecraft.world.level.block.state.BlockState;

import redstone.multimeter.block.MeterableBlock;
import redstone.multimeter.block.PowerSource;

@Mixin(RedStoneWireBlock.class)
public class RedStoneWireBlockMixin implements MeterableBlock, PowerSource {

	@Inject(
		method = "updatePowerStrengthImpl",
		locals = LocalCapture.CAPTURE_FAILHARD,
		at = @At(
			value = "FIELD",
			ordinal = 1,
			target = "Lnet/minecraft/world/level/block/RedStoneWireBlock;POWER:Lnet/minecraft/world/level/block/state/properties/IntegerProperty;"
		)
	)
	private void logPowered(Level level, BlockPos pos, BlockState state, CallbackInfoReturnable<BlockState> cir, BlockState oldState, int oldPower, int neighborPower, int wirePower, int receivedPower) {
		rsmm$logPowered(level, pos, receivedPower > MIN_POWER);
	}

	@Override
	public boolean rsmm$logPoweredOnBlockUpdate() {
		return false;
	}

	// This method is only called on blocks where 'logPoweredOnBlockUpdate'
	// returns 'true', so it does not really matter that a potentially
	// incorrect value is returned.
	@Override
	public boolean rsmm$isPowered(Level level, BlockPos pos, BlockState state) {
		return rsmm$isActive(level, pos, state);
	}

	@Override
	public boolean rsmm$isActive(Level level, BlockPos pos, BlockState state) {
		return state.getValue(RedStoneWireBlock.POWER) > MIN_POWER;
	}

	@Override
	public int rsmm$getPowerLevel(Level level, BlockPos pos, BlockState state) {
		return state.getValue(RedStoneWireBlock.POWER);
	}
}
