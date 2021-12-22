package redstone.multimeter.mixin.meterable;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.class_3717;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import redstone.multimeter.block.MeterableBlock;

@Mixin(class_3717.class)
public abstract class WallRedstoneTorchBlockMixin implements MeterableBlock {
	
	@Inject(
			method = "method_8885",
			at = @At(
					value = "RETURN"
			)
	)
	private void onPowerCheck(World world, BlockPos pos, BlockState state, CallbackInfoReturnable<Boolean> cir) {
		logPowered(world, pos, cir.getReturnValue());
	}
}
