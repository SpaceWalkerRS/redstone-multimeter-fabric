package redstone.multimeter.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.MultiBufferSource;

@Mixin(GuiGraphics.class)
public interface GuiGraphicsAccessor {

	@Accessor("bufferSource")
	MultiBufferSource.BufferSource rsmm$getBufferSource();

}
