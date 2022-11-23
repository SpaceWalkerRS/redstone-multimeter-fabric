package redstone.multimeter.client.gui.hud;

import org.joml.Matrix4f;

import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.VertexConsumerProvider.Immediate;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

import redstone.multimeter.client.gui.element.RenderHelper2D;
import redstone.multimeter.client.gui.element.IElement;
import redstone.multimeter.util.ColorUtils;

public class HudRenderer extends RenderHelper2D {
	
	private final MultimeterHud hud;
	
	private IElement target;
	
	public HudRenderer(MultimeterHud hud) {
		this.hud = hud;
		this.target = hud;
	}
	
	public void render(IElement element, MatrixStack matrices, int mouseX, int mouseY) {
		(target = element).render(matrices, mouseX, mouseY);
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
	
	public void renderHighlight(MatrixStack matrices, int x, int y, int width, int height, int color) {
		int d = hud.settings.gridSize;
		renderBorder(matrices, x, y, width + d, height + d, d, color);
	}
	
	public void renderRect(MatrixStack matrices, int x, int y, int width, int height, int color) {
		super.renderRect(matrices, x, y, width, height, color);
	}
	
	public void renderText(MatrixStack matrices, String text, int x, int y, int color) {
		super.renderText(hud.font, matrices, text, x, y, false, color);
	}
	
	public void renderText(MatrixStack matrices, Text text, int x, int y, int color) {
		super.renderText(hud.font, matrices, text, x, y, false, color);
	}
	
	@Override
	protected void drawRect(BufferBuilder bufferBuilder, Matrix4f model, int x, int y, int width, int height, int color) {
		RenderSystem.enableDepthTest();
		
		int x0 = translateX(x, width);
		int y0 = translateY(y, height);
		int x1 = x0 + width;
		int y1 = y0 + height;
		
		int a = Math.round(ColorUtils.getAlpha(color) * hud.settings.opacity() / 100.0F);
		int r = ColorUtils.getRed(color);
		int g = ColorUtils.getGreen(color);
		int b = ColorUtils.getBlue(color);
		
		drawRect(bufferBuilder, model, x0, y0, x1, y1, a, r, g, b);
	}
	
	@Override
	protected void drawText(Immediate immediate, Matrix4f model, TextRenderer font, String text, int x, int y, boolean shadow, int color) {
		x = translateX(x, font.getWidth(text) - 1);
		y = translateY(y, font.fontHeight - 2);
		
		int alpha = Math.round(ColorUtils.getAlpha(color) * hud.settings.opacity() / 100.0F);
		color = ColorUtils.setAlpha(color, alpha);
		
		super.drawText(immediate, model, font, text, x, y, shadow, color);
	}
	
	@Override
	protected void drawText(Immediate immediate, Matrix4f model, TextRenderer font, Text text, int x, int y, boolean shadow, int color) {
		x = translateX(x, font.getWidth(text) - 1);
		y = translateY(y, font.fontHeight - 2);
		
		int alpha = Math.round(ColorUtils.getAlpha(color) * hud.settings.opacity() / 100.0F);
		color = ColorUtils.setAlpha(color, alpha);
		
		super.drawText(immediate, model, font, text, x, y, shadow, color);
	}
}
