package redstone.multimeter.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.gui.render.state.GuiTextRenderState;

import redstone.multimeter.client.gui.DepthOverride;
import redstone.multimeter.interfaces.mixin.IGuiElementRenderState;

@Mixin(net.minecraft.client.gui.render.GuiRenderer.class)
public class GuiRendererMixin {

	@Inject(
		method = "method_71058", // lambda in prepareText
		at = @At(
			value = "HEAD"
		)
	)
	private void depthOverride(GuiTextRenderState text, CallbackInfo ci) {
		DepthOverride.push();
		DepthOverride.translate(((IGuiElementRenderState) (Object) text).rsmm$depthOverride());
	}

	@Inject(
		method = "method_71058", // lambda in prepareText
		at = @At(
			value = "TAIL"
		)
	)
	private void depthOverride(CallbackInfo ci) {
		DepthOverride.pop();
	}
}
