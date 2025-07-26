package redstone.multimeter.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.gui.render.state.BlitRenderState;
import net.minecraft.client.gui.render.state.GuiElementRenderState;
import net.minecraft.client.gui.render.state.GuiItemRenderState;
import net.minecraft.client.gui.render.state.GuiRenderState;
import net.minecraft.client.gui.render.state.GuiTextRenderState;

import redstone.multimeter.client.gui.DepthOverride;
import redstone.multimeter.interfaces.mixin.IGuiElementRenderState;

@Mixin(GuiRenderState.class)
public class GuiRenderStateMixin {

	@Inject(
		method = "submitItem",
		at = @At(
			value = "HEAD"
		)
	)
	private void depthOverride(GuiItemRenderState element, CallbackInfo ci) {
		this.depthOverride(element);
	}

	@Inject(
		method = "submitText",
		at = @At(
			value = "HEAD"
		)
	)
	private void depthOverride(GuiTextRenderState element, CallbackInfo ci) {
		this.depthOverride(element);
	}

	@Inject(
		method = "submitGuiElement",
		at = @At(
			value = "HEAD"
		)
	)
	private void depthOverride(GuiElementRenderState element, CallbackInfo ci) {
		this.depthOverride(element);
	}

	@Inject(
		method = "submitBlitToCurrentLayer",
		at = @At(
			value = "HEAD"
		)
	)
	private void depthOverride(BlitRenderState element, CallbackInfo ci) {
		this.depthOverride(element);
	}

	@Inject(
		method = "submitGlyphToCurrentLayer",
		at = @At(
			value = "HEAD"
		)
	)
	private void depthOverrideGlyph(GuiElementRenderState element, CallbackInfo ci) {
		this.depthOverride(element);
	}

	@Unique
	private void depthOverride(Object element) {
		Float override = DepthOverride.peek();

		if (override != null) {
			((IGuiElementRenderState) element).rsmm$overrideDepth(override);
		}
	}
}
