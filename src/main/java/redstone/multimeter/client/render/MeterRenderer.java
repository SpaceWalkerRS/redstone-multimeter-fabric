package redstone.multimeter.client.render;

import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tessellator;

import net.minecraft.client.Minecraft;
import net.minecraft.client.render.TextRenderer;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;

import redstone.multimeter.client.MultimeterClient;
import redstone.multimeter.client.meter.ClientMeterGroup;
import redstone.multimeter.client.option.Options;
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
		GlStateManager.disableTexture();
		GlStateManager.disableCull();

		renderMeters(this::renderMeterHighlight, tickDelta);
	}

	public void renderMeterNameTags(float tickDelta) {
		GlStateManager.disableDepthTest();
		GlStateManager.enableTexture();

		MeterNameMode mode = Options.RedstoneMultimeter.RENDER_METER_NAMES.get();

		if (mode == MeterNameMode.ALWAYS
			|| (mode == MeterNameMode.WHEN_PREVIEWING && client.isPreviewing())
			|| (mode == MeterNameMode.IN_FOCUS_MODE && client.getHud().isFocusMode() && !client.isPreviewing())) {
			renderMeters(this::renderMeterNameTag, tickDelta);
		}

		GlStateManager.enableDepthTest();
	}

	private void renderMeters(MeterPartRenderer renderer, float tickDelta) {
		if (client.isPreviewing() || !client.getHud().isFocusMode()) {
			ClientMeterGroup meterGroup = client.isPreviewing() ? client.getMeterGroupPreview() : client.getMeterGroup();

			for (Meter meter : meterGroup.getMeters()) {
				if (meter.isIn(minecraft.world)) {
					renderer.render(meter, tickDelta);
				}
			}
		} else {
			Meter focussed = client.getHud().getFocussedMeter();

			if (focussed != null) {
				if (focussed.isIn(minecraft.world)) {
					renderer.render(focussed, tickDelta);
				}
			}
		}
	}

	private void renderMeterHighlight(Meter meter, float tickDelta) {
		Tessellator tesselator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tesselator.getBuilder();

		BlockPos pos = meter.getPos().getBlockPos();
		int color = meter.getColor();
		boolean movable = meter.isMovable();

		Entity camera = minecraft.getCamera();
		double cameraX = camera.prevX + (camera.x - camera.prevX) * tickDelta;
		double cameraY = camera.prevY + (camera.y - camera.prevY) * tickDelta;
		double cameraZ = camera.prevZ + (camera.z - camera.prevZ) * tickDelta;

		double dx = pos.getX() - cameraX;
		double dy = pos.getY() - cameraY;
		double dz = pos.getZ() - cameraZ;

		GlStateManager.pushMatrix();;
		GlStateManager.translated(dx, dy, dz);

		float r = ColorUtils.getRed(color) / (float) 0xFF;
		float g = ColorUtils.getGreen(color) / (float) 0xFF;
		float b = ColorUtils.getBlue(color) / (float) 0xFF;

		renderMeterHighlight(bufferBuilder, tesselator, r, g, b, 0.5F);

		if (movable) {
			renderMeterOutline(bufferBuilder, tesselator, r, g, b, 1.0F);
		}

		GlStateManager.popMatrix();
	}

	private void renderMeterNameTag(Meter meter, float tickDelta) {
		String name = meter.getName();
		BlockPos pos = meter.getPos().getBlockPos();

		Entity camera = minecraft.getCamera();
		double cameraX = camera.prevX + (camera.x - camera.prevX) * tickDelta;
		double cameraY = camera.prevY + (camera.y - camera.prevY) * tickDelta;
		double cameraZ = camera.prevZ + (camera.z - camera.prevZ) * tickDelta;

		double dx = pos.getX() - cameraX;
		double dy = pos.getY() - cameraY;
		double dz = pos.getZ() - cameraZ;
		double distanceSquared = dx * dx + dy * dy + dz * dz;

		int range = Options.RedstoneMultimeter.METER_NAME_RANGE.get();
		double rangeSquared = range * range;

		if (distanceSquared < rangeSquared) {
			renderNameTag(minecraft.textRenderer, name, dx + 0.5D, dy + 0.75D, dz + 0.5D);
		}
	}

	private void renderMeterHighlight(BufferBuilder bufferBuilder, Tessellator tessellator, float r, float g, float b, float a) {
		bufferBuilder.begin(GL11.GL_QUADS, DefaultVertexFormat.POSITION_COLOR);
		drawBox(bufferBuilder, r, g, b, a, false);
		tessellator.end();
	}

	private void renderMeterOutline(BufferBuilder bufferBuilder, Tessellator tessellator, float r, float g, float b, float a) {
		bufferBuilder.begin(GL11.GL_LINES, DefaultVertexFormat.POSITION_COLOR);
		drawBox(bufferBuilder, r, g, b, a, true);
		tessellator.end();
	}

	private void drawBox(BufferBuilder buffer, float r, float g, float b, float a, boolean outline) {
		// The box is slightly larger than 1x1 to prevent z-fighting
		float c0 = -0.002F;
		float c1 = 1.002F;

		// West face
		buffer.vertex(c0, c0, c0).color(r, g, b, a).nextVertex();
		buffer.vertex(c0, c0, c1).color(r, g, b, a).nextVertex();
		buffer.vertex(c0, c1, c1).color(r, g, b, a).nextVertex();
		buffer.vertex(c0, c1, c0).color(r, g, b, a).nextVertex();
		if (outline) {
			buffer.vertex(c0, c0, c0).color(r, g, b, a).nextVertex();
		}

		// East face
		buffer.vertex(c1, c0, c0).color(r, g, b, a).nextVertex();
		buffer.vertex(c1, c1, c0).color(r, g, b, a).nextVertex();
		buffer.vertex(c1, c1, c1).color(r, g, b, a).nextVertex();
		buffer.vertex(c1, c0, c1).color(r, g, b, a).nextVertex();
		if (outline) {
			buffer.vertex(c1, c0, c0).color(r, g, b, a).nextVertex();
		}

		// North face
		buffer.vertex(c0, c0, c0).color(r, g, b, a).nextVertex();
		buffer.vertex(c0, c1, c0).color(r, g, b, a).nextVertex();
		buffer.vertex(c1, c1, c0).color(r, g, b, a).nextVertex();
		buffer.vertex(c1, c0, c0).color(r, g, b, a).nextVertex();
		if (outline) {
			buffer.vertex(c0, c0, c0).color(r, g, b, a).nextVertex();
		}

		// South face
		buffer.vertex(c0, c0, c1).color(r, g, b, a).nextVertex();
		buffer.vertex(c1, c0, c1).color(r, g, b, a).nextVertex();
		buffer.vertex(c1, c1, c1).color(r, g, b, a).nextVertex();
		buffer.vertex(c0, c1, c1).color(r, g, b, a).nextVertex();
		if (outline) {
			buffer.vertex(c0, c0, c1).color(r, g, b, a).nextVertex();
		}

		// Bottom face
		buffer.vertex(c0, c0, c0).color(r, g, b, a).nextVertex();
		buffer.vertex(c1, c0, c0).color(r, g, b, a).nextVertex();
		buffer.vertex(c1, c0, c1).color(r, g, b, a).nextVertex();
		buffer.vertex(c0, c0, c1).color(r, g, b, a).nextVertex();
		if (outline) {
			buffer.vertex(c0, c0, c0).color(r, g, b, a).nextVertex();
		}

		// Top face
		buffer.vertex(c0, c1, c0).color(r, g, b, a).nextVertex();
		buffer.vertex(c0, c1, c1).color(r, g, b, a).nextVertex();
		buffer.vertex(c1, c1, c1).color(r, g, b, a).nextVertex();
		buffer.vertex(c1, c1, c0).color(r, g, b, a).nextVertex();
		if (outline) {
			buffer.vertex(c0, c1, c0).color(r, g, b, a).nextVertex();
		}
	}

	private void renderNameTag(TextRenderer textRenderer, String name, double dx, double dy, double dz) {
		EntityRenderDispatcher entityRenderDispatcher = this.minecraft.getEntityRenderDispatcher();

		float yaw = entityRenderDispatcher.cameraYaw;
		float pitch = entityRenderDispatcher.cameraPitch;

		GlStateManager.pushMatrix();
		GlStateManager.translated(dx, dy, dz);
		GlStateManager.normal3f(0.0F, 1.0F, 0.0F);
		GlStateManager.rotatef(-yaw, 0.0F, 1.0F, 0.0F);
		GlStateManager.rotatef(pitch, 1.0F, 0.0F, 0.0F);
		GlStateManager.scalef(-0.025F, -0.025F, 0.025F);

		textRenderer.draw(name, -textRenderer.getWidth(name) / 2, 0, 0xFFFFFFFF);

		GlStateManager.popMatrix();
	}

	@FunctionalInterface
	private interface MeterPartRenderer {

		void render(Meter meter, float tickDelta);

	}
}
