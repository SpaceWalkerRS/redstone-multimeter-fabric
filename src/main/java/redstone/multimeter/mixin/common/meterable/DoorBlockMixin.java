package redstone.multimeter.mixin.common.meterable;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.block.Block;
import net.minecraft.block.DoorBlock;
import net.minecraft.world.World;

import redstone.multimeter.block.MeterableBlock;

@Mixin(DoorBlock.class)
public class DoorBlockMixin implements MeterableBlock {

	@Inject(
		method = "neighborChanged",
		locals = LocalCapture.CAPTURE_FAILHARD,
		at = @At(
			value = "INVOKE",
			shift = Shift.BEFORE,
			target = "Lnet/minecraft/block/Block;isSignalSource()Z"
		)
	)
	private void logPowered(World world, int x, int y, int z, Block neighborBlock, CallbackInfo ci, int powered /* the fuck? */) {
		rsmm$logPowered(world, x, y, z, powered != 0);
		rsmm$logPowered(world, x, y + 1, z, powered != 0);
	}

	@Override
	public boolean rsmm$logPoweredOnBlockUpdate() {
		return false;
	}

	@Override
	public boolean rsmm$isPowered(World world, int x, int y, int z, int metadata) {
		return world.hasNeighborSignal(x, y, z) || world.hasNeighborSignal(x, rsmm$getOtherHalf(y, metadata), z);
	}

	@Override
	public boolean rsmm$isActive(World world, int x, int y, int z, int metadata) {
		return (metadata & 0b100) != 0;
	}

	private int rsmm$getOtherHalf(int y, int metadata) {
		return (metadata & 8) == 0 ? y + 1 : y - 1;
	}
}
