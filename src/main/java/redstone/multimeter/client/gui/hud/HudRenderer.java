package redstone.multimeter.client.gui.hud;

import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.vertex.BufferBuilder;

import net.minecraft.client.render.TextRenderer;

import redstone.multimeter.client.gui.element.Element;
import redstone.multimeter.client.gui.element.RenderHelper2D;
import redstone.multimeter.util.ColorUtils;

public class HudRenderer extends RenderHelper2D {

	private final MultimeterHud hud;

	private Element target;

	public HudRenderer(MultimeterHud hud) {
		this.hud = hud;
		this.target = hud;
	}

	public void render(Element element, int mouseX, int mouseY) {
		(target = element).render(mouseX, mouseY);
	}

	private int translateX(int x, int width) {
		switch (hud.getDirectionalityX()) {
		default:
		case LEFT_TO_RIGHT:
			return target.getX() + x;
		case RIGHT_TO_LEFT:
			return (target.getX() + target.getWidth()) - (x + width);
		}
	}

	private int translateY(int y, int height) {
		switch (hud.getDirectionalityY()) {
		default:
		case TOP_TO_BOTTOM:
			return target.getY() + y;
		case BOTTOM_TO_TOP:
			return (target.getY() + target.getHeight()) - (y + height);
		}
	}

	public void renderHighlight(int x, int y, int width, int height, int color) {
		int d = hud.settings.gridSize;
		renderBorder(x, y, width + d, height + d, d, color);
	}

	public void renderRect(int x, int y, int width, int height, int color) {
		super.renderRect(x, y, width, height, color);
	}

	public void renderGradient(int x, int y, int width, int height, int color0, int color1) {
		super.renderGradient(x, y, width, height, color0, color1);
	}

	public void renderText(String text, int x, int y, int color) {
		renderText(hud.textRenderer, text, x, y, false, color);
	}

	@Override
	protected void drawRect(BufferBuilder bufferBuilder, int x, int y, int width, int height, int color) {
		GL11.glEnable(GL11.GL_DEPTH_TEST);

		int x0 = translateX(x, width);
		int y0 = translateY(y, height);
		int x1 = x0 + width;
		int y1 = y0 + height;

		int a = Math.round(ColorUtils.getAlpha(color) * hud.settings.opacity() / 100.0F);
		int r = ColorUtils.getRed(color);
		int g = ColorUtils.getGreen(color);
		int b = ColorUtils.getBlue(color);

		drawRect(bufferBuilder, x0, y0, x1, y1, a, r, g, b);
	}

	@Override
	protected void drawGradient(BufferBuilder bufferBuilder, int x, int y, int width, int height, int color0, int color1) {
		GL11.glEnable(GL11.GL_DEPTH_TEST);

		int x0 = translateX(x, width);
		int y0 = translateY(y, height);
		int x1 = x0 + width;
		int y1 = y0 + height;

		int a0 = Math.round(ColorUtils.getAlpha(color0) * hud.settings.opacity() / 100.0F);
		int r0 = ColorUtils.getRed(color0);
		int g0 = ColorUtils.getGreen(color0);
		int b0 = ColorUtils.getBlue(color0);

		int a1 = Math.round(ColorUtils.getAlpha(color1) * hud.settings.opacity() / 100.0F);
		int r1 = ColorUtils.getRed(color1);
		int g1 = ColorUtils.getGreen(color1);
		int b1 = ColorUtils.getBlue(color1);

		drawGradient(bufferBuilder, x0, y0, x1, y1, a0, r0, g0, b0, a1, r1, g1, b1);
	}

	@Override
	protected void renderText(TextRenderer textRenderer, String text, int x, int y, boolean shadow, int color) {
		x = translateX(x, textRenderer.getWidth(text) - 1);
		y = translateY(y, textRenderer.fontHeight - 2);

		int alpha = Math.round(ColorUtils.getAlpha(color) * hud.settings.opacity() / 100.0F);
		color = ColorUtils.setAlpha(color, alpha);

		super.renderText(textRenderer, text, x, y, shadow, color);
	}
}
