package redstone.multimeter.client.gui.element;

import org.joml.Matrix4f;

import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.Font.DisplayMode;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.MultiBufferSource.BufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;

import redstone.multimeter.client.gui.Texture;
import redstone.multimeter.client.gui.TextureRegion;
import redstone.multimeter.mixin.client.GuiGraphicsAccessor;
import redstone.multimeter.util.ColorUtils;

public class RenderHelper2D {

	protected void renderRect(GuiGraphics graphics, Drawer drawer) {
		graphics.drawSpecial(bufferSource -> {
			VertexConsumer buffer = bufferSource.getBuffer(RenderType.gui());
			Matrix4f pose = graphics.pose().last().pose();

			drawer.draw(buffer, pose);
		});
	}

	protected void renderRect(GuiGraphics graphics, int x, int y, int width, int height, int color) {
		renderRect(graphics, (bufferBuilder, pose) -> drawRect(bufferBuilder, pose, x, y, width, height, color));
	}

	protected void renderRect(GuiGraphics graphics, int x0, int y0, int x1, int y1, int a, int r, int g, int b) {
		renderRect(graphics, (bufferBuilder, pose) -> drawRect(bufferBuilder, pose, x0, y0, x1, y1, a, r, g, b));
	}

	protected void renderGradient(GuiGraphics graphics, int x, int y, int width, int height, int color0, int color1) {
		renderRect(graphics, (bufferBuilder, pose) -> drawGradient(bufferBuilder, pose, x, y, width, height, color0, color1));
	}

	protected void renderGradient(GuiGraphics graphics, int x0, int y0, int x1, int y1, int a0, int r0, int g0, int b0, int a1, int r1, int g1, int b1) {
		renderRect(graphics, (bufferBuilder, pose) -> drawGradient(bufferBuilder, pose, x0, y0, x1, y1, a0, r0, g0, b0, a1, r1, g1, b1));
	}

	protected void drawRect(VertexConsumer buffer, Matrix4f pose, int x, int y, int width, int height, int color) {
		int x0 = x;
		int y0 = y;
		int x1 = x + width;
		int y1 = y + height;

		int a = ColorUtils.getAlpha(color);
		int r = ColorUtils.getRed(color);
		int g = ColorUtils.getGreen(color);
		int b = ColorUtils.getBlue(color);

		drawRect(buffer, pose, x0, y0, x1, y1, a, r, g, b);
	}

	protected void drawRect(VertexConsumer buffer, Matrix4f pose, int x0, int y0, int x1, int y1, int a, int r, int g, int b) {
		int z = 0;

		buffer.addVertex(pose, x0, y0, z).setColor(r, g, b, a);
		buffer.addVertex(pose, x0, y1, z).setColor(r, g, b, a);
		buffer.addVertex(pose, x1, y1, z).setColor(r, g, b, a);
		buffer.addVertex(pose, x1, y0, z).setColor(r, g, b, a);
	}

	protected void drawGradient(VertexConsumer buffer, Matrix4f pose, int x, int y, int width, int height, int color0, int color1) {
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

		drawGradient(buffer, pose, x0, y0, x1, y1, a0, r0, g0, b0, a1, r1, g1, b1);
	}

	protected void drawGradient(VertexConsumer buffer, Matrix4f pose, int x0, int y0, int x1, int y1, int a0, int r0, int g0, int b0, int a1, int r1, int g1, int b1) {
		int z = 0;

		buffer.addVertex(pose, x0, y0, z).setColor(r0, g0, b0, a0);
		buffer.addVertex(pose, x0, y1, z).setColor(r1, g1, b1, a1);
		buffer.addVertex(pose, x1, y1, z).setColor(r1, g1, b1, a1);
		buffer.addVertex(pose, x1, y0, z).setColor(r0, g0, b0, a0);
	}

	protected void renderBorder(GuiGraphics graphics, int x, int y, int width, int height, int color) {
		renderBorder(graphics, x, y, width, height, 1, color);
	}

	protected void renderBorder(GuiGraphics graphics, int x, int y, int width, int height, int d, int color) {
		int left = x;
		int right = x + width;
		int top = y;
		int bottom = y + height;

		renderRect(graphics, (bufferBuilder, pose) -> {
			drawRect(bufferBuilder, pose, left     , top       , d        , height - d, color); // left
			drawRect(bufferBuilder, pose, left     , bottom - d, width - d, d         , color); // bottom
			drawRect(bufferBuilder, pose, right - d, top    + d, d        , height - d, color); // right
			drawRect(bufferBuilder, pose, left  + d, top       , width - d, d         , color); // top
		});
	}

	protected void renderTexture(GuiGraphics graphics, Texture texture, Drawer drawer) {
		graphics.drawSpecial(bufferSource -> {
			VertexConsumer buffer = bufferSource.getBuffer(RenderType.guiTextured(texture.location));
			Matrix4f pose = graphics.pose().last().pose();

			drawer.draw(buffer, pose);
		});
	}

	protected void renderTextureRegion(GuiGraphics graphics, TextureRegion region, int x, int y, int width, int height) {
		renderTexture(graphics, region.texture, (bufferBuilder, pose) -> drawTextureRegion(bufferBuilder, pose, region, x, y, width, height));
	}

	protected void renderTexture(GuiGraphics graphics, Texture texture, int x0, int y0, int x1, int y1, int tx0, int ty0, int tx1, int ty1) {
		renderTexture(graphics, texture, (bufferBuilder, pose) -> drawTexture(bufferBuilder, pose, texture, x0, y0, x1, y1, tx0, ty0, tx1, ty1));
	}

