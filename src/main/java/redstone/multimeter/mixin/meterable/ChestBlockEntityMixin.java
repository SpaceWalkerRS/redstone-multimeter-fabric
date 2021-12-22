package redstone.multimeter.mixin.meterable;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.entity.player.PlayerEntity;

import redstone.multimeter.interfaces.mixin.IChestBlockEntity;

@Mixin(ChestBlockEntity.class)
public class ChestBlockEntityMixin implements IChestBlockEntity {
	
	@Inject(
			method = "onInvOpen",
			at = @At(
					value = "INVOKE",
					shift = Shift.BEFORE,
					target = "Lnet/minecraft/block/entity/ChestBlockEntity;method_16795()V"
			)
	)
	private void onOpenedByPlayer(PlayerEntity player, CallbackInfo ci) {
		invOpenOrClose(true);
	}
	
	@Inject(
			method = "onInvClose",
			at = @At(
					value = "INVOKE",
					shift = Shift.BEFORE,
					target = "Lnet/minecraft/block/entity/ChestBlockEntity;method_16795()V"
			)
	)
	private void onClosedByPlayer(PlayerEntity player, CallbackInfo ci) {
		invOpenOrClose(false);
	}
}
