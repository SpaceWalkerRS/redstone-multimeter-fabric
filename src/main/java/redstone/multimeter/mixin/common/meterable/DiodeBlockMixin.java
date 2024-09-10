package redstone.multimeter.mixin.common.meterable;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.block.DiodeBlock;
import net.minecraft.world.World;

import redstone.multimeter.block.MeterableBlock;

@Mixin(DiodeBlock.class)
public class DiodeBlockMixin implements MeterableBlock {

	@Shadow @Final private boolean powered;

	@Shadow private boolean shouldBePowered(World world, int x, int y, int z, int metadata) { return false; }

	@Inject(
		method = "shouldBePowered",
		at = @At(
			value = "RETURN"
		)
	)
	private void logPowered(World world, int x, int y, int z, int metadata, CallbackInfoReturnable<Boolean> cir) {
		rsmm$logPowered(world, 0, 0, 0, cir.getReturnValue()); // repeaters
	}

	@Override
	public boolean rsmm$logPoweredOnBlockUpdate() {
		return false;
	}

	@Override
	public boolean rsmm$isPowered(World world, int x, int y, int z, int metadata) {
		return shouldBePowered(world, x, y, z, metadata);
	}

	@Override
	public boolean rsmm$isActive(World world, int x, int y, int z, int metadata) {
		return powered;
	}
}
