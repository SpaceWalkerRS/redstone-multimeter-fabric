package redstone.multimeter.mixin.meterable;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.BlockState;
import net.minecraft.block.RedstoneTorchBlock;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import redstone.multimeter.block.MeterableBlock;
import redstone.multimeter.block.PowerSource;

@Mixin(RedstoneTorchBlock.class)
public abstract class RedstoneTorchBlockMixin implements MeterableBlock, PowerSource {
	
	@Shadow protected abstract boolean method_10488(World world, BlockPos pos, BlockState state);
	
	@Inject(
			method = "method_10488",
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
		return method_10488(world, pos, state);
	}
	
	@Override
	public boolean isActive(World world, BlockPos pos, BlockState state) {
		return state.get(Properties.LIT);
	}
	
	@Override
	public int getPowerLevel(World world, BlockPos pos, BlockState state) {
		return state.get(Properties.LIT) ? MAX_POWER : MIN_POWER;
	}
}
