package redstone.multimeter.client.gui.element;

import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.platform.GLX;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.text.Text;

import redstone.multimeter.client.gui.Texture;
import redstone.multimeter.client.gui.TextureRegion;
import redstone.multimeter.util.ColorUtils;

public class RenderHelper2D {
	
	protected void renderRect(Drawer drawer) {
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GLX.glBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
		GL11.glShadeModel(GL11.GL_SMOOTH);
		
		Tessellator tessellator = Tessellator.INSTANCE;
		
		tessellator.method_1405();
		drawer.draw(tessellator);
		tessellator.method_1396();
	}
	
	protected void renderRect(int x, int y, int width, int height, int color) {
		renderRect(tessellator -> drawRect(tessellator, x, y, width, height, color));
	}
	
	protected void renderRect(int x0, int y0, int x1, int y1, int a, int r, int g, int b) {
		renderRect(tessellator -> drawRect(tessellator, x0, y0, x1, y1, a, r, g, b));
	}
	
	protected void renderGradient(int x, int y, int width, int height, int color0, int color1) {
		renderRect(tessellator -> drawGradient(tessellator, x, y, width, height, color0, color1));
	}
	
	protected void renderGradient(int x0, int y0, int x1, int y1, int a0, int r0, int g0, int b0, int a1, int r1, int g1, int b1) {
		renderRect(tessellator -> drawGradient(tessellator, x0, y0, x1, y1, a0, r0, g0, b0, a1, r1, g1, b1));
	}
	
	protected void drawRect(Tessellator tessellator, int x, int y, int width, int height, int color) {
		int x0 = x;
		int y0 = y;
		int x1 = x + width;
		int y1 = y + height;
		
		int a = ColorUtils.getAlpha(color);
		int r = ColorUtils.getRed(color);
		int g = ColorUtils.getGreen(color);
		int b = ColorUtils.getBlue(color);
		
		drawRect(tessellator, x0, y0, x1, y1, a, r, g, b);
	}
	
	protected void drawRect(Tessellator tessellator, int x0, int y0, int x1, int y1, int a, int r, int g, int b) {
		int z = 0;
		
		tessellator.method_1404(r, g, b, a);
		tessellator.method_1398(x0, y0, z);
		tessellator.method_1398(x0, y1, z);
		tessellator.method_1398(x1, y1, z);
		tessellator.method_1398(x1, y0, z);
	}
	
	protected void drawGradient(Tessellator tessellator, int x, int y, int width, int height, int color0, int color1) {
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
		
		drawGradient(tessellator, x0, y0, x1, y1, a0, r0, g0, b0, a1, r1, g1, b1);
	}
	
	protected void drawGradient(Tessellator tessellator, int x0, int y0, int x1, int y1, int a0, int r0, int g0, int b0, int a1, int r1, int g1, int b1) {
		int z = 0;
		
		tessellator.method_1404(r0, g0, b0, a0);
		tessellator.method_1398(x1, y0, z);
		tessellator.method_1398(x0, y0, z);
		tessellator.method_1404(r1, g1, b1, a1);
		tessellator.method_1398(x0, y1, z);
		tessellator.method_1398(x1, y1, z);
	}
	
	protected void renderTexture(Texture texture, Drawer drawer) {
		MinecraftClient.getInstance().getTextureManager().bindTexture(texture.id);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GLX.glBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
		
		Tessellator tessellator = Tessellator.INSTANCE;
		
		tessellator.method_1405();
		drawer.draw(tessellator);
		tessellator.method_1396();
	}
	
	protected void renderTextureRegion(TextureRegion region, int x, int y, int width, int height) {
		renderTexture(region.texture, tessellator -> drawTextureRegion(tessellator, region, x, y, width, height));
	}
	
	protected void renderTexture(Texture texture, int x0, int y0, int x1, int y1, int tx0, int ty0, int tx1, int ty1) {
		renderTexture(texture, tessellator -> drawTexture(tessellator, texture, x0, y0, x1, y1, tx0, ty0, tx1, ty1));
	}
	
