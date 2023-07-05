package redstone.multimeter.client.gui.hud;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.vertex.BufferBuilder;

import net.minecraft.client.render.TextRenderer;
import net.minecraft.text.Text;

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

	public void renderText(String text, int x, int y, int color) {
		renderText(hud.textRenderer, text, x, y, false, color);
	}

	public void renderText(Text text, int x, int y, int color) {
		renderText(hud.textRenderer, text, x, y, false, color);
	}

	@Override
	protected void drawRect(BufferBuilder bufferBuilder, int x, int y, int width, int height, int color) {
		GlStateManager.enableDepthTest();

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
	protected void renderText(TextRenderer textRenderer, String text, int x, int y, boolean shadow, int color) {
		x = translateX(x, textRenderer.getStringWidth(text) - 1);
		y = translateY(y, textRenderer.fontHeight - 2);

		int alpha = Math.round(ColorUtils.getAlpha(color) * hud.settings.opacity() / 100.0F);
		color = ColorUtils.setAlpha(color, alpha);

		super.renderText(textRenderer, text, x, y, shadow, color);
	}

	@Override
	protected void renderText(TextRenderer textRenderer, Text text, int x, int y, boolean shadow, int color) {
		x = translateX(x, textWidth(textRenderer, text) - 1);
		y = translateY(y, textRenderer.fontHeight - 2);

		int alpha = Math.round(ColorUtils.getAlpha(color) * hud.settings.opacity() / 100.0F);
		color = ColorUtils.setAlpha(color, alpha);

		super.renderText(textRenderer, text, x, y, shadow, color);
	}
}
