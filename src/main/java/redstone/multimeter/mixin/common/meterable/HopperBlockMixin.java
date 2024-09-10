package redstone.multimeter.mixin.common.meterable;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.block.HopperBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.HopperBlockEntity;
import net.minecraft.world.World;

import redstone.multimeter.block.MeterableBlock;
import redstone.multimeter.interfaces.mixin.IHopperBlockEntity;

@Mixin(HopperBlock.class)
public class HopperBlockMixin implements MeterableBlock {

	@Inject(
		method = "updateEnabled",
		locals = LocalCapture.CAPTURE_FAILHARD,
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/block/HopperBlock;isEnabled(I)Z"
		)
	)
	private void logPowered(World world, int x, int y, int z, CallbackInfo ci, int metadata, int facing, int shouldBeEnabled /* the fuck? */) {
		rsmm$logPowered(world, x, y, z, shouldBeEnabled == 0);
	}

	@Override
	public boolean rsmm$logPoweredOnBlockUpdate() {
		return false;
	}

	@Override
	public boolean rsmm$isActive(World world, int x, int y, int z, int metadata) {
		BlockEntity blockEntity = world.getBlockEntity(x, y, z);

		if (blockEntity instanceof HopperBlockEntity) {
			return !((IHopperBlockEntity)blockEntity).rsmm$isOnCooldown();
		}

		return false;
	}
}
