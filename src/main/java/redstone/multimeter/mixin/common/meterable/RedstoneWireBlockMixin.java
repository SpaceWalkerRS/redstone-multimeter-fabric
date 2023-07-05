package redstone.multimeter.mixin.common.meterable;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.block.RedstoneWireBlock;
import net.minecraft.block.state.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import redstone.multimeter.block.MeterableBlock;
import redstone.multimeter.block.PowerSource;

@Mixin(RedstoneWireBlock.class)
public class RedstoneWireBlockMixin implements MeterableBlock, PowerSource {

	@Inject(
		method = "doUpdatePower",
		locals = LocalCapture.CAPTURE_FAILHARD,
		at = @At(
			value = "FIELD",
			ordinal = 1,
			target = "Lnet/minecraft/block/RedstoneWireBlock;POWER:Lnet/minecraft/block/state/property/IntegerProperty;"
		)
	)
	private void logPowered(World world, BlockPos pos, BlockPos self, BlockState state, CallbackInfoReturnable<BlockState> cir, BlockState oldState, int oldPower, int receivedPower) {
		rsmm$logPowered(world, pos, receivedPower > MIN_POWER);
	}

	@Override
	public boolean rsmm$logPoweredOnBlockUpdate() {
		return false;
	}

	// This method is only called on blocks where 'logPoweredOnBlockUpdate'
	// returns 'true', so it does not really matter that a potentially
	// incorrect value is returned.
	@Override
	public boolean rsmm$isPowered(World world, BlockPos pos, BlockState state) {
		return rsmm$isActive(world, pos, state);
	}

	@Override
	public boolean rsmm$isActive(World world, BlockPos pos, BlockState state) {
		return state.get(RedstoneWireBlock.POWER) > MIN_POWER;
	}

	@Override
	public int rsmm$getPowerLevel(World world, BlockPos pos, BlockState state) {
		return state.get(RedstoneWireBlock.POWER);
	}
}
