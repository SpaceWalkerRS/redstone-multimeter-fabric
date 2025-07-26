package redstone.multimeter.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import net.minecraft.client.gui.render.state.BlitRenderState;
import net.minecraft.client.gui.render.state.ColoredRectangleRenderState;
import net.minecraft.client.gui.render.state.GlyphEffectRenderState;
import net.minecraft.client.gui.render.state.GlyphRenderState;

import redstone.multimeter.interfaces.mixin.IGuiElementRenderState;

@Mixin(value = {
	BlitRenderState.class,
	ColoredRectangleRenderState.class,
	GlyphEffectRenderState.class,
	GlyphRenderState.class
})
public class GuiElementRenderStateMixin implements IGuiElementRenderState {

	@Unique
	private Float depth;

	@Override
	public void rsmm$overrideDepth(float depth) {
		this.depth = depth;
	}

	@Override
	public Float rsmm$depthOverride() {
		return this.depth;
	}

	@ModifyVariable(
		method = "buildVertices",
		argsOnly = true,
		at = @At(
			value = "HEAD"
		)
	)
	private float overrideDepth(float depth) {
		return this.depth == null ? depth : this.depth;
	}
}
