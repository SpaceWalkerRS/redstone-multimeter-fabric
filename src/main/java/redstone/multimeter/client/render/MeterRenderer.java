package redstone.multimeter.client.render;

import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tessellator;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;

import redstone.multimeter.client.MultimeterClient;
import redstone.multimeter.common.meter.Meter;
import redstone.multimeter.util.ColorUtils;

public class MeterRenderer {

	private final MultimeterClient client;
	private final Minecraft minecraft;

	public MeterRenderer(MultimeterClient client) {
		this.client = client;
		this.minecraft = this.client.getMinecraft();
	}

	public void renderMeters(float tickDelta) {
		GlStateManager.enableBlend();
		GlStateManager.blendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
		GlStateManager.disableTexture();
		GlStateManager.depthMask(false);
		GlStateManager.enableDepthTest();

		for (Meter meter : client.getMeterGroup().getMeters()) {
			if (meter.isIn(minecraft.world)) {
				drawMeter(meter, tickDelta);
			}
		}

		GlStateManager.disableDepthTest();
		GlStateManager.depthMask(true);
		GlStateManager.enableTexture();
		GlStateManager.disableBlend();
	}

	private void drawMeter(Meter meter, float tickDelta) {
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBuilder();

		BlockPos pos = meter.getPos().getBlockPos();
		int color = meter.getColor();
		boolean movable = meter.isMovable();

		Entity camera = minecraft.getCamera();
		double cameraX = camera.prevX + (camera.x - camera.prevX) * tickDelta;
		double cameraY = camera.prevY + (camera.y - camera.prevY) * tickDelta;
		double cameraZ = camera.prevZ + (camera.z - camera.prevZ) * tickDelta;

		GlStateManager.pushMatrix();
		GlStateManager.translated(pos.getX() - cameraX, pos.getY() - cameraY, pos.getZ() - cameraZ);

		float r = ColorUtils.getRed(color) / 255.0F;
		float g = ColorUtils.getGreen(color) / 255.0F;
		float b = ColorUtils.getBlue(color) / 255.0F;

		drawFilledBox(bufferBuilder, tessellator, r, g, b, 0.5F);

		if (movable) {
			drawBoxOutline(bufferBuilder, tessellator, r, g, b, 1.0F);
		}

		GlStateManager.popMatrix();
	}

	private void drawFilledBox(BufferBuilder bufferBuilder, Tessellator tessellator, float r, float g, float b, float a) {
		bufferBuilder.begin(GL11.GL_QUADS, DefaultVertexFormat.POSITION_COLOR);
		drawBox(bufferBuilder, r, g, b, a, false);
		tessellator.end();
	}

	private void drawBoxOutline(BufferBuilder bufferBuilder, Tessellator tessellator, float r, float g, float b, float a) {
		bufferBuilder.begin(GL11.GL_LINES, DefaultVertexFormat.POSITION_COLOR);
		drawBox(bufferBuilder, r, g, b, a, true);
		tessellator.end();
	}

	private void drawBox(BufferBuilder bufferBuilder, float r, float g, float b, float a, boolean outline) {
		// The box is slightly larger than 1x1 to prevent z-fighting
		float c0 = -0.002F;
		float c1 = 1.002F;

		// West face
		bufferBuilder.vertex(c0, c0, c0).color(r, g, b, a).nextVertex();
		bufferBuilder.vertex(c0, c0, c1).color(r, g, b, a).nextVertex();
		bufferBuilder.vertex(c0, c1, c1).color(r, g, b, a).nextVertex();
		bufferBuilder.vertex(c0, c1, c0).color(r, g, b, a).nextVertex();
		if (outline) {
			bufferBuilder.vertex(c0, c0, c0).color(r, g, b, a).nextVertex();
		}

		// East face
		bufferBuilder.vertex(c1, c0, c0).color(r, g, b, a).nextVertex();
		bufferBuilder.vertex(c1, c1, c0).color(r, g, b, a).nextVertex();
		bufferBuilder.vertex(c1, c1, c1).color(r, g, b, a).nextVertex();
		bufferBuilder.vertex(c1, c0, c1).color(r, g, b, a).nextVertex();
		if (outline) {
			bufferBuilder.vertex(c1, c0, c0).color(r, g, b, a).nextVertex();
		}

		// North face
		bufferBuilder.vertex(c0, c0, c0).color(r, g, b, a).nextVertex();
		bufferBuilder.vertex(c0, c1, c0).color(r, g, b, a).nextVertex();
		bufferBuilder.vertex(c1, c1, c0).color(r, g, b, a).nextVertex();
		bufferBuilder.vertex(c1, c0, c0).color(r, g, b, a).nextVertex();
		if (outline) {
			bufferBuilder.vertex(c0, c0, c0).color(r, g, b, a).nextVertex();
		}

		// South face
		bufferBuilder.vertex(c0, c0, c1).color(r, g, b, a).nextVertex();
		bufferBuilder.vertex(c1, c0, c1).color(r, g, b, a).nextVertex();
		bufferBuilder.vertex(c1, c1, c1).color(r, g, b, a).nextVertex();
		bufferBuilder.vertex(c0, c1, c1).color(r, g, b, a).nextVertex();
		if (outline) {
			bufferBuilder.vertex(c0, c0, c1).color(r, g, b, a).nextVertex();
		}

		// Bottom face
		bufferBuilder.vertex(c0, c0, c0).color(r, g, b, a).nextVertex();
		bufferBuilder.vertex(c1, c0, c0).color(r, g, b, a).nextVertex();
		bufferBuilder.vertex(c1, c0, c1).color(r, g, b, a).nextVertex();
		bufferBuilder.vertex(c0, c0, c1).color(r, g, b, a).nextVertex();
		if (outline) {
			bufferBuilder.vertex(c0, c0, c0).color(r, g, b, a).nextVertex();
		}

		// Top face
		bufferBuilder.vertex(c0, c1, c0).color(r, g, b, a).nextVertex();
		bufferBuilder.vertex(c0, c1, c1).color(r, g, b, a).nextVertex();
		bufferBuilder.vertex(c1, c1, c1).color(r, g, b, a).nextVertex();
		bufferBuilder.vertex(c1, c1, c0).color(r, g, b, a).nextVertex();
		if (outline) {
			bufferBuilder.vertex(c0, c1, c0).color(r, g, b, a).nextVertex();
		}
	}
}
