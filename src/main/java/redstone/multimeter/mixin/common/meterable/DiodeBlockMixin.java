package redstone.multimeter.mixin.common.meterable;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.block.DiodeBlock;
import net.minecraft.block.state.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import redstone.multimeter.block.MeterableBlock;

@Mixin(DiodeBlock.class)
public class DiodeBlockMixin implements MeterableBlock {

	@Shadow @Final private boolean powered;

	@Shadow private boolean shouldBePowered(World world, BlockPos pos, BlockState state) { return false; }

	@Inject(
		method = "shouldBePowered",
		at = @At(
			value = "RETURN"
		)
	)
	private void logPowered(World world, BlockPos pos, BlockState state, CallbackInfoReturnable<Boolean> cir) {
		rsmm$logPowered(world, pos, cir.getReturnValue()); // repeaters
	}

	@Override
	public boolean rsmm$logPoweredOnBlockUpdate() {
		return false;
	}

	@Override
	public boolean rsmm$isPowered(World world, BlockPos pos, BlockState state) {
		return shouldBePowered(world, pos, state);
	}

	@Override
	public boolean rsmm$isActive(World world, BlockPos pos, BlockState state) {
		return powered;
	}
}
