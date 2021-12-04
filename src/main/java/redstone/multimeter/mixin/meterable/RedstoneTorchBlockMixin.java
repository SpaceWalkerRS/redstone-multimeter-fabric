package redstone.multimeter.mixin.meterable;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.block.BlockState;
import net.minecraft.block.RedstoneTorchBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import redstone.multimeter.block.MeterableBlock;
import redstone.multimeter.block.PowerSource;

@Mixin(RedstoneTorchBlock.class)
public abstract class RedstoneTorchBlockMixin implements MeterableBlock, PowerSource {
	
	@Shadow @Final private boolean field_24721;
	
	@Shadow protected abstract boolean method_26773(World world, BlockPos pos, BlockState state);
	
	@Inject(
			method = "method_26773",
			at = @At(
					value = "RETURN"
			)
	)
	private void onPowerCheck(World world, BlockPos pos, BlockState state, CallbackInfoReturnable<Boolean> cir) {
		logPowered(world, pos, cir.getReturnValue()); // floor redstone torches only
	}
	
	@Override
	public boolean logPoweredOnBlockUpdate() {
		return false;
	}
	
	@Override
	public boolean isPowered(World world, BlockPos pos, BlockState state) {
		return method_26773(world, pos, state);
	}
	
	@Override
	public boolean isActive(World world, BlockPos pos, BlockState state) {
		return field_24721;
	}
	
	@Override
	public int getPowerLevel(World world, BlockPos pos, BlockState state) {
		return field_24721 ? MAX_POWER : MIN_POWER;
	}
}
