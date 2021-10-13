package rsmm.fabric.client.gui.hud;

import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.math.Matrix4f;

import rsmm.fabric.util.ColorUtils;

public interface HudRenderer {
	
	default void drawHighlight(MultimeterHud hud, MatrixStack matrices, int x, int y, int width, int height, int color) {
		int left   = x;
		int right  = x + width;
		int top    = y;
		int bottom = y + height;
		int d      = hud.settings.gridSize;
		
		drawRect(hud, matrices, left     , top       , left  + d, bottom    , color); // left
		drawRect(hud, matrices, left  + d, top       , right + d, top    + d, color); // top
		drawRect(hud, matrices, right    , top    + d, right + d, bottom + d, color); // right
		drawRect(hud, matrices, left     , bottom    , right    , bottom + d, color); // bottom
	}
	
	default void drawRect(MultimeterHud hud, MatrixStack matrices, int x1, int y1, int x2, int y2, int color) {
		RenderSystem.enableBlend();
		RenderSystem.disableTexture();
		RenderSystem.enableDepthTest();
		RenderSystem.defaultBlendFunc();
		RenderSystem.setShader(() -> GameRenderer.getPositionColorShader());
		
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBuffer();
		
		Matrix4f model = matrices.peek().getModel();
		
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
	
	default void drawText(MultimeterHud hud, MatrixStack matrices, Text text, int x, int y, int color) {
		int alpha = Math.round(0xFF * hud.settings.opacity() / 100.0F);
		color = ColorUtils.setAlpha(color, alpha);
		
		hud.font.draw(matrices, text, x, y, color);
	}
}
