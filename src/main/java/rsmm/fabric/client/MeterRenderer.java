package rsmm.fabric.client;

import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.platform.GlStateManager;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import rsmm.fabric.common.Meter;
import rsmm.fabric.common.MeterGroup;

public class MeterRenderer {
	
	private final MultimeterClient multimeterClient;
	private final MinecraftClient minecraftClient;
	
	public MeterRenderer(MultimeterClient multimeterClient) {
		this.multimeterClient = multimeterClient;
		this.minecraftClient = this.multimeterClient.getMinecraftClient();
	}
	
	public void renderMeters() {
		MeterGroup meterGroup = multimeterClient.getMeterGroup();
		if (meterGroup == null) {
			return;
		}
		
		GlStateManager.enableBlend();
		GlStateManager.disableTexture();
		GlStateManager.blendFuncSeparate(
			GlStateManager.SourceFactor.SRC_ALPHA,
			GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA,
			GlStateManager.SourceFactor.ONE,
			GlStateManager.DestFactor.ZERO
		);
		GlStateManager.depthMask(false);
        GlStateManager.disableLighting();
		
		GlStateManager.pushMatrix();
		
		Camera camera = minecraftClient.gameRenderer.getCamera();
		Vec3d cameraPos = camera.getPos();
		
		GlStateManager.translated(-cameraPos.x, -cameraPos.y, -cameraPos.z);
		
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder builder = tessellator.getBuffer();
		
		for (Meter meter : meterGroup.getMeters()) {
			if (meter.isIn(minecraftClient.world)) {
				drawMeter(builder, tessellator, meter);
			}
		}
		
		GlStateManager.popMatrix();
		
		GlStateManager.enableLighting();
		GlStateManager.depthMask(true);
		GlStateManager.enableTexture();
		GlStateManager.disableBlend();
	}
	
	private void drawMeter(BufferBuilder builder, Tessellator tessellator, Meter meter) {
		BlockPos pos = meter.getPos().getBlockPos();
		int color = meter.getColor();
		boolean movable = meter.isMovable();
		
		// The coordinates are slightly offset to prevent z-fighting
		float x0 = pos.getX() - 0.001F;
		float y0 = pos.getY() - 0.001F;
		float z0 = pos.getZ() - 0.001F;
		float x1 = pos.getX() + 1.001F;
		float y1 = pos.getY() + 1.001F;
		float z1 = pos.getZ() + 1.001F;
		
		float r = (color >> 16 & 255) / 255.0F;
		float g = (color >> 8  & 255) / 255.0F;
		float b = (color       & 255) / 255.0F;
		
		drawFilledBox(builder, tessellator, x0, y0, z0, x1, y1, z1, r, g, b, 0.5F);
		
		if (movable) {
			drawBoxOutline(builder, tessellator, x0, y0, z0, x1, y1, z1, r, g, b, 1.0F);
		}
	}
	
	private void drawFilledBox(BufferBuilder builder, Tessellator tessellator, float x0, float y0, float z0, float x1, float y1, float z1, float r, float g, float b, float a) {
		builder.begin(GL11.GL_QUADS, VertexFormats.POSITION_COLOR);
		drawBox(builder, x0, y0, z0, x1, y1, z1, r, g, b, a, false);
		tessellator.draw();
	}
	
	private void drawBoxOutline(BufferBuilder builder, Tessellator tessellator, float x0, float y0, float z0, float x1, float y1, float z1, float r, float g, float b, float a) {
		builder.begin(GL11.GL_LINES, VertexFormats.POSITION_COLOR);
		drawBox(builder, x0, y0, z0, x1, y1, z1, r, g, b, a, true);
		tessellator.draw();
	}
	
	private void drawBox(BufferBuilder builder, float x0, float y0, float z0, float x1, float y1, float z1, float r, float g, float b, float a, boolean outline) {
		// Back Face
		builder.vertex(x0, y0, z0).color(r, g, b, a).next();
		builder.vertex(x0, y0, z1).color(r, g, b, a).next();
		builder.vertex(x0, y1, z1).color(r, g, b, a).next();
		builder.vertex(x0, y1, z0).color(r, g, b, a).next();
		if (outline) {
			builder.vertex(x0, y0, z0).color(r, g, b, a).next();
		}
		
		// Front Face
		builder.vertex(x1, y0, z0).color(r, g, b, a).next();
		builder.vertex(x1, y1, z0).color(r, g, b, a).next();
		builder.vertex(x1, y1, z1).color(r, g, b, a).next();
		builder.vertex(x1, y0, z1).color(r, g, b, a).next();
		if (outline) {
			builder.vertex(x1, y0, z0).color(r, g, b, a).next();
		}
		
		// Right Face
		builder.vertex(x0, y0, z0).color(r, g, b, a).next();
		builder.vertex(x0, y1, z0).color(r, g, b, a).next();
		builder.vertex(x1, y1, z0).color(r, g, b, a).next();
		builder.vertex(x1, y0, z0).color(r, g, b, a).next();
		if (outline) {
			builder.vertex(x0, y0, z0).color(r, g, b, a).next();
		}
		
		// Left Face
		builder.vertex(x0, y0, z1).color(r, g, b, a).next();
		builder.vertex(x1, y0, z1).color(r, g, b, a).next();
		builder.vertex(x1, y1, z1).color(r, g, b, a).next();
		builder.vertex(x0, y1, z1).color(r, g, b, a).next();
		if (outline) {
			builder.vertex(x0, y0, z1).color(r, g, b, a).next();
		}
		
		// Bottom Face
		builder.vertex(x0, y0, z0).color(r, g, b, a).next();
		builder.vertex(x1, y0, z0).color(r, g, b, a).next();
		builder.vertex(x1, y0, z1).color(r, g, b, a).next();
		builder.vertex(x0, y0, z1).color(r, g, b, a).next();
		if (outline) {
			builder.vertex(x0, y0, z0).color(r, g, b, a).next();
		}
		
		// Top Face
		builder.vertex(x0, y1, z0).color(r, g, b, a).next();
		builder.vertex(x0, y1, z1).color(r, g, b, a).next();
		builder.vertex(x1, y1, z1).color(r, g, b, a).next();
		builder.vertex(x1, y1, z0).color(r, g, b, a).next();
		if (outline) {
			builder.vertex(x0, y1, z0).color(r, g, b, a).next();
		}
	}
}
