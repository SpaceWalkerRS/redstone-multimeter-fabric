package redstone.multimeter.client.gui.element;

import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.platform.GlStateManager;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.text.Text;

import redstone.multimeter.client.gui.Texture;
import redstone.multimeter.client.gui.TextureRegion;
import redstone.multimeter.util.ColorUtils;

public class RenderHelper2D {
	
	protected void renderRect(Drawer drawer) {
		GlStateManager.enableBlend();
		GlStateManager.disableTexture();
		GlStateManager.blendFuncSeparate(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SrcFactor.ONE, GlStateManager.DstFactor.ZERO);
		GlStateManager.shadeModel(GL11.GL_SMOOTH);
		
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBuffer();
		
		bufferBuilder.begin(GL11.GL_QUADS, VertexFormats.POSITION_COLOR);
		drawer.draw(bufferBuilder);
		tessellator.draw();
	}
	
	protected void renderRect(int x, int y, int width, int height, int color) {
		renderRect(bufferBuilder -> drawRect(bufferBuilder, x, y, width, height, color));
	}
	
	protected void renderRect(int x0, int y0, int x1, int y1, int a, int r, int g, int b) {
		renderRect(bufferBuilder -> drawRect(bufferBuilder, x0, y0, x1, y1, a, r, g, b));
	}
	
	protected void renderGradient(int x, int y, int width, int height, int color0, int color1) {
		renderRect(bufferBuilder -> drawGradient(bufferBuilder, x, y, width, height, color0, color1));
	}
	
	protected void renderGradient(int x0, int y0, int x1, int y1, int a0, int r0, int g0, int b0, int a1, int r1, int g1, int b1) {
		renderRect(bufferBuilder -> drawGradient(bufferBuilder, x0, y0, x1, y1, a0, r0, g0, b0, a1, r1, g1, b1));
	}
	
	protected void drawRect(BufferBuilder bufferBuilder, int x, int y, int width, int height, int color) {
		int x0 = x;
		int y0 = y;
		int x1 = x + width;
		int y1 = y + height;
		
		int a = ColorUtils.getAlpha(color);
		int r = ColorUtils.getRed(color);
		int g = ColorUtils.getGreen(color);
		int b = ColorUtils.getBlue(color);
		
		drawRect(bufferBuilder, x0, y0, x1, y1, a, r, g, b);
	}
	
	protected void drawRect(BufferBuilder bufferBuilder, int x0, int y0, int x1, int y1, int a, int r, int g, int b) {
		int z = 0;
		
		bufferBuilder.vertex(x0, y0, z).color(r, g, b, a).next();
		bufferBuilder.vertex(x0, y1, z).color(r, g, b, a).next();
		bufferBuilder.vertex(x1, y1, z).color(r, g, b, a).next();
		bufferBuilder.vertex(x1, y0, z).color(r, g, b, a).next();
	}
	
	protected void drawGradient(BufferBuilder bufferBuilder, int x, int y, int width, int height, int color0, int color1) {
		int x0 = x;
		int y0 = y;
		int x1 = x + width;
		int y1 = y + height;
		
		int a0 = ColorUtils.getAlpha(color0);
		int r0 = ColorUtils.getRed(color0);
		int g0 = ColorUtils.getGreen(color0);
		int b0 = ColorUtils.getBlue(color0);
		
		int a1 = ColorUtils.getAlpha(color1);
		int r1 = ColorUtils.getRed(color1);
		int g1 = ColorUtils.getGreen(color1);
		int b1 = ColorUtils.getBlue(color1);
		
		drawGradient(bufferBuilder, x0, y0, x1, y1, a0, r0, g0, b0, a1, r1, g1, b1);
	}
	
	protected void drawGradient(BufferBuilder bufferBuilder, int x0, int y0, int x1, int y1, int a0, int r0, int g0, int b0, int a1, int r1, int g1, int b1) {
		int z = 0;
		
		bufferBuilder.vertex(x0, y0, z).color(r0, g0, b0, a0).next();
		bufferBuilder.vertex(x0, y1, z).color(r1, g1, b1, a1).next();
		bufferBuilder.vertex(x1, y1, z).color(r1, g1, b1, a1).next();
		bufferBuilder.vertex(x1, y0, z).color(r0, g0, b0, a0).next();
	}
	
	protected void renderTexture(Texture texture, Drawer drawer) {
		MinecraftClient.getInstance().getTextureManager().bindTexture(texture.id);
		GlStateManager.enableBlend();
		GlStateManager.enableTexture();
		GlStateManager.blendFuncSeparate(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SrcFactor.ONE, GlStateManager.DstFactor.ZERO);
		
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBuffer();
		
		bufferBuilder.begin(GL11.GL_QUADS, VertexFormats.POSITION_TEXTURE_F);
		drawer.draw(bufferBuilder);
		tessellator.draw();
	}
	
