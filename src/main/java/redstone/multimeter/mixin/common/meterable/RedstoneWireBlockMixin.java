package redstone.multimeter.mixin.common.meterable;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.block.RedstoneWireBlock;
import net.minecraft.world.World;

import redstone.multimeter.block.MeterableBlock;
import redstone.multimeter.block.PowerSource;

@Mixin(RedstoneWireBlock.class)
public class RedstoneWireBlockMixin implements MeterableBlock, PowerSource {

	@Inject(
		method = "doUpdatePower",
		locals = LocalCapture.CAPTURE_FAILHARD,
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/World;setBlockMetadata(IIII)V"
		)
	)
	private void logPowered(World world, int x, int y, int z, int sourceX, int sourceY, int sourceZ, CallbackInfo cir, int oldPower, int receivedPower) {
		rsmm$logPowered(world, x, y, z, receivedPower > MIN_POWER);
	}

	@Override
	public boolean rsmm$logPoweredOnBlockUpdate() {
		return false;
	}

	// This method is only called on blocks where 'logPoweredOnBlockUpdate'
	// returns 'true', so it does not really matter that a potentially
	// incorrect value is returned.
	@Override
	public boolean rsmm$isPowered(World world, int x, int y, int z, int metadata) {
		return rsmm$isActive(world, 0, 0, 0, 0);
	}

	@Override
	public boolean rsmm$isActive(World world, int x, int y, int z, int metadata) {
		return metadata > MIN_POWER;
	}

	@Override
	public int rsmm$getPowerLevel(World world, int x, int y, int z, int metadata) {
		return metadata;
	}
}
