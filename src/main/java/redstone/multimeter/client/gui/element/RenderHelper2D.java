package redstone.multimeter.client.gui.element;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.text.ITextComponent;

import redstone.multimeter.client.gui.Texture;
import redstone.multimeter.client.gui.TextureRegion;
import redstone.multimeter.util.ColorUtils;

public class RenderHelper2D {
	
	protected void renderRect(Drawer drawer) {
		GlStateManager.enableBlend();
		GlStateManager.disableTexture2D();
		GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
		GlStateManager.color(1.0F, 1.0F, 1.0F);
		GlStateManager.shadeModel(GL11.GL_SMOOTH);
		
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBuffer();
		
		bufferBuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
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
		
		bufferBuilder.pos(x0, y0, z).color(r, g, b, a).endVertex();
		bufferBuilder.pos(x0, y1, z).color(r, g, b, a).endVertex();
		bufferBuilder.pos(x1, y1, z).color(r, g, b, a).endVertex();
		bufferBuilder.pos(x1, y0, z).color(r, g, b, a).endVertex();
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
		
		bufferBuilder.pos(x0, y0, z).color(r0, g0, b0, a0).endVertex();
		bufferBuilder.pos(x0, y1, z).color(r1, g1, b1, a1).endVertex();
		bufferBuilder.pos(x1, y1, z).color(r1, g1, b1, a1).endVertex();
		bufferBuilder.pos(x1, y0, z).color(r0, g0, b0, a0).endVertex();
	}
	
	protected void renderBorder(int x, int y, int width, int height, int color) {
		renderBorder(x, y, width, height, 1, color);
	}
	
	protected void renderBorder(int x, int y, int width, int height, int d, int color) {
		int left = x;
		int right = x + width;
		int top = y;
		int bottom = y + height;
		
		renderRect(bufferBuilder -> {
			drawRect(bufferBuilder, left     , top       , d        , height - d, color); // left
			drawRect(bufferBuilder, left     , bottom - d, width - d, d         , color); // bottom
			drawRect(bufferBuilder, right - d, top    + d, d        , height - d, color); // right
			drawRect(bufferBuilder, left  + d, top       , width - d, d         , color); // top
		});
	}
	
	protected void renderTexture(Texture texture, Drawer drawer) {
		Minecraft.getMinecraft().getTextureManager().bindTexture(texture.id);
		GlStateManager.enableBlend();
		GlStateManager.enableTexture2D();
		GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
		GlStateManager.color(1.0F, 1.0F, 1.0F);
		
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBuffer();
		
		bufferBuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
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
		
		bufferBuilder.pos(x0, y0, z).tex(u0, v0).endVertex();
		bufferBuilder.pos(x0, y1, z).tex(u0, v1).endVertex();
		bufferBuilder.pos(x1, y1, z).tex(u1, v1).endVertex();
		bufferBuilder.pos(x1, y0, z).tex(u1, v0).endVertex();
	}
	
	protected void renderTextureColor(Texture texture, Drawer drawer) {
		Minecraft.getMinecraft().getTextureManager().bindTexture(texture.id);
		GlStateManager.enableBlend();
		GlStateManager.enableTexture2D();
		GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
		GlStateManager.color(1.0F, 1.0F, 1.0F);
		GlStateManager.color(1.0F, 1.0F, 1.0F);
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBuffer();
		
		bufferBuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
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
		
		bufferBuilder.pos(x0, y0, z).tex(u0, v0).color(r, g, b, a).endVertex();
		bufferBuilder.pos(x0, y1, z).tex(u0, v1).color(r, g, b, a).endVertex();
		bufferBuilder.pos(x1, y1, z).tex(u1, v1).color(r, g, b, a).endVertex();
		bufferBuilder.pos(x1, y0, z).tex(u1, v0).color(r, g, b, a).endVertex();
	}
	
	protected int getWidth(FontRenderer font, ITextComponent text) {
		return font.getStringWidth(text.getFormattedText());
	}
	
	protected void renderText(FontRenderer font, ITextComponent text, int x, int y, boolean shadow, int color) {
		GlStateManager.enableTexture2D();
		
		if (shadow) {
			font.drawStringWithShadow(text.getFormattedText(), x, y, color);
		} else {
			font.drawString(text.getFormattedText(), x, y, color);
		}
	}
	
	protected void renderText(FontRenderer font, String text, int x, int y, boolean shadow, int color) {
		GlStateManager.enableTexture2D();
		
		if (shadow) {
			font.drawStringWithShadow(text, x, y, color);
		} else {
			font.drawString(text, x, y, color);
		}
	}
	
	@FunctionalInterface
	protected interface Drawer {
		
		public void draw(BufferBuilder bufferBuilder);
		
	}
}
