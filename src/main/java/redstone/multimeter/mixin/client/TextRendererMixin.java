package redstone.multimeter.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.client.render.TextRenderer;

import redstone.multimeter.client.gui.FontRenderer;

@Mixin(TextRenderer.class)
public class TextRendererMixin {

	@Unique
	private boolean zeroCharWidth;

	@Inject(
		method = "getWidth(Ljava/lang/String;)I",
		at = @At(
			value = "INVOKE",
			ordinal = 1,
			target = "Lnet/minecraft/client/render/TextRenderer;getWidth(C)I"
		)
	)
	private void beforeGetCharWidth(CallbackInfoReturnable<Integer> cir) {
		zeroCharWidth = FontRenderer.fixFormattedTextWidth();
	}

	@Inject(
		method = "getWidth(Ljava/lang/String;)I",
		at = @At(
			value = "INVOKE",
			shift = Shift.AFTER,
			target = "Lnet/minecraft/client/render/TextRenderer;getWidth(C)I"
		)
	)
	private void afterGetCharWidth(CallbackInfoReturnable<Integer> cir) {
		zeroCharWidth = false;
	}

	@Inject(
		method = "getWidth(C)I",
		cancellable = true,
		at = @At(
			value = "HEAD"
		)
	)
	private void modifyGetCharWidth(CallbackInfoReturnable<Integer> cir) {
		if (zeroCharWidth) {
			// fix width of formatting chars
			cir.setReturnValue(0);
		}
	}
}
