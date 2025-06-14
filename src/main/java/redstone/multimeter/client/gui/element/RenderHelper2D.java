package redstone.multimeter.client.gui.element;

import org.joml.Matrix4f;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.Font.DisplayMode;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource.BufferSource;
import net.minecraft.network.chat.Component;

import redstone.multimeter.client.gui.Texture;
import redstone.multimeter.client.gui.TextureRegion;
import redstone.multimeter.util.ColorUtils;

public class RenderHelper2D {

	protected void renderRect(GuiGraphics graphics, Drawer drawer) {
		RenderSystem.setShader(() -> GameRenderer.getPositionColorShader());
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		RenderSystem.enableDepthTest();

		Tesselator tessellator = Tesselator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBuilder();
		Matrix4f pose = graphics.pose().last().pose();

		bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
		drawer.draw(bufferBuilder, pose);
		tessellator.end();
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

	protected void drawRect(BufferBuilder bufferBuilder, Matrix4f pose, int x, int y, int width, int height, int color) {
		int x0 = x;
		int y0 = y;
		int x1 = x + width;
		int y1 = y + height;

		int a = ColorUtils.getAlpha(color);
		int r = ColorUtils.getRed(color);
		int g = ColorUtils.getGreen(color);
		int b = ColorUtils.getBlue(color);

		drawRect(bufferBuilder, pose, x0, y0, x1, y1, a, r, g, b);
	}

	protected void drawRect(BufferBuilder bufferBuilder, Matrix4f pose, int x0, int y0, int x1, int y1, int a, int r, int g, int b) {
		int z = 0;

		bufferBuilder.vertex(pose, x0, y0, z).color(r, g, b, a).endVertex();
		bufferBuilder.vertex(pose, x0, y1, z).color(r, g, b, a).endVertex();
		bufferBuilder.vertex(pose, x1, y1, z).color(r, g, b, a).endVertex();
		bufferBuilder.vertex(pose, x1, y0, z).color(r, g, b, a).endVertex();
	}

	protected void drawGradient(BufferBuilder bufferBuilder, Matrix4f pose, int x, int y, int width, int height, int color0, int color1) {
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

		drawGradient(bufferBuilder, pose, x0, y0, x1, y1, a0, r0, g0, b0, a1, r1, g1, b1);
	}

	protected void drawGradient(BufferBuilder bufferBuilder, Matrix4f pose, int x0, int y0, int x1, int y1, int a0, int r0, int g0, int b0, int a1, int r1, int g1, int b1) {
		int z = 0;

		bufferBuilder.vertex(pose, x0, y0, z).color(r0, g0, b0, a0).endVertex();
		bufferBuilder.vertex(pose, x0, y1, z).color(r1, g1, b1, a1).endVertex();
		bufferBuilder.vertex(pose, x1, y1, z).color(r1, g1, b1, a1).endVertex();
		bufferBuilder.vertex(pose, x1, y0, z).color(r0, g0, b0, a0).endVertex();
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
		RenderSystem.setShader(() -> GameRenderer.getPositionTexShader());
		RenderSystem.setShaderTexture(0, texture.location);
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		RenderSystem.enableDepthTest();

		Tesselator tessellator = Tesselator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBuilder();
		Matrix4f pose = graphics.pose().last().pose();

		bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
		drawer.draw(bufferBuilder, pose);
		tessellator.end();
	}

	protected void renderTextureRegion(GuiGraphics graphics, TextureRegion region, int x, int y, int width, int height) {
		renderTexture(graphics, region.texture, (bufferBuilder, pose) -> drawTextureRegion(bufferBuilder, pose, region, x, y, width, height));
	}

	protected void renderTexture(GuiGraphics graphics, Texture texture, int x0, int y0, int x1, int y1, int tx0, int ty0, int tx1, int ty1) {
		renderTexture(graphics, texture, (bufferBuilder, pose) -> drawTexture(bufferBuilder, pose, texture, x0, y0, x1, y1, tx0, ty0, tx1, ty1));
	}

	protected void drawTextureRegion(BufferBuilder bufferBuilder, Matrix4f pose, TextureRegion region, int x, int y, int width, int height) {
		int x0 = x;
		int y0 = y;
		int x1 = x + width;
		int y1 = y + height;

		int tx0 = region.x;
		int ty0 = region.y;
		int tx1 = region.x + region.width;
		int ty1 = region.y + region.height;

		drawTexture(bufferBuilder, pose, region.texture, x0, y0, x1, y1, tx0, ty0, tx1, ty1);
	}

	protected void drawTexture(BufferBuilder bufferBuilder, Matrix4f pose, Texture texture, int x0, int y0, int x1, int y1, int tx0, int ty0, int tx1, int ty1) {
		int z = 0;

		float u0 = (float)tx0 / texture.width;
		float v0 = (float)ty0 / texture.height;
		float u1 = (float)tx1 / texture.width;
		float v1 = (float)ty1 / texture.height;

		bufferBuilder.vertex(pose, x0, y0, z).uv(u0, v0).endVertex();
		bufferBuilder.vertex(pose, x0, y1, z).uv(u0, v1).endVertex();
		bufferBuilder.vertex(pose, x1, y1, z).uv(u1, v1).endVertex();
		bufferBuilder.vertex(pose, x1, y0, z).uv(u1, v0).endVertex();
	}

	protected void renderTextureColor(GuiGraphics graphics, Texture texture, Drawer drawer) {
		RenderSystem.setShader(() -> GameRenderer.getPositionTexColorShader());
		RenderSystem.setShaderTexture(0, texture.location);
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		RenderSystem.enableDepthTest();

		Tesselator tessellator = Tesselator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBuilder();
		Matrix4f pose = graphics.pose().last().pose();

		bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
		drawer.draw(bufferBuilder, pose);
		tessellator.end();
	}

	protected void renderTextureRegionColor(GuiGraphics graphics, TextureRegion region, int x, int y, int width, int height, int color) {
		renderTextureColor(graphics, region.texture, (bufferBuilder, pose) -> drawTextureRegionColor(bufferBuilder, pose, region, x, y, width, height, color));
	}

	protected void renderTextureColor(GuiGraphics graphics, Texture texture, int x0, int y0, int x1, int y1, int tx0, int ty0, int tx1, int ty1, int a, int r, int g, int b) {
		renderTextureColor(graphics, texture, (bufferBuilder, pose) -> drawTextureColor(bufferBuilder, pose, texture, x0, y0, x1, y1, tx0, ty0, tx1, ty1, a, r, g, b));
	}

	protected void drawTextureRegionColor(BufferBuilder bufferBuilder, Matrix4f pose, TextureRegion region, int x, int y, int width, int height, int color) {
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

		drawTextureColor(bufferBuilder, pose, region.texture, x0, y0, x1, y1, tx0, ty0, tx1, ty1, a, r, g, b);
	}

	protected void drawTextureColor(BufferBuilder bufferBuilder, Matrix4f pose, Texture texture, int x0, int y0, int x1, int y1, int tx0, int ty0, int tx1, int ty1, int a, int r, int g, int b) {
		int z = 0;

		float u0 = (float)tx0 / texture.width;
		float v0 = (float)ty0 / texture.height;
		float u1 = (float)tx1 / texture.width;
		float v1 = (float)ty1 / texture.height;

		bufferBuilder.vertex(pose, x0, y0, z).uv(u0, v0).color(r, g, b, a).endVertex();
		bufferBuilder.vertex(pose, x0, y1, z).uv(u0, v1).color(r, g, b, a).endVertex();
		bufferBuilder.vertex(pose, x1, y1, z).uv(u1, v1).color(r, g, b, a).endVertex();
		bufferBuilder.vertex(pose, x1, y0, z).uv(u1, v0).color(r, g, b, a).endVertex();
	}

	protected void renderText(GuiGraphics graphics, TextDrawer drawer) {
		BufferSource source = graphics.bufferSource();
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

		public void draw(BufferBuilder bufferBuilder, Matrix4f pose);

	}

	@FunctionalInterface
	protected interface TextDrawer {

		public void draw(BufferSource source, Matrix4f pose);

	}
}
