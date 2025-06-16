package redstone.multimeter.client.gui.element;

import java.util.Stack;

import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;

import com.mojang.math.Matrix4f;

import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.MultiBufferSource.BufferSource;
import net.minecraft.network.chat.Component;

import redstone.multimeter.client.MultimeterClient;
import redstone.multimeter.client.gui.Texture;
import redstone.multimeter.client.gui.TextureRegion;
import redstone.multimeter.util.ColorUtils;

public class RenderHelper2D {

	private static final ScissorStack SCISSOR_STACK = new ScissorStack();

	public static void enableScissor(int x0, int y0, int x1, int y1) {
		applyScissor(SCISSOR_STACK.push(new ScissorBox(x0, y0, x1 - x0, y1 - y0)));
	}

	public static void disableScissor() {
		applyScissor(SCISSOR_STACK.pop());
	}

	private static void applyScissor(ScissorBox box) {
		if (box == null) {
			GL11.glDisable(GL11.GL_SCISSOR_TEST);
		} else {
			Window window = MultimeterClient.MINECRAFT.getWindow();

			int windowHeight = window.getHeight();
			double windowScale = window.getGuiScale();

			int x = (int)(box.x * windowScale);
			int y = (int)(windowHeight - (box.y + box.height) * windowScale);
			int width = (int)(box.width * windowScale);
			int height = (int)(box.height * windowScale);

	        GL11.glEnable(GL11.GL_SCISSOR_TEST);
	        GL11.glScissor(x, y, Math.max(0, width), Math.max(0, height));
		}
	}

	protected void renderRect(PoseStack poses, Drawer drawer) {
		RenderSystem.enableBlend();
		RenderSystem.disableTexture();
		RenderSystem.defaultBlendFunc();
		RenderSystem.enableDepthTest();
		RenderSystem.shadeModel(GL11.GL_SMOOTH);

		Tesselator tessellator = Tesselator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBuilder();
		Matrix4f pose = poses.last().pose();

		bufferBuilder.begin(GL11.GL_QUADS, DefaultVertexFormat.POSITION_COLOR);
		drawer.draw(bufferBuilder, pose);
		tessellator.end();
	}

	protected void renderRect(PoseStack poses, int x, int y, int width, int height, int color) {
		renderRect(poses, (bufferBuilder, pose) -> drawRect(bufferBuilder, pose, x, y, width, height, color));
	}

	protected void renderRect(PoseStack poses, int x0, int y0, int x1, int y1, int a, int r, int g, int b) {
		renderRect(poses, (bufferBuilder, pose) -> drawRect(bufferBuilder, pose, x0, y0, x1, y1, a, r, g, b));
	}

	protected void renderGradient(PoseStack poses, int x, int y, int width, int height, int color0, int color1) {
		renderRect(poses, (bufferBuilder, pose) -> drawGradient(bufferBuilder, pose, x, y, width, height, color0, color1));
	}

