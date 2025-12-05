package redstone.multimeter.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import com.mojang.blaze3d.pipeline.RenderPipeline;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.Identifier;

@Mixin(GuiGraphics.class)
public interface GuiGraphicsAccessor {

	@Invoker("innerBlit")
	void rsmm$innerBlit(RenderPipeline pipeline, Identifier location, int x0, int x1, int y0, int y1, float u0, float u1, float v0, float v1, int color);

}
