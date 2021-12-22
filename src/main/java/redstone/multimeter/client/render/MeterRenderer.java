package redstone.multimeter.client.render;

import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.platform.GlStateManager;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;

import redstone.multimeter.client.MultimeterClient;
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
		
		GlStateManager.enableBlend();
		GlStateManager.method_12288(GlStateManager.class_2870.field_13525, GlStateManager.class_2866.field_13480, GlStateManager.class_2870.field_13518, GlStateManager.class_2866.field_13484);
		GlStateManager.disableTexture();
		GlStateManager.depthMask(false);
		
		for (Meter meter : multimeterClient.getMeterGroup().getMeters()) {
			if (meter.isIn(minecraftClient.world)) {
				drawMeter(meter);
			}
		}
		
		GlStateManager.depthMask(true);
		GlStateManager.enableTexture();
		GlStateManager.disableBlend();
	}
	
	private void drawMeter(Meter meter) {
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder builder = tessellator.getBuffer();
		
		BlockPos pos = meter.getPos().getBlockPos();
		int color = meter.getColor();
		boolean movable = meter.isMovable();
		
		GlStateManager.pushMatrix();
		GlStateManager.translated(pos.getX() - cameraX, pos.getY() - cameraY, pos.getZ() - cameraZ);
		
		float r = ColorUtils.getRed(color) / 255.0F;
		float g = ColorUtils.getGreen(color) / 255.0F;
		float b = ColorUtils.getBlue(color) / 255.0F;
		
		drawFilledBox(builder, tessellator, r, g, b, 0.5F);
		
		if (movable) {
			drawBoxOutline(builder, tessellator, r, g, b, 1.0F);
		}
		
		GlStateManager.popMatrix();
	}
	
	private void drawFilledBox(BufferBuilder builder, Tessellator tessellator, float r, float g, float b, float a) {
		builder.begin(GL11.GL_QUADS, VertexFormats.POSITION_COLOR);
		drawBox(builder, r, g, b, a, false);
		tessellator.draw();
	}
	
	private void drawBoxOutline(BufferBuilder builder, Tessellator tessellator, float r, float g, float b, float a) {
		builder.begin(GL11.GL_LINES, VertexFormats.POSITION_COLOR);
		drawBox(builder, r, g, b, a, true);
		tessellator.draw();
	}
	
	private void drawBox(BufferBuilder builder, float r, float g, float b, float a, boolean outline) {
		// The box is slightly larger than 1x1 to prevent z-fighting
		float c0 = -0.002F;
		float c1 = 1.002F;
		
		// West face
		builder.vertex(c0, c0, c0).color(r, g, b, a).next();
		builder.vertex(c0, c0, c1).color(r, g, b, a).next();
		builder.vertex(c0, c1, c1).color(r, g, b, a).next();
		builder.vertex(c0, c1, c0).color(r, g, b, a).next();
		if (outline) {
			builder.vertex(c0, c0, c0).color(r, g, b, a).next();
		}
		
		// East face
		builder.vertex(c1, c0, c0).color(r, g, b, a).next();
		builder.vertex(c1, c1, c0).color(r, g, b, a).next();
		builder.vertex(c1, c1, c1).color(r, g, b, a).next();
		builder.vertex(c1, c0, c1).color(r, g, b, a).next();
		if (outline) {
			builder.vertex(c1, c0, c0).color(r, g, b, a).next();
		}
		
		// North face
		builder.vertex(c0, c0, c0).color(r, g, b, a).next();
		builder.vertex(c0, c1, c0).color(r, g, b, a).next();
		builder.vertex(c1, c1, c0).color(r, g, b, a).next();
		builder.vertex(c1, c0, c0).color(r, g, b, a).next();
		if (outline) {
			builder.vertex(c0, c0, c0).color(r, g, b, a).next();
		}
		
		// South face
		builder.vertex(c0, c0, c1).color(r, g, b, a).next();
		builder.vertex(c1, c0, c1).color(r, g, b, a).next();
		builder.vertex(c1, c1, c1).color(r, g, b, a).next();
		builder.vertex(c0, c1, c1).color(r, g, b, a).next();
		if (outline) {
			builder.vertex(c0, c0, c1).color(r, g, b, a).next();
		}
		
		// Bottom face
		builder.vertex(c0, c0, c0).color(r, g, b, a).next();
		builder.vertex(c1, c0, c0).color(r, g, b, a).next();
		builder.vertex(c1, c0, c1).color(r, g, b, a).next();
		builder.vertex(c0, c0, c1).color(r, g, b, a).next();
		if (outline) {
			builder.vertex(c0, c0, c0).color(r, g, b, a).next();
		}
		
		// Top face
		builder.vertex(c0, c1, c0).color(r, g, b, a).next();
		builder.vertex(c0, c1, c1).color(r, g, b, a).next();
		builder.vertex(c1, c1, c1).color(r, g, b, a).next();
		builder.vertex(c1, c1, c0).color(r, g, b, a).next();
		if (outline) {
			builder.vertex(c0, c1, c0).color(r, g, b, a).next();
		}
	}
}
