package redstone.multimeter.client.render;

import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.platform.GLX;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Tessellator;
import net.minecraft.entity.Entity;

import redstone.multimeter.client.MultimeterClient;
import redstone.multimeter.common.DimPos;
import redstone.multimeter.common.meter.Meter;
import redstone.multimeter.util.ColorUtils;

public class MeterRenderer {
	
	private final MultimeterClient multimeterClient;
	private final MinecraftClient minecraftClient;
	
	private double cameraX;
	private double cameraY;
	private double cameraZ;
	
	public MeterRenderer(MultimeterClient multimeterClient) {
		this.multimeterClient = multimeterClient;
		this.minecraftClient = this.multimeterClient.getMinecraftClient();
	}
	
	public void renderMeters(Entity camera, float tickDelta) {
		cameraX = camera.prevTickX + tickDelta * (camera.x - camera.prevTickX);
		cameraY = camera.prevTickY + tickDelta * (camera.y - camera.prevTickY);
		cameraZ = camera.prevTickZ + tickDelta * (camera.z - camera.prevTickZ);
		
		GL11.glEnable(GL11.GL_BLEND);
		GLX.glBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glDepthMask(false);
		
		for (Meter meter : multimeterClient.getMeterGroup().getMeters()) {
			if (meter.isIn(minecraftClient.world)) {
				drawMeter(meter);
			}
		}
		
		GL11.glDepthMask(true);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glDisable(GL11.GL_BLEND);
	}
	
	private void drawMeter(Meter meter) {
		Tessellator tessellator = Tessellator.INSTANCE;
		
		DimPos pos = meter.getPos();
		int color = meter.getColor();
		boolean movable = meter.isMovable();
		
		GL11.glPushMatrix();
		GL11.glTranslated(pos.getX() - cameraX, pos.getY() - cameraY, pos.getZ() - cameraZ);
		
		int r = ColorUtils.getRed(color);
		int g = ColorUtils.getGreen(color);
		int b = ColorUtils.getBlue(color);
		
		drawFilledBox(tessellator, r, g, b, 0x80);
		
		if (movable) {
			drawBoxOutline(tessellator, r, g, b, 0xFF);
		}
		
		GL11.glPopMatrix();
	}
	
	private void drawFilledBox(Tessellator tessellator, int r, int g, int b, int a) {
		tessellator.method_1408(GL11.GL_QUADS);
		drawBox(tessellator, r, g, b, a, false);
		tessellator.method_1396();
	}
	
	private void drawBoxOutline(Tessellator tessellator, int r, int g, int b, int a) {
		tessellator.method_1408(GL11.GL_LINES);
		drawBox(tessellator, r, g, b, a, true);
		tessellator.method_1396();
	}
	
	private void drawBox(Tessellator tessellator, int r, int g, int b, int a, boolean outline) {
		tessellator.method_1404(r, g, b, a);
		
		// The box is slightly larger than 1x1 to prevent z-fighting
		float c0 = -0.002F;
		float c1 = 1.002F;
		
		// West face
		tessellator.method_1398(c0, c0, c0);
		tessellator.method_1398(c0, c0, c1);
		tessellator.method_1398(c0, c1, c1);
		tessellator.method_1398(c0, c1, c0);
		if (outline) {
			tessellator.method_1398(c0, c0, c0);
		}
		
		// East face
		tessellator.method_1398(c1, c0, c0);
		tessellator.method_1398(c1, c1, c0);
		tessellator.method_1398(c1, c1, c1);
		tessellator.method_1398(c1, c0, c1);
		if (outline) {
			tessellator.method_1398(c1, c0, c0);
		}
		
		// North face
		tessellator.method_1398(c0, c0, c0);
		tessellator.method_1398(c0, c1, c0);
		tessellator.method_1398(c1, c1, c0);
		tessellator.method_1398(c1, c0, c0);
		if (outline) {
			tessellator.method_1398(c0, c0, c0);
		}
		
		// South face
		tessellator.method_1398(c0, c0, c1);
		tessellator.method_1398(c1, c0, c1);
		tessellator.method_1398(c1, c1, c1);
		tessellator.method_1398(c0, c1, c1);
		if (outline) {
			tessellator.method_1398(c0, c0, c1);
		}
		
		// Bottom face
		tessellator.method_1398(c0, c0, c0);
		tessellator.method_1398(c1, c0, c0);
		tessellator.method_1398(c1, c0, c1);
		tessellator.method_1398(c0, c0, c1);
		if (outline) {
			tessellator.method_1398(c0, c0, c0);
		}
		
		// Top face
		tessellator.method_1398(c0, c1, c0);
		tessellator.method_1398(c0, c1, c1);
		tessellator.method_1398(c1, c1, c1);
		tessellator.method_1398(c1, c1, c0);
		if (outline) {
			tessellator.method_1398(c0, c1, c0);
		}
	}
}
