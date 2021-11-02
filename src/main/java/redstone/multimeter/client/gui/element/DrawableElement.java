package redstone.multimeter.client.gui.element;

import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Matrix4f;

import redstone.multimeter.util.ColorUtils;

public abstract class DrawableElement extends DrawableHelper {
	
	protected void drawTexture(MatrixStack matrices, TextureRegion region, int x, int y, int width, int height) {
		int x0 = x;
		int y0 = y;
		int x1 = x + width;
		int y1 = y + height;
		
		int tx0 = region.x;
		int ty0 = region.y;
		int tx1 = region.x + region.width;
		int ty1 = region.y + region.height;
		
		drawTexture(matrices, region.texture, x0, y0, x1, y1, tx0, ty0, tx1, ty1);
	}
	
	protected void drawTexture(MatrixStack matrices, Texture texture, int x0, int y0, int x1, int y1, int tx0, int ty0, int tx1, int ty1) {
		float u0 = (float)tx0 / texture.width;
		float v0 = (float)ty0 / texture.height;
		float u1 = (float)tx1 / texture.width;
		float v1 = (float)ty1 / texture.height;
		
		drawTexture(matrices, texture, x0, y0, x1, y1, u0, v0, u1, v1);
	}
	
	protected void drawTexture(MatrixStack matrices, Texture texture, int x0, int y0, int x1, int y1, float u0, float v0, float u1, float v1) {
		drawTexture(matrices, texture, x0, y0, getZOffset(), x1, y1, u0, v0, u1, v1);
	}
	
	protected static void drawTexture(MatrixStack matrices, Texture texture, int x0, int y0, int z, int x1, int y1, float u0, float v0, float u1, float v1) {
		RenderSystem.setShader(() -> GameRenderer.getPositionTexShader());
		RenderSystem.setShaderTexture(0, texture.id);
		RenderSystem.enableTexture();
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		RenderSystem.enableDepthTest();
		
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBuffer();
		Matrix4f model = matrices.peek().getModel();
		
		bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
		
		bufferBuilder.vertex(model, x0, y0, z).texture(u0, v0).next();
		bufferBuilder.vertex(model, x0, y1, z).texture(u0, v1).next();
		bufferBuilder.vertex(model, x1, y1, z).texture(u1, v1).next();
		bufferBuilder.vertex(model, x1, y0, z).texture(u1, v0).next();
		
		tessellator.draw();
		
		RenderSystem.disableTexture();
		RenderSystem.disableBlend();
		RenderSystem.disableDepthTest();
	}
	
	protected void drawTextureColor(MatrixStack matrices, TextureRegion region, int x, int y, int width, int height, int color) {
		int x0 = x;
		int y0 = y;
		int x1 = x + width;
		int y1 = y + height;
		
		int tx0 = region.x;
		int ty0 = region.y;
		int tx1 = region.x + region.width;
		int ty1 = region.y + region.height;
		
		int a = ColorUtils.getAlpha(color);
		int r = ColorUtils.getRed(color);
		int g = ColorUtils.getGreen(color);
		int b = ColorUtils.getBlue(color);
		
		drawTextureColor(matrices, region.texture, x0, y0, x1, y1, tx0, ty0, tx1, ty1, a, r, g, b);
	}
	
	protected void drawTextureColor(MatrixStack matrices, Texture texture, int x0, int y0, int x1, int y1, int tx0, int ty0, int tx1, int ty1, int a, int r, int g, int b) {
		float u0 = (float)tx0 / texture.width;
		float v0 = (float)ty0 / texture.height;
		float u1 = (float)tx1 / texture.width;
		float v1 = (float)ty1 / texture.height;
		
		drawTextureColor(matrices, texture, x0, y0, x1, y1, u0, v0, u1, v1, a, r, g, b);
	}
	
	protected void drawTextureColor(MatrixStack matrices, Texture texture, int x0, int y0, int x1, int y1, float u0, float v0, float u1, float v1, int a, int r, int g, int b) {
		drawTextureColor(matrices, texture, x0, y0, getZOffset(), x1, y1, u0, v0, u1, v1, a, r, g, b);
	}
	
	protected static void drawTextureColor(MatrixStack matrices, Texture texture, int x0, int y0, int z, int x1, int y1, float u0, float v0, float u1, float v1, int a, int r, int g, int b) {
		RenderSystem.setShader(() -> GameRenderer.getPositionTexColorShader());
		RenderSystem.setShaderTexture(0, texture.id);
		RenderSystem.enableTexture();
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		RenderSystem.enableDepthTest();
		RenderSystem.depthFunc(GL11.GL_ALWAYS);
		
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBuffer();
		Matrix4f model = matrices.peek().getModel();
		
		bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
		
		bufferBuilder.vertex(model, x0, y0, z).texture(u0, v0).color(r, g, b, a).next();
		bufferBuilder.vertex(model, x0, y1, z).texture(u0, v1).color(r, g, b, a).next();
		bufferBuilder.vertex(model, x1, y1, z).texture(u1, v1).color(r, g, b, a).next();
		bufferBuilder.vertex(model, x1, y0, z).texture(u1, v0).color(r, g, b, a).next();
		
		tessellator.draw();
		
		RenderSystem.disableTexture();
		RenderSystem.disableBlend();
		RenderSystem.disableDepthTest();
	}
}