	protected void renderGradient(PoseStack poses, int x0, int y0, int x1, int y1, int a0, int r0, int g0, int b0, int a1, int r1, int g1, int b1) {
		renderRect(poses, (bufferBuilder, pose) -> drawGradient(bufferBuilder, pose, x0, y0, x1, y1, a0, r0, g0, b0, a1, r1, g1, b1));
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

	protected void renderBorder(PoseStack poses, int x, int y, int width, int height, int color) {
		renderBorder(poses, x, y, width, height, 1, color);
	}

	protected void renderBorder(PoseStack poses, int x, int y, int width, int height, int d, int color) {
		int left = x;
		int right = x + width;
		int top = y;
		int bottom = y + height;

		renderRect(poses, (bufferBuilder, pose) -> {
			drawRect(bufferBuilder, pose, left     , top       , d        , height - d, color); // left
			drawRect(bufferBuilder, pose, left     , bottom - d, width - d, d         , color); // bottom
			drawRect(bufferBuilder, pose, right - d, top    + d, d        , height - d, color); // right
			drawRect(bufferBuilder, pose, left  + d, top       , width - d, d         , color); // top
		});
	}

	protected void renderTexture(PoseStack poses, Texture texture, Drawer drawer) {
		MultimeterClient.MINECRAFT.getTextureManager().bind(texture.location);
		RenderSystem.enableTexture();
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		RenderSystem.enableDepthTest();

		Tesselator tessellator = Tesselator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBuilder();
		Matrix4f pose = poses.last().pose();

		bufferBuilder.begin(GL11.GL_QUADS, DefaultVertexFormat.POSITION_TEX);
		drawer.draw(bufferBuilder, pose);
		tessellator.end();
	}

	protected void renderTextureRegion(PoseStack poses, TextureRegion region, int x, int y, int width, int height) {
		renderTexture(poses, region.texture, (bufferBuilder, pose) -> drawTextureRegion(bufferBuilder, pose, region, x, y, width, height));
	}

	protected void renderTexture(PoseStack poses, Texture texture, int x0, int y0, int x1, int y1, int tx0, int ty0, int tx1, int ty1) {
		renderTexture(poses, texture, (bufferBuilder, pose) -> drawTexture(bufferBuilder, pose, texture, x0, y0, x1, y1, tx0, ty0, tx1, ty1));
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

	protected void renderTextureColor(PoseStack poses, Texture texture, Drawer drawer) {
		MultimeterClient.MINECRAFT.getTextureManager().bind(texture.location);
		RenderSystem.enableTexture();
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		RenderSystem.enableDepthTest();

		Tesselator tessellator = Tesselator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBuilder();
		Matrix4f pose = poses.last().pose();

		bufferBuilder.begin(GL11.GL_QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
		drawer.draw(bufferBuilder, pose);
		tessellator.end();
	}

	protected void renderTextureRegionColor(PoseStack poses, TextureRegion region, int x, int y, int width, int height, int color) {
		renderTextureColor(poses, region.texture, (bufferBuilder, pose) -> drawTextureRegionColor(bufferBuilder, pose, region, x, y, width, height, color));
	}

	protected void renderTextureColor(PoseStack poses, Texture texture, int x0, int y0, int x1, int y1, int tx0, int ty0, int tx1, int ty1, int a, int r, int g, int b) {
		renderTextureColor(poses, texture, (bufferBuilder, pose) -> drawTextureColor(bufferBuilder, pose, texture, x0, y0, x1, y1, tx0, ty0, tx1, ty1, a, r, g, b));
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

	protected int textWidth(Font font, Component text) {
		return font.width(text.getColoredString());
	}

	protected void renderText(PoseStack poses, TextDrawer drawer) {
		Tesselator tessellator = Tesselator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBuilder();
		BufferSource source = MultiBufferSource.immediate(bufferBuilder);
		Matrix4f pose = poses.last().pose();

		drawer.draw(source, pose);
		source.endBatch();
	}

	protected void renderText(Font font, PoseStack poses, Component text, int x, int y, boolean shadow, int color) {
		renderText(poses, (source, pose) -> drawText(source, pose, font, text, x, y, shadow, color));
	}

	protected void renderText(Font font, PoseStack poses, String text, int x, int y, boolean shadow, int color) {
		renderText(poses, (source, pose) -> drawText(source, pose, font, text, x, y, shadow, color));
	}

	protected void drawText(BufferSource source, Matrix4f pose, Font font, Component text, int x, int y, boolean shadow) {
		drawText(source, pose, font, text, x, y, shadow, 0xFFFFFFFF);
	}

	protected void drawText(BufferSource source, Matrix4f pose, Font font, Component text, int x, int y, boolean shadow, int color) {
		font.drawInBatch(text.getColoredString(), x, y, color, shadow, pose, source, false, 0x00000000, 0x00F000F0);
	}

	protected void drawText(BufferSource source, Matrix4f pose, Font font, String text, int x, int y, boolean shadow) {
		drawText(source, pose, font, text, x, y, shadow, 0xFFFFFFFF);
	}

	protected void drawText(BufferSource source, Matrix4f pose, Font font, String text, int x, int y, boolean shadow, int color) {
		font.drawInBatch(text, x, y, color, shadow, pose, source, false, 0x00000000, 0x00F000F0);
	}

	@FunctionalInterface
	protected interface Drawer {

		public void draw(BufferBuilder bufferBuilder, Matrix4f pose);

	}

	@FunctionalInterface
	protected interface TextDrawer {

		public void draw(BufferSource source, Matrix4f pose);

	}

	private static class ScissorStack {

		private final Stack<ScissorBox> boxes = new Stack<>();

		public ScissorBox push(ScissorBox box) {
			if (!boxes.isEmpty()) {
				box = boxes.peek().intersection(box);
			}

			return boxes.push(box);
		}

		public ScissorBox pop() {
			if (boxes.isEmpty()) {
				throw new IllegalStateException("popping empty scissor stack");
			} else{
				boxes.pop();
				return boxes.isEmpty() ? null : boxes.peek();
			}
		}
	}

	private static class ScissorBox {

		private static final ScissorBox EMPTY = new ScissorBox(0, 0, 0, 0);

		private final int x;
		private final int y;
		private final int width;
		private final int height;

		public ScissorBox(int x, int y, int width, int height) {
			this.x = x;
			this.y = y;
			this.width = width;
			this.height = height;
		}

		public ScissorBox intersection(ScissorBox o) {
			int x0 = Math.max(x, o.x);
			int y0 = Math.max(y, o.y);
			int x1 = Math.min(x + width, o.x + o.width);
			int y1 = Math.min(y + height, o.y + o.height);

			return (x0 == x1 || y0 == y1) ? EMPTY : new ScissorBox(x0, y0, x1 - x0, y1 - y0);
		}
	}
}
