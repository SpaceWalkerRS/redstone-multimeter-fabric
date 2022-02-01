package redstone.multimeter.client.render;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;

import redstone.multimeter.client.MultimeterClient;
import redstone.multimeter.common.meter.Meter;
import redstone.multimeter.util.ColorUtils;

public class MeterRenderer {
	
	private final MultimeterClient multimeterClient;
	private final Minecraft minecraftClient;
	
	private double cameraX;
	private double cameraY;
	private double cameraZ;
	
	public MeterRenderer(MultimeterClient multimeterClient) {
		this.multimeterClient = multimeterClient;
		this.minecraftClient = this.multimeterClient.getMinecraftClient();
	}
	
	public void renderMeters(Entity camera, float tickDelta) {
		cameraX = camera.lastTickPosX + tickDelta * (camera.posX - camera.lastTickPosX);
		cameraY = camera.lastTickPosY + tickDelta * (camera.posY - camera.lastTickPosY);
		cameraZ = camera.lastTickPosZ + tickDelta * (camera.posZ - camera.lastTickPosZ);
		
		GlStateManager.enableBlend();
		GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
		GlStateManager.disableTexture2D();
		GlStateManager.depthMask(false);
		
		for (Meter meter : multimeterClient.getMeterGroup().getMeters()) {
			if (meter.isIn(minecraftClient.world)) {
				drawMeter(meter);
			}
		}
		
		GlStateManager.depthMask(true);
		GlStateManager.enableTexture2D();
		GlStateManager.disableBlend();
	}
	
	private void drawMeter(Meter meter) {
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder builder = tessellator.getBuffer();
		
		BlockPos pos = meter.getPos().getBlockPos();
		int color = meter.getColor();
		boolean movable = meter.isMovable();
		
		GlStateManager.pushMatrix();
		GlStateManager.translate(pos.getX() - cameraX, pos.getY() - cameraY, pos.getZ() - cameraZ);
		
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
		builder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
		drawBox(builder, r, g, b, a, false);
		tessellator.draw();
	}
	
	private void drawBoxOutline(BufferBuilder builder, Tessellator tessellator, float r, float g, float b, float a) {
		builder.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR);
		drawBox(builder, r, g, b, a, true);
		tessellator.draw();
	}
	
	private void drawBox(BufferBuilder builder, float r, float g, float b, float a, boolean outline) {
		// The box is slightly larger than 1x1 to prevent z-fighting
		float c0 = -0.002F;
		float c1 = 1.002F;
		
		// West face
		builder.pos(c0, c0, c0).color(r, g, b, a).endVertex();
		builder.pos(c0, c0, c1).color(r, g, b, a).endVertex();
		builder.pos(c0, c1, c1).color(r, g, b, a).endVertex();
		builder.pos(c0, c1, c0).color(r, g, b, a).endVertex();
		if (outline) {
			builder.pos(c0, c0, c0).color(r, g, b, a).endVertex();
		}
		
		// East face
		builder.pos(c1, c0, c0).color(r, g, b, a).endVertex();
		builder.pos(c1, c1, c0).color(r, g, b, a).endVertex();
		builder.pos(c1, c1, c1).color(r, g, b, a).endVertex();
		builder.pos(c1, c0, c1).color(r, g, b, a).endVertex();
		if (outline) {
			builder.pos(c1, c0, c0).color(r, g, b, a).endVertex();
		}
		
		// North face
		builder.pos(c0, c0, c0).color(r, g, b, a).endVertex();
		builder.pos(c0, c1, c0).color(r, g, b, a).endVertex();
		builder.pos(c1, c1, c0).color(r, g, b, a).endVertex();
		builder.pos(c1, c0, c0).color(r, g, b, a).endVertex();
		if (outline) {
			builder.pos(c0, c0, c0).color(r, g, b, a).endVertex();
		}
		
		// South face
		builder.pos(c0, c0, c1).color(r, g, b, a).endVertex();
		builder.pos(c1, c0, c1).color(r, g, b, a).endVertex();
		builder.pos(c1, c1, c1).color(r, g, b, a).endVertex();
		builder.pos(c0, c1, c1).color(r, g, b, a).endVertex();
		if (outline) {
			builder.pos(c0, c0, c1).color(r, g, b, a).endVertex();
		}
		
		// Bottom face
		builder.pos(c0, c0, c0).color(r, g, b, a).endVertex();
		builder.pos(c1, c0, c0).color(r, g, b, a).endVertex();
		builder.pos(c1, c0, c1).color(r, g, b, a).endVertex();
		builder.pos(c0, c0, c1).color(r, g, b, a).endVertex();
		if (outline) {
			builder.pos(c0, c0, c0).color(r, g, b, a).endVertex();
		}
		
		// Top face
		builder.pos(c0, c1, c0).color(r, g, b, a).endVertex();
		builder.pos(c0, c1, c1).color(r, g, b, a).endVertex();
		builder.pos(c1, c1, c1).color(r, g, b, a).endVertex();
		builder.pos(c1, c1, c0).color(r, g, b, a).endVertex();
		if (outline) {
			builder.pos(c0, c1, c0).color(r, g, b, a).endVertex();
		}
	}
}
