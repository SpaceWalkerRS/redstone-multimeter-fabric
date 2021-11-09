package redstone.multimeter.client.gui.element;

import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.VertexConsumerProvider.Immediate;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.Matrix4f;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

import redstone.multimeter.client.gui.Texture;
import redstone.multimeter.client.gui.TextureRegion;
import redstone.multimeter.util.ColorUtils;

public class RenderHelper2D {
	
	protected void renderRect(MatrixStack matrices, Drawer drawer) {
		RenderSystem.enableBlend();
		RenderSystem.disableTexture();
		RenderSystem.defaultBlendFunc();
		RenderSystem.shadeModel(GL11.GL_SMOOTH);
		
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBuffer();
		Matrix4f model = matrices.peek().getModel();
		
		bufferBuilder.begin(GL11.GL_QUADS, VertexFormats.POSITION_COLOR);
		drawer.draw(bufferBuilder, model);
		tessellator.draw();
	}
	
	protected void renderRect(MatrixStack matrices, int x, int y, int width, int height, int color) {
		renderRect(matrices, (bufferBuilder, model) -> drawRect(bufferBuilder, model, x, y, width, height, color));
	}
	
	protected void renderRect(MatrixStack matrices, int x0, int y0, int x1, int y1, int a, int r, int g, int b) {
		renderRect(matrices, (bufferBuilder, model) -> drawRect(bufferBuilder, model, x0, y0, x1, y1, a, r, g, b));
	}
	
	protected void renderGradient(MatrixStack matrices, int x, int y, int width, int height, int color0, int color1) {
		renderRect(matrices, (bufferBuilder, model) -> drawGradient(bufferBuilder, model, x, y, width, height, color0, color1));
	}
	
	protected void renderGradient(MatrixStack matrices, int x0, int y0, int x1, int y1, int a0, int r0, int g0, int b0, int a1, int r1, int g1, int b1) {
		renderRect(matrices, (bufferBuilder, model) -> drawGradient(bufferBuilder, model, x0, y0, x1, y1, a0, r0, g0, b0, a1, r1, g1, b1));
	}
	
	protected void drawRect(BufferBuilder bufferBuilder, Matrix4f model, int x, int y, int width, int height, int color) {
		int x0 = x;
		int y0 = y;
		int x1 = x + width;
		int y1 = y + height;
		
		int a = ColorUtils.getAlpha(color);
		int r = ColorUtils.getRed(color);
		int g = ColorUtils.getGreen(color);
		int b = ColorUtils.getBlue(color);
		
		drawRect(bufferBuilder, model, x0, y0, x1, y1, a, r, g, b);
	}
	
	protected void drawRect(BufferBuilder bufferBuilder, Matrix4f model, int x0, int y0, int x1, int y1, int a, int r, int g, int b) {
		int z = 0;
		
		bufferBuilder.vertex(model, x0, y0, z).color(r, g, b, a).next();
		bufferBuilder.vertex(model, x0, y1, z).color(r, g, b, a).next();
		bufferBuilder.vertex(model, x1, y1, z).color(r, g, b, a).next();
		bufferBuilder.vertex(model, x1, y0, z).color(r, g, b, a).next();
	}
	
	protected void drawGradient(BufferBuilder bufferBuilder, Matrix4f model, int x, int y, int width, int height, int color0, int color1) {
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
		
		drawGradient(bufferBuilder, model, x0, y0, x1, y1, a0, r0, g0, b0, a1, r1, g1, b1);
	}
	
	protected void drawGradient(BufferBuilder bufferBuilder, Matrix4f model, int x0, int y0, int x1, int y1, int a0, int r0, int g0, int b0, int a1, int r1, int g1, int b1) {
		int z = 0;
		
		bufferBuilder.vertex(model, x0, y0, z).color(r0, g0, b0, a0).next();
		bufferBuilder.vertex(model, x0, y1, z).color(r1, g1, b1, a1).next();
		bufferBuilder.vertex(model, x1, y1, z).color(r1, g1, b1, a1).next();
		bufferBuilder.vertex(model, x1, y0, z).color(r0, g0, b0, a0).next();
	}
	
	protected void renderTexture(MatrixStack matrices, Texture texture, Drawer drawer) {
		MinecraftClient.getInstance().getTextureManager().bindTexture(texture.id);
		RenderSystem.enableTexture();
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBuffer();
		Matrix4f model = matrices.peek().getModel();
		
		bufferBuilder.begin(GL11.GL_QUADS, VertexFormats.POSITION_TEXTURE);
		drawer.draw(bufferBuilder, model);
		tessellator.draw();
	}
	
	protected void renderTextureRegion(MatrixStack matrices, TextureRegion region, int x, int y, int width, int height) {
		renderTexture(matrices, region.texture, (bufferBuilder, model) -> drawTextureRegion(bufferBuilder, model, region, x, y, width, height));
	}
	
	protected void renderTexture(MatrixStack matrices, Texture texture, int x0, int y0, int x1, int y1, int tx0, int ty0, int tx1, int ty1) {
		renderTexture(matrices, texture, (bufferBuilder, model) -> drawTexture(bufferBuilder, model, texture, x0, y0, x1, y1, tx0, ty0, tx1, ty1));
	}
	
	protected void drawTextureRegion(BufferBuilder bufferBuilder, Matrix4f model, TextureRegion region, int x, int y, int width, int height) {
		int x0 = x;
		int y0 = y;
		int x1 = x + width;
		int y1 = y + height;
		
		int tx0 = region.x;
		int ty0 = region.y;
		int tx1 = region.x + region.width;
		int ty1 = region.y + region.height;
		
		drawTexture(bufferBuilder, model, region.texture, x0, y0, x1, y1, tx0, ty0, tx1, ty1);
	}
	