	protected void drawTextureRegion(VertexConsumer buffer, Matrix4f pose, TextureRegion region, int x, int y, int width, int height) {
		int x0 = x;
		int y0 = y;
		int x1 = x + width;
		int y1 = y + height;

		int tx0 = region.x;
		int ty0 = region.y;
		int tx1 = region.x + region.width;
		int ty1 = region.y + region.height;

		drawTexture(buffer, pose, region.texture, x0, y0, x1, y1, tx0, ty0, tx1, ty1);
	}

	protected void drawTexture(VertexConsumer buffer, Matrix4f pose, Texture texture, int x0, int y0, int x1, int y1, int tx0, int ty0, int tx1, int ty1) {
		int z = 0;

		float u0 = (float)tx0 / texture.width;
		float v0 = (float)ty0 / texture.height;
		float u1 = (float)tx1 / texture.width;
		float v1 = (float)ty1 / texture.height;

		buffer.addVertex(pose, x0, y0, z).setUv(u0, v0).setColor(0xFFFFFFFF);
		buffer.addVertex(pose, x0, y1, z).setUv(u0, v1).setColor(0xFFFFFFFF);
		buffer.addVertex(pose, x1, y1, z).setUv(u1, v1).setColor(0xFFFFFFFF);
		buffer.addVertex(pose, x1, y0, z).setUv(u1, v0).setColor(0xFFFFFFFF);
	}

	protected void renderTextureColor(GuiGraphics graphics, Texture texture, Drawer drawer) {
		graphics.drawSpecial(bufferSource -> {
			VertexConsumer buffer = bufferSource.getBuffer(RenderType.guiTextured(texture.location));
			Matrix4f pose = graphics.pose().last().pose();

			drawer.draw(buffer, pose);
		});
	}

	protected void renderTextureRegionColor(GuiGraphics graphics, TextureRegion region, int x, int y, int width, int height, int color) {
		renderTextureColor(graphics, region.texture, (bufferBuilder, pose) -> drawTextureRegionColor(bufferBuilder, pose, region, x, y, width, height, color));
	}

	protected void renderTextureColor(GuiGraphics graphics, Texture texture, int x0, int y0, int x1, int y1, int tx0, int ty0, int tx1, int ty1, int a, int r, int g, int b) {
		renderTextureColor(graphics, texture, (bufferBuilder, pose) -> drawTextureColor(bufferBuilder, pose, texture, x0, y0, x1, y1, tx0, ty0, tx1, ty1, a, r, g, b));
	}

	protected void drawTextureRegionColor(VertexConsumer buffer, Matrix4f pose, TextureRegion region, int x, int y, int width, int height, int color) {
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

		drawTextureColor(buffer, pose, region.texture, x0, y0, x1, y1, tx0, ty0, tx1, ty1, a, r, g, b);
	}

	protected void drawTextureColor(VertexConsumer buffer, Matrix4f pose, Texture texture, int x0, int y0, int x1, int y1, int tx0, int ty0, int tx1, int ty1, int a, int r, int g, int b) {
		int z = 0;

		float u0 = (float)tx0 / texture.width;
		float v0 = (float)ty0 / texture.height;
		float u1 = (float)tx1 / texture.width;
		float v1 = (float)ty1 / texture.height;

		buffer.addVertex(pose, x0, y0, z).setUv(u0, v0).setColor(r, g, b, a);
		buffer.addVertex(pose, x0, y1, z).setUv(u0, v1).setColor(r, g, b, a);
		buffer.addVertex(pose, x1, y1, z).setUv(u1, v1).setColor(r, g, b, a);
		buffer.addVertex(pose, x1, y0, z).setUv(u1, v0).setColor(r, g, b, a);
	}

	protected void renderText(GuiGraphics graphics, TextDrawer drawer) {
		BufferSource source = ((GuiGraphicsAccessor) graphics).rsmm$getBufferSource();
		Matrix4f pose = graphics.pose().last().pose();

		drawer.draw(source, pose);
		source.endBatch();
	}

	protected void renderText(Font font, GuiGraphics graphics, Component text, int x, int y, boolean shadow, int color) {
		renderText(graphics, (source, pose) -> drawText(source, pose, font, text, x, y, shadow, color));
	}

	protected void renderText(Font font, GuiGraphics graphics, String text, int x, int y, boolean shadow, int color) {
		renderText(graphics, (source, pose) -> drawText(source, pose, font, text, x, y, shadow, color));
	}

	protected void drawText(BufferSource source, Matrix4f pose, Font font, Component text, int x, int y, boolean shadow) {
		drawText(source, pose, font, text, x, y, shadow, 0xFFFFFFFF);
	}

	protected void drawText(BufferSource source, Matrix4f pose, Font font, Component text, int x, int y, boolean shadow, int color) {
		font.drawInBatch(text, x, y, color, shadow, pose, source, DisplayMode.NORMAL, 0x00000000, 0x00F000F0);
	}

	protected void drawText(BufferSource source, Matrix4f pose, Font font, String text, int x, int y, boolean shadow) {
		drawText(source, pose, font, text, x, y, shadow, 0xFFFFFFFF);
	}

	protected void drawText(BufferSource source, Matrix4f pose, Font font, String text, int x, int y, boolean shadow, int color) {
		font.drawInBatch(text, x, y, color, shadow, pose, source, DisplayMode.NORMAL, 0x00000000, 0x00F000F0);
	}

	@FunctionalInterface
	protected interface Drawer {

		public void draw(VertexConsumer buffer, Matrix4f pose);

	}

	@FunctionalInterface
	protected interface TextDrawer {

		public void draw(BufferSource source, Matrix4f pose);

	}
}
