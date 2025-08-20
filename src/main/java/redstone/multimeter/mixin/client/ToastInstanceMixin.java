package redstone.multimeter.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.llamalad7.mixinextras.sugar.Local;

import com.mojang.blaze3d.platform.GlStateManager;

import net.minecraft.client.gui.components.toasts.Toast;

import redstone.multimeter.client.gui.element.tutorial.TutorialToast;

@Mixin(targets = "net.minecraft.client.gui.components.toasts.ToastComponent$ToastInstance")
public class ToastInstanceMixin {

	@Shadow
	private Toast toast;

	@Shadow
	private float getVisibility(long timeMillis) { return 0.0F; }

	@Inject(
		method = "render",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/gui/components/toasts/Toast;render(Lnet/minecraft/client/gui/components/toasts/ToastComponent;J)Lnet/minecraft/client/gui/components/toasts/Toast$Visibility;"
		)
	)
	private void rsmm$customToastWidth(CallbackInfoReturnable<Boolean> cir, @Local long timeMillis) {
		if (this.toast instanceof TutorialToast) {
			TutorialToast t = (TutorialToast) this.toast;

			float visibility = this.getVisibility(timeMillis);
			float translate = (160 - t.width()) * visibility;

			GlStateManager.translatef(translate, 0.0F, 0.0F);
		}
	}
}
