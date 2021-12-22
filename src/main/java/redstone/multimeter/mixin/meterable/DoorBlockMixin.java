package redstone.multimeter.mixin.meterable;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.block.Block;
import net.minecraft.block.DoorBlock;
import net.minecraft.world.World;

import redstone.multimeter.block.MeterableBlock;

@Mixin(DoorBlock.class)
public abstract class DoorBlockMixin implements MeterableBlock {
	
	@Inject(
			method = "method_408",
			locals = LocalCapture.CAPTURE_FAILHARD,
			at = @At(
					value = "HEAD"
			)
	)
	private void onNeighborUpdate(World world, int x, int y, int z, Block block, CallbackInfo ci) {
		int metadata = world.method_3777(x, y, z);
		boolean powered = isPowered(world, x, y, z, metadata);
		
		logPowered(world, x, y, z, powered);
		logPowered(world, x, getOtherHalf(y, metadata), z, powered);
	}
	
	@Override
	public boolean logPoweredOnBlockUpdate() {
		return false;
	}
	
	@Override
	public boolean isPowered(World world, int x, int y, int z, int metadata) {
		return world.method_3739(x, y, z) || world.method_3739(x, getOtherHalf(y, metadata), z);
	}
	
	@Override
	public boolean isActive(World world, int x, int y, int z, int metadata) {
		return (metadata & 4) != 0;
	}
	
	private int getOtherHalf(int y, int metadata) {
		return (metadata & 8) == 0 ? y + 1 : y - 1;
	}
}
