package redstone.multimeter.client.gui.hud;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.PoseStack;

import com.mojang.math.Matrix4f;

import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource.BufferSource;
import net.minecraft.network.chat.Component;

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

	public void render(Element element, PoseStack poses, int mouseX, int mouseY) {
		(target = element).render(poses, mouseX, mouseY);
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

	public void renderHighlight(PoseStack poses, int x, int y, int width, int height, int color) {
		int d = hud.settings.gridSize;
		renderBorder(poses, x, y, width + d, height + d, d, color);
	}

	public void renderRect(PoseStack poses, int x, int y, int width, int height, int color) {
		super.renderRect(poses, x, y, width, height, color);
	}

	public void renderText(PoseStack poses, String text, int x, int y, int color) {
		super.renderText(hud.font, poses, text, x, y, false, color);
	}

	public void renderText(PoseStack poses, Component text, int x, int y, int color) {
		super.renderText(hud.font, poses, text, x, y, false, color);
	}

	@Override
	protected void drawRect(BufferBuilder bufferBuilder, Matrix4f pose, int x, int y, int width, int height, int color) {
		RenderSystem.enableDepthTest();

		int x0 = translateX(x, width);
		int y0 = translateY(y, height);
		int x1 = x0 + width;
		int y1 = y0 + height;

		int a = Math.round(ColorUtils.getAlpha(color) * hud.settings.opacity() / 100.0F);
		int r = ColorUtils.getRed(color);
		int g = ColorUtils.getGreen(color);
		int b = ColorUtils.getBlue(color);

		drawRect(bufferBuilder, pose, x0, y0, x1, y1, a, r, g, b);
	}

	@Override
	protected void drawText(BufferSource source, Matrix4f pose, Font font, String text, int x, int y, boolean shadow, int color) {
		x = translateX(x, font.width(text) - 1);
		y = translateY(y, font.lineHeight - 2);

		int alpha = Math.round(ColorUtils.getAlpha(color) * hud.settings.opacity() / 100.0F);
		color = ColorUtils.setAlpha(color, alpha);

		super.drawText(source, pose, font, text, x, y, shadow, color);
	}

	@Override
	protected void drawText(BufferSource source, Matrix4f pose, Font font, Component text, int x, int y, boolean shadow, int color) {
		x = translateX(x, textWidth(font, text) - 1);
		y = translateY(y, font.lineHeight - 2);

		int alpha = Math.round(ColorUtils.getAlpha(color) * hud.settings.opacity() / 100.0F);
		color = ColorUtils.setAlpha(color, alpha);

		super.drawText(source, pose, font, text, x, y, shadow, color);
	}
}