	protected void renderTextureRegion(TextureRegion region, int x, int y, int width, int height) {
		renderTexture(region.texture, bufferBuilder -> drawTextureRegion(bufferBuilder, region, x, y, width, height));
	}
	
	protected void renderTexture(Texture texture, int x0, int y0, int x1, int y1, int tx0, int ty0, int tx1, int ty1) {
		renderTexture(texture, bufferBuilder -> drawTexture(bufferBuilder, texture, x0, y0, x1, y1, tx0, ty0, tx1, ty1));
	}
	
	protected void drawTextureRegion(BufferBuilder bufferBuilder, TextureRegion region, int x, int y, int width, int height) {
		int x0 = x;
		int y0 = y;
		int x1 = x + width;
		int y1 = y + height;
		
		int tx0 = region.x;
		int ty0 = region.y;
		int tx1 = region.x + region.width;
		int ty1 = region.y + region.height;
		
		drawTexture(bufferBuilder, region.texture, x0, y0, x1, y1, tx0, ty0, tx1, ty1);
	}
	
	protected void drawTexture(BufferBuilder bufferBuilder, Texture texture, int x0, int y0, int x1, int y1, int tx0, int ty0, int tx1, int ty1) {
		int z = 0;
		
		float u0 = (float)tx0 / texture.width;
		float v0 = (float)ty0 / texture.height;
		float u1 = (float)tx1 / texture.width;
		float v1 = (float)ty1 / texture.height;
		
		bufferBuilder.vertex(x0, y0, z).texture(u0, v0).next();
		bufferBuilder.vertex(x0, y1, z).texture(u0, v1).next();
		bufferBuilder.vertex(x1, y1, z).texture(u1, v1).next();
		bufferBuilder.vertex(x1, y0, z).texture(u1, v0).next();
	}
	
	protected void renderTextureColor(Texture texture, Drawer drawer) {
		MinecraftClient.getInstance().getTextureManager().bindTexture(texture.id);
		GlStateManager.enableBlend();
		GlStateManager.enableTexture();
		GlStateManager.blendFuncSeparate(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SrcFactor.ONE, GlStateManager.DstFactor.ZERO);
		
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBuffer();
		
		bufferBuilder.begin(GL11.GL_QUADS, VertexFormats.POSITION_COLOR_TEXTURE_F);
		drawer.draw(bufferBuilder);
		tessellator.draw();
	}
	
	protected void renderTextureRegionColor(TextureRegion region, int x, int y, int width, int height, int color) {
		renderTextureColor(region.texture, bufferBuilder -> drawTextureRegionColor(bufferBuilder, region, x, y, width, height, color));
	}
	
	protected void renderTextureColor(Texture texture, int x0, int y0, int x1, int y1, int tx0, int ty0, int tx1, int ty1, int a, int r, int g, int b) {
		renderTextureColor(texture, bufferBuilder -> drawTextureColor(bufferBuilder, texture, x0, y0, x1, y1, tx0, ty0, tx1, ty1, a, r, g, b));
	}
	
	protected void drawTextureRegionColor(BufferBuilder bufferBuilder, TextureRegion region, int x, int y, int width, int height, int color) {
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
		
		drawTextureColor(bufferBuilder, region.texture, x0, y0, x1, y1, tx0, ty0, tx1, ty1, a, r, g, b);
	}
	
	protected void drawTextureColor(BufferBuilder bufferBuilder, Texture texture, int x0, int y0, int x1, int y1, int tx0, int ty0, int tx1, int ty1, int a, int r, int g, int b) {
		int z = 0;
		
		float u0 = (float)tx0 / texture.width;
		float v0 = (float)ty0 / texture.height;
		float u1 = (float)tx1 / texture.width;
		float v1 = (float)ty1 / texture.height;
		
		bufferBuilder.vertex(x0, y0, z).texture(u0, v0).color(r, g, b, a).next();
		bufferBuilder.vertex(x0, y1, z).texture(u0, v1).color(r, g, b, a).next();
		bufferBuilder.vertex(x1, y1, z).texture(u1, v1).color(r, g, b, a).next();
		bufferBuilder.vertex(x1, y0, z).texture(u1, v0).color(r, g, b, a).next();
	}
	
	protected int getWidth(TextRenderer font, Text text) {
		return font.getWidth(text.toFormattedString());
	}
	
	protected void renderText(TextRenderer font, Text text, int x, int y, boolean shadow, int color) {
		GlStateManager.enableTexture();
		
		if (shadow) {
			font.drawWithShadow(text.toFormattedString(), x, y, color);
		} else {
			font.draw(text.toFormattedString(), x, y, color);
		}
	}
	
	protected void renderText(TextRenderer font, String text, int x, int y, boolean shadow, int color) {
		GlStateManager.enableTexture();
		
		if (shadow) {
			font.drawWithShadow(text, x, y, color);
		} else {
			font.draw(text, x, y, color);
		}
	}
	
	protected interface Drawer {
		
		public void draw(BufferBuilder bufferBuilder);
		
	}
}