	protected void drawTextureRegion(Tessellator tessellator, TextureRegion region, int x, int y, int width, int height) {
		int x0 = x;
		int y0 = y;
		int x1 = x + width;
		int y1 = y + height;
		
		int tx0 = region.x;
		int ty0 = region.y;
		int tx1 = region.x + region.width;
		int ty1 = region.y + region.height;
		
		drawTexture(tessellator, region.texture, x0, y0, x1, y1, tx0, ty0, tx1, ty1);
	}
	
	protected void drawTexture(Tessellator tessellator, Texture texture, int x0, int y0, int x1, int y1, int tx0, int ty0, int tx1, int ty1) {
		int z = 0;
		
		float u0 = (float)tx0 / texture.width;
		float v0 = (float)ty0 / texture.height;
		float u1 = (float)tx1 / texture.width;
		float v1 = (float)ty1 / texture.height;
		
		tessellator.method_1399(x0, y0, z, u0, v0);
		tessellator.method_1399(x0, y1, z, u0, v1);
		tessellator.method_1399(x1, y1, z, u1, v1);
		tessellator.method_1399(x1, y0, z, u1, v0);
	}
	
	protected void renderTextureColor(Texture texture, Drawer drawer) {
		MinecraftClient.getInstance().getTextureManager().bindTexture(texture.id);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GLX.glBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
		
		Tessellator tessellator = Tessellator.INSTANCE;
		
		tessellator.method_1405();
		drawer.draw(tessellator);
		tessellator.method_1396();
	}
	
	protected void renderTextureRegionColor(TextureRegion region, int x, int y, int width, int height, int color) {
		renderTextureColor(region.texture, tessellator -> drawTextureRegionColor(tessellator, region, x, y, width, height, color));
	}
	
	protected void renderTextureColor(Texture texture, int x0, int y0, int x1, int y1, int tx0, int ty0, int tx1, int ty1, int a, int r, int g, int b) {
		renderTextureColor(texture, tessellator -> drawTextureColor(tessellator, texture, x0, y0, x1, y1, tx0, ty0, tx1, ty1, a, r, g, b));
	}
	
	protected void drawTextureRegionColor(Tessellator tessellator, TextureRegion region, int x, int y, int width, int height, int color) {
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
		
		drawTextureColor(tessellator, region.texture, x0, y0, x1, y1, tx0, ty0, tx1, ty1, a, r, g, b);
	}
	
	protected void drawTextureColor(Tessellator tessellator, Texture texture, int x0, int y0, int x1, int y1, int tx0, int ty0, int tx1, int ty1, int a, int r, int g, int b) {
		int z = 0;
		
		float u0 = (float)tx0 / texture.width;
		float v0 = (float)ty0 / texture.height;
		float u1 = (float)tx1 / texture.width;
		float v1 = (float)ty1 / texture.height;
		
		tessellator.method_1404(r, g, b, a);
		tessellator.method_1399(x0, y0, z, u0, v0);
		tessellator.method_1399(x0, y1, z, u0, v1);
		tessellator.method_1399(x1, y1, z, u1, v1);
		tessellator.method_1399(x1, y0, z, u1, v0);
	}
	
	protected int getWidth(TextRenderer font, Text text) {
		return font.getStringWidth(text.asFormattedString());
	}
	
	protected void renderText(TextRenderer font, Text text, int x, int y, boolean shadow, int color) {
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		
		if (shadow) {
			font.method_956(text.asFormattedString(), x, y, color);
		} else {
			font.draw(text.asFormattedString(), x, y, color);
		}
	}
	
	protected void renderText(TextRenderer font, String text, int x, int y, boolean shadow, int color) {
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		
		if (shadow) {
			font.method_956(text, x, y, color);
		} else {
			font.draw(text, x, y, color);
		}
	}
	
	protected interface Drawer {
		
		public void draw(Tessellator tessellator);
		
	}
}
