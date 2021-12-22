package redstone.multimeter.mixin.meterable;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.block.RedstoneTorchBlock;
import net.minecraft.world.World;

import redstone.multimeter.block.MeterableBlock;
import redstone.multimeter.block.PowerSource;

@Mixin(RedstoneTorchBlock.class)
public abstract class RedstoneTorchBlockMixin implements MeterableBlock, PowerSource {
	
	@Shadow @Final private boolean field_292;
	
	@Shadow protected abstract boolean method_347(World world, int x, int y, int z);
	
	@Inject(
			method = "method_347",
			at = @At(
					value = "RETURN"
			)
	)
	private void onPowerCheck(World world, int x, int y, int z, CallbackInfoReturnable<Boolean> cir) {
		logPowered(world, x, y, z, cir.getReturnValue()); // floor redstone torches only
	}
	
	@Override
	public boolean logPoweredOnBlockUpdate() {
		return false;
	}
	
	@Override
	public boolean isPowered(World world, int x, int y, int z, int metadata) {
		return method_347(world, x, y, z);
	}
	
	@Override
	public boolean isActive(World world, int x, int y, int z, int metadata) {
		return field_292;
	}
	
	@Override
	public int getPowerLevel(World world, int x, int y, int z, int metadata) {
		return field_292 ? MAX_POWER : MIN_POWER;
	}
}
