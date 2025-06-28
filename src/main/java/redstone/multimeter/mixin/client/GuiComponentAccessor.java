package redstone.multimeter.mixin.client;

import org.joml.Matrix4f;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.client.gui.GuiComponent;

@Mixin(GuiComponent.class)
public interface GuiComponentAccessor {

	@Invoker("innerBlit")
	static void rsmm$innerBlit(Matrix4f pose, int x0, int x1, int y0, int y1, int z, float u0, float u1, float v0, float v1, float r, float g, float b, float a) {
		throw new UnsupportedOperationException();
	}
}
