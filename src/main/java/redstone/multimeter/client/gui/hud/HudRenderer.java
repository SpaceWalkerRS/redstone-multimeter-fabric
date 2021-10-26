package redstone.multimeter.client.gui.hud;

import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.math.Matrix4f;

import redstone.multimeter.client.gui.element.IElement;
import redstone.multimeter.util.ColorUtils;

public class HudRenderer {
	
	private final MultimeterHud hud;
	
	private IElement target;
	
	public HudRenderer(MultimeterHud hud) {
		this.hud = hud;
		this.target = hud;
	}
	
	public void render(IElement element, MatrixStack matrices, int mouseX, int mouseY, float delta) {
		(target = element).render(matrices, mouseX, mouseY, delta);
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
	
	public void drawHighlight(MatrixStack matrices, int x, int y, int width, int height, boolean selected) {
		int left   = x;
		int right  = x + width;
		int top    = y;
		int bottom = y + height;
		int d      = hud.settings.gridSize;
		int color  = selected ? hud.settings.colorHighlightSelected : hud.settings.colorHighlightHovered;
		
		drawRect(matrices, left     , top       , d     , height, color); // left
		drawRect(matrices, left  + d, top       , width , d     , color); // top
		drawRect(matrices, right    , top    + d, d     , height, color); // right
		drawRect(matrices, left     , bottom    , width , d     , color); // bottom
	}
	
	public void drawRect(MatrixStack matrices, int x, int y, int width, int height, int color) {
		RenderSystem.enableBlend();
		RenderSystem.disableTexture();
		RenderSystem.enableDepthTest();
		RenderSystem.defaultBlendFunc();
		RenderSystem.setShader(() -> GameRenderer.getPositionColorShader());
		
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBuffer();
		
		Matrix4f model = matrices.peek().getModel();
		
		int x1 = translateX(x, width);
		int x2 = x1 + width;
		int y1 = translateY(y, height);
		int y2 = y1 + height;
		
		float r = (float)ColorUtils.getRed(color)   / 0xFF;
		float g = (float)ColorUtils.getGreen(color) / 0xFF;
		float b = (float)ColorUtils.getBlue(color)  / 0xFF;
		float a = hud.settings.opacity() / 100.0F;
		
		bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
		
		bufferBuilder.vertex(model, x1, y2, 0.0F).color(r, g, b, a).next();
		bufferBuilder.vertex(model, x2, y2, 0.0F).color(r, g, b, a).next();
		bufferBuilder.vertex(model, x2, y1, 0.0F).color(r, g, b, a).next();
		bufferBuilder.vertex(model, x1, y1, 0.0F).color(r, g, b, a).next();
		
		bufferBuilder.end();
		BufferRenderer.draw(bufferBuilder);
		
		RenderSystem.disableDepthTest();
		RenderSystem.enableTexture();
		RenderSystem.disableBlend();
	}
	
	public void drawText(MatrixStack matrices, String text, int x, int y, int color) {
		drawText(matrices, new LiteralText(text), x, y, color);
	}
	
	public void drawText(MatrixStack matrices, Text text, int x, int y, int color) {
		x = translateX(x, hud.font.getWidth(text) - 1);
		y = translateY(y, hud.font.fontHeight - 2);
		int alpha = Math.round(0xFF * hud.settings.opacity() / 100.0F);
		color = ColorUtils.setAlpha(color, alpha);
		
		hud.font.draw(matrices, text, x, y, color);
	}
}