	protected void drawTexture(BufferBuilder bufferBuilder, Matrix4f model, Texture texture, int x0, int y0, int x1, int y1, int tx0, int ty0, int tx1, int ty1) {
		int z = 0;
		
		float u0 = (float)tx0 / texture.width;
		float v0 = (float)ty0 / texture.height;
		float u1 = (float)tx1 / texture.width;
		float v1 = (float)ty1 / texture.height;
		
		bufferBuilder.vertex(model, x0, y0, z).texture(u0, v0).next();
		bufferBuilder.vertex(model, x0, y1, z).texture(u0, v1).next();
		bufferBuilder.vertex(model, x1, y1, z).texture(u1, v1).next();
		bufferBuilder.vertex(model, x1, y0, z).texture(u1, v0).next();
	}
	
	protected void renderTextureColor(MatrixStack matrices, Texture texture, Drawer drawer) {
		MinecraftClient.getInstance().getTextureManager().bindTexture(texture.id);
		RenderSystem.enableTexture();
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBuffer();
		Matrix4f model = matrices.peek().getModel();
		
		bufferBuilder.begin(GL11.GL_QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
		drawer.draw(bufferBuilder, model);
		tessellator.draw();
	}
	
	protected void renderTextureRegionColor(MatrixStack matrices, TextureRegion region, int x, int y, int width, int height, int color) {
		renderTextureColor(matrices, region.texture, (bufferBuilder, model) -> drawTextureRegionColor(bufferBuilder, model, region, x, y, width, height, color));
	}
	
	protected void renderTextureColor(MatrixStack matrices, Texture texture, int x0, int y0, int x1, int y1, int tx0, int ty0, int tx1, int ty1, int a, int r, int g, int b) {
		renderTextureColor(matrices, texture, (bufferBuilder, model) -> drawTextureColor(bufferBuilder, model, texture, x0, y0, x1, y1, tx0, ty0, tx1, ty1, a, r, g, b));
	}
	
	protected void drawTextureRegionColor(BufferBuilder bufferBuilder, Matrix4f model, TextureRegion region, int x, int y, int width, int height, int color) {
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
		
		drawTextureColor(bufferBuilder, model, region.texture, x0, y0, x1, y1, tx0, ty0, tx1, ty1, a, r, g, b);
	}
	
	protected void drawTextureColor(BufferBuilder bufferBuilder, Matrix4f model, Texture texture, int x0, int y0, int x1, int y1, int tx0, int ty0, int tx1, int ty1, int a, int r, int g, int b) {
		int z = 0;
		
		float u0 = (float)tx0 / texture.width;
		float v0 = (float)ty0 / texture.height;
		float u1 = (float)tx1 / texture.width;
		float v1 = (float)ty1 / texture.height;
		
		bufferBuilder.vertex(model, x0, y0, z).texture(u0, v0).color(r, g, b, a).next();
		bufferBuilder.vertex(model, x0, y1, z).texture(u0, v1).color(r, g, b, a).next();
		bufferBuilder.vertex(model, x1, y1, z).texture(u1, v1).color(r, g, b, a).next();
		bufferBuilder.vertex(model, x1, y0, z).texture(u1, v0).color(r, g, b, a).next();
	}
	
	protected int getWidth(TextRenderer font, Text text) {
		return font.getStringWidth(text.asFormattedString());
	}
	
	protected void renderText(MatrixStack matrices, TextDrawer drawer) {
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBuffer();
		Immediate immediate = VertexConsumerProvider.immediate(bufferBuilder);
		Matrix4f model = matrices.peek().getModel();
		
		drawer.draw(immediate, model);
		immediate.draw();
	}
	
	protected void renderText(TextRenderer font, MatrixStack matrices, Text text, int x, int y, boolean shadow, int color) {
		renderText(matrices, (immediate, model) -> drawText(immediate, model, font, text, x, y, shadow, color));
	}
	
	protected void renderText(TextRenderer font, MatrixStack matrices, String text, int x, int y, boolean shadow, int color) {
		renderText(matrices, (immediate, model) -> drawText(immediate, model, font, text, x, y, shadow, color));
	}
	
	protected void drawText(Immediate immediate, Matrix4f model, TextRenderer font, Text text, int x, int y, boolean shadow) {
		drawText(immediate, model, font, text, x, y, shadow, 0xFFFFFFFF);
	}
	
	protected void drawText(Immediate immediate, Matrix4f model, TextRenderer font, Text text, int x, int y, boolean shadow, int color) {
		font.draw(text.asFormattedString(), x, y, color, shadow, model, immediate, false, 0x00000000, 0x00F000F0);
	}
	
	protected void drawText(Immediate immediate, Matrix4f model, TextRenderer font, String text, int x, int y, boolean shadow) {
		drawText(immediate, model, font, text, x, y, shadow, 0xFFFFFFFF);
	}
	
	protected void drawText(Immediate immediate, Matrix4f model, TextRenderer font, String text, int x, int y, boolean shadow, int color) {
		font.draw(text, x, y, color, shadow, model, immediate, false, 0x00000000, 0x00F000F0);
	}
	
	protected interface Drawer {
		
		public void draw(BufferBuilder bufferBuilder, Matrix4f model);
		
	}
	
	protected interface TextDrawer {
		
		public void draw(Immediate immediate, Matrix4f model);
		
	}
}
