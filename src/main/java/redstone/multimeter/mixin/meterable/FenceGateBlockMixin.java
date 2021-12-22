package redstone.multimeter.mixin.meterable;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.class_3772;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.FenceGateBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import redstone.multimeter.block.MeterableBlock;

@Mixin(FenceGateBlock.class)
public class FenceGateBlockMixin implements MeterableBlock {
	
	@Inject(
			method = "method_8641",
			locals = LocalCapture.CAPTURE_FAILHARD,
			at = @At(
					value = "FIELD",
					ordinal = 0,
					target = "Lnet/minecraft/block/FenceGateBlock;field_18321:Lnet/minecraft/state/property/BooleanProperty;"
			)
	)
	private void onNeighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, CallbackInfo ci, boolean powered) {
		logPowered(world, pos, powered);
	}
	
	@Override
	public boolean logPoweredOnBlockUpdate() {
		return false;
	}
	
	@Override
	public boolean isActive(World world, BlockPos pos, BlockState state) {
		return state.method_16934(class_3772.field_18781);
	}
}
