package redstone.multimeter.mixin.common.meterable;

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
public class RedstoneTorchBlockMixin implements MeterableBlock, PowerSource {

	@Shadow @Final private boolean lit;

	@Shadow private boolean hasNeighborSignal(World world, int x, int y, int z) { return false; }

	@Inject(
		method = "hasNeighborSignal",
		at = @At(
			value = "RETURN"
		)
	)
	private void logPowered(World world, int x, int y, int z, CallbackInfoReturnable<Boolean> cir) {
		rsmm$logPowered(world, x, y, z, cir.getReturnValue());
	}

	@Override
	public boolean rsmm$logPoweredOnBlockUpdate() {
		return false;
	}

	@Override
	public boolean rsmm$isPowered(World world, int x, int y, int z, int metadata) {
		return hasNeighborSignal(world, x, y, z);
	}

	@Override
	public boolean rsmm$isActive(World world, int x, int y, int z, int metadata) {
		return lit;
	}

	@Override
	public int rsmm$getPowerLevel(World world, int x, int y, int z, int metadata) {
		return lit ? MAX_POWER : MIN_POWER;
	}
}
