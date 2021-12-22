package redstone.multimeter.mixin.meterable;

import java.util.Random;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.block.RedstoneLampBlock;
import net.minecraft.world.World;

import redstone.multimeter.block.MeterableBlock;

@Mixin(RedstoneLampBlock.class)
public class RedstoneLampBlockMixin implements MeterableBlock {
	
	@Shadow @Final private boolean field_316;
	
	@Inject(
			method = "method_436",
			at = @At(
					value = "HEAD"
			)
	)
	private void onScheduledTick(World world, int x, int y, int z, Random random, CallbackInfo ci) {
		logPowered(world, x, y, z, world.method_3739(x, y, z));
	}
	
	@Override
	public boolean isActive(World world, int x, int y, int z, int metadata) {
		return field_316;
	}
}
