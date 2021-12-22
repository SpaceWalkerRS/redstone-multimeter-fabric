package redstone.multimeter.mixin.meterable;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.block.RedstoneWireBlock;
import net.minecraft.world.World;

import redstone.multimeter.block.MeterableBlock;
import redstone.multimeter.block.PowerSource;

@Mixin(RedstoneWireBlock.class)
public abstract class RedstoneWireBlockMixin implements MeterableBlock, PowerSource {
	
	@Inject(
			method = "method_371",
			locals = LocalCapture.CAPTURE_FAILHARD,
			at = @At(
					value = "INVOKE",
					shift = Shift.BEFORE,
					target = "Lnet/minecraft/world/World;method_4718(IIIII)Z"
			)
	)
	private void onUpdateLogic(World world, int x, int y, int z, int _x, int _y, int _z, CallbackInfo ci, int metadata, int newPower) {
		logPowered(world, x, y, z, newPower > MIN_POWER);
	}
	
	@Override
	public boolean logPoweredOnBlockUpdate() {
		return false;
	}
	
	// This method is only called on blocks where 'logPoweredOnBlockUpdate'
	// returns 'true', so it does not really matter that a potentially
	// incorrect value is returned.
	@Override
	public boolean isPowered(World world, int x, int y, int z, int metadata) {
		return isActive(world, x, y, z, metadata);
	}
	
	@Override
	public boolean isActive(World world, int x, int y, int z, int metadata) {
		return metadata > MIN_POWER;
	}
	
	@Override
	public int getPowerLevel(World world, int x, int y, int z, int metadata) {
		return metadata;
	}
}
