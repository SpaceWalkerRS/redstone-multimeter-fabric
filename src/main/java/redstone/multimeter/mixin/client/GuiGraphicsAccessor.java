package redstone.multimeter.mixin.client;

import java.util.function.Function;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

@Mixin(GuiGraphics.class)
public interface GuiGraphicsAccessor {

	@Accessor("bufferSource")
	MultiBufferSource.BufferSource rsmm$getBufferSource();

	@Invoker("innerBlit")
	void rsmm$innerBlit(Function<ResourceLocation, RenderType> renderType, ResourceLocation location, int x0, int x1, int y0, int y1, float u0, float u1, float v0, float v1, int color);

}
