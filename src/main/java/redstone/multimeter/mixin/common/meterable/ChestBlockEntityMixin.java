package redstone.multimeter.mixin.common.meterable;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.entity.living.player.PlayerEntity;

import redstone.multimeter.interfaces.mixin.IChestBlockEntity;

@Mixin(ChestBlockEntity.class)
public class ChestBlockEntityMixin implements IChestBlockEntity {

	@Shadow private int viewerCount;

	@Inject(
		method = "onOpen",
		at = @At(
			value = "INVOKE",
			shift = Shift.BEFORE,
			target = "Lnet/minecraft/block/entity/ChestBlockEntity;notifyViewerCountChange()V"
		)
	)
	private void onOpen(PlayerEntity player, CallbackInfo ci) {
		signalViewerCount(viewerCount - 1, viewerCount);
	}

	@Inject(
		method = "onClose",
		at = @At(
			value = "INVOKE",
			shift = Shift.BEFORE,
			target = "Lnet/minecraft/block/entity/ChestBlockEntity;notifyViewerCountChange()V"
		)
	)
	private void onClose(PlayerEntity player, CallbackInfo ci) {
		signalViewerCount(viewerCount + 1, viewerCount);
	}

	@Override
	public void signalViewerCount(int oldViewerCount, int newViewerCount) {
	}
}
