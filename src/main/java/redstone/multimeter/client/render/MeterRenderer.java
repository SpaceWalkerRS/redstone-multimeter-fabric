package redstone.multimeter.client.render;

import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.GlStateManager.DestFactor;
import com.mojang.blaze3d.platform.GlStateManager.SourceFactor;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;

import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;

import redstone.multimeter.client.MultimeterClient;
import redstone.multimeter.client.meter.ClientMeterGroup;
import redstone.multimeter.common.meter.Meter;
import redstone.multimeter.util.ColorUtils;

public class MeterRenderer {

	private final MultimeterClient client;
	private final Minecraft minecraft;

	public MeterRenderer(MultimeterClient client) {
		this.client = client;
		this.minecraft = this.client.getMinecraft();
	}

	public void renderMeters() {
		GlStateManager.enableBlend();
		GlStateManager.blendFuncSeparate(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA, SourceFactor.ONE, DestFactor.ZERO);
		GlStateManager.disableTexture();
		GlStateManager.depthMask(false);
		GlStateManager.enableDepthTest();

		if (client.isPreviewing() || !client.getHud().isFocusMode()) {
			ClientMeterGroup meterGroup = client.isPreviewing() ? client.getMeterGroupPreview() : client.getMeterGroup();

			for (Meter meter : meterGroup.getMeters()) {
				if (meter.isIn(minecraft.level)) {
					drawMeter(meter);
				}
			}
		} else {
			Meter focussed = client.getHud().getFocussedMeter();

			if (focussed != null) {
				if (focussed.isIn(minecraft.level)) {
					drawMeter(focussed);
				}
			}
		}

		GlStateManager.disableDepthTest();
		GlStateManager.depthMask(true);
		GlStateManager.enableTexture();
		GlStateManager.disableBlend();
	}

	private void drawMeter(Meter meter) {
		Tesselator tessellator = Tesselator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBuilder();

		BlockPos pos = meter.getPos().getBlockPos();
		int color = meter.getColor();
		boolean movable = meter.isMovable();

		Camera camera = minecraft.gameRenderer.getMainCamera();
		Vec3 cameraPos = camera.getPosition();

		GlStateManager.pushMatrix();
		GlStateManager.translated(pos.getX() - cameraPos.x, pos.getY() - cameraPos.y, pos.getZ() - cameraPos.z);

		float r = ColorUtils.getRed(color) / 255.0F;
		float g = ColorUtils.getGreen(color) / 255.0F;
		float b = ColorUtils.getBlue(color) / 255.0F;

		drawFilledBox(bufferBuilder, tessellator, r, g, b, 0.5F);

		if (movable) {
			drawBoxOutline(bufferBuilder, tessellator, r, g, b, 1.0F);
		}

		GlStateManager.popMatrix();
	}

	private void drawFilledBox(BufferBuilder bufferBuilder, Tesselator tessellator, float r, float g, float b, float a) {
		bufferBuilder.begin(GL11.GL_QUADS, DefaultVertexFormat.POSITION_COLOR);
		drawBox(bufferBuilder, r, g, b, a, false);
		tessellator.end();
	}

	private void drawBoxOutline(BufferBuilder bufferBuilder, Tesselator tessellator, float r, float g, float b, float a) {
		bufferBuilder.begin(GL11.GL_LINES, DefaultVertexFormat.POSITION_COLOR);
		drawBox(bufferBuilder, r, g, b, a, true);
		tessellator.end();
	}

	private void drawBox(BufferBuilder bufferBuilder, float r, float g, float b, float a, boolean outline) {
		// The box is slightly larger than 1x1 to prevent z-fighting
		float c0 = -0.002F;
		float c1 = 1.002F;

		// West face
		bufferBuilder.vertex(c0, c0, c0).color(r, g, b, a).endVertex();
		bufferBuilder.vertex(c0, c0, c1).color(r, g, b, a).endVertex();
		bufferBuilder.vertex(c0, c1, c1).color(r, g, b, a).endVertex();
		bufferBuilder.vertex(c0, c1, c0).color(r, g, b, a).endVertex();
		if (outline) {
			bufferBuilder.vertex(c0, c0, c0).color(r, g, b, a).endVertex();
		}

		// East face
		bufferBuilder.vertex(c1, c0, c0).color(r, g, b, a).endVertex();
		bufferBuilder.vertex(c1, c1, c0).color(r, g, b, a).endVertex();
		bufferBuilder.vertex(c1, c1, c1).color(r, g, b, a).endVertex();
		bufferBuilder.vertex(c1, c0, c1).color(r, g, b, a).endVertex();
		if (outline) {
			bufferBuilder.vertex(c1, c0, c0).color(r, g, b, a).endVertex();
		}

		// North face
		bufferBuilder.vertex(c0, c0, c0).color(r, g, b, a).endVertex();
		bufferBuilder.vertex(c0, c1, c0).color(r, g, b, a).endVertex();
		bufferBuilder.vertex(c1, c1, c0).color(r, g, b, a).endVertex();
		bufferBuilder.vertex(c1, c0, c0).color(r, g, b, a).endVertex();
		if (outline) {
			bufferBuilder.vertex(c0, c0, c0).color(r, g, b, a).endVertex();
		}

		// South face
		bufferBuilder.vertex(c0, c0, c1).color(r, g, b, a).endVertex();
		bufferBuilder.vertex(c1, c0, c1).color(r, g, b, a).endVertex();
		bufferBuilder.vertex(c1, c1, c1).color(r, g, b, a).endVertex();
		bufferBuilder.vertex(c0, c1, c1).color(r, g, b, a).endVertex();
		if (outline) {
			bufferBuilder.vertex(c0, c0, c1).color(r, g, b, a).endVertex();
		}

		// Bottom face
		bufferBuilder.vertex(c0, c0, c0).color(r, g, b, a).endVertex();
		bufferBuilder.vertex(c1, c0, c0).color(r, g, b, a).endVertex();
		bufferBuilder.vertex(c1, c0, c1).color(r, g, b, a).endVertex();
		bufferBuilder.vertex(c0, c0, c1).color(r, g, b, a).endVertex();
		if (outline) {
			bufferBuilder.vertex(c0, c0, c0).color(r, g, b, a).endVertex();
		}

		// Top face
		bufferBuilder.vertex(c0, c1, c0).color(r, g, b, a).endVertex();
		bufferBuilder.vertex(c0, c1, c1).color(r, g, b, a).endVertex();
		bufferBuilder.vertex(c1, c1, c1).color(r, g, b, a).endVertex();
		bufferBuilder.vertex(c1, c1, c0).color(r, g, b, a).endVertex();
		if (outline) {
			bufferBuilder.vertex(c0, c1, c0).color(r, g, b, a).endVertex();
		}
	}
}
