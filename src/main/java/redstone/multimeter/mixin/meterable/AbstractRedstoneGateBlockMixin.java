package redstone.multimeter.mixin.meterable;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.block.RedstoneComponentBlock;
import net.minecraft.world.World;

import redstone.multimeter.block.MeterableBlock;

@Mixin(RedstoneComponentBlock.class)
public abstract class AbstractRedstoneGateBlockMixin implements MeterableBlock {
	
	@Shadow @Final private boolean field_5539;
	
	@Shadow protected abstract boolean method_4754(World world, int x, int y, int z, int metadata);
	
	@Inject(
			method = "method_4754",
			at = @At(
					value = "RETURN"
			)
	)
	private void onPowerCheck(World world, int x, int y, int z, int meta, CallbackInfoReturnable<Boolean> cir) {
		logPowered(world, x, y, z, cir.getReturnValue()); // repeaters
	}
	
	@Override
	public boolean logPoweredOnBlockUpdate() {
		return false;
	}
	
	@Override
	public boolean isPowered(World world, int x, int y, int z, int metadata) {
		return method_4754(world, x, y, z, metadata);
	}
	
	@Override
	public boolean isActive(World world, int x, int y, int z, int metadata) {
		return field_5539;
	}
}
