package redstone.multimeter.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import net.minecraft.client.gui.render.state.GuiTextRenderState;

import redstone.multimeter.interfaces.mixin.IGuiElementRenderState;

@Mixin(GuiTextRenderState.class)
public class GuiTextRenderStateMixin implements IGuiElementRenderState {

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
}
