package redstone.multimeter.mixin.meterable;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.block.BlockState;
import net.minecraft.block.RedstoneWireBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import redstone.multimeter.block.MeterableBlock;
import redstone.multimeter.block.PowerSource;

@Mixin(RedstoneWireBlock.class)
public abstract class RedstoneWireBlockMixin implements MeterableBlock, PowerSource {
	
	@Inject(
			method = "method_26762",
			locals = LocalCapture.CAPTURE_FAILHARD,
			at = @At(
					value = "FIELD",
					ordinal = 1,
					target = "Lnet/minecraft/block/RedstoneWireBlock;POWER:Lnet/minecraft/state/property/IntProperty;"
			)
	)
	private void onUpdateLogic(World world, BlockPos pos, BlockPos otherPos, BlockState state, CallbackInfoReturnable<BlockState> cir, BlockState oldState, int oldPower, int receivedPower) {
		logPowered(world, pos, receivedPower > MIN_POWER);
	}
	
	@Override
	public boolean logPoweredOnBlockUpdate() {
		return false;
	}
	
	// This method is only called on blocks where 'logPoweredOnBlockUpdate'
	// returns 'true', so it does not really matter that a potentially
	// incorrect value is returned.
	@Override
	public boolean isPowered(World world, BlockPos pos, BlockState state) {
		return isActive(world, pos, state);
	}
	
	@Override
	public boolean isActive(World world, BlockPos pos, BlockState state) {
		return state.get(RedstoneWireBlock.POWER) > MIN_POWER;
	}
	
	@Override
	public int getPowerLevel(World world, BlockPos pos, BlockState state) {
		return state.get(RedstoneWireBlock.POWER);
	}
}
