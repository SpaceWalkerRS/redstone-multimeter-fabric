package redstone.multimeter.client.render;

import java.util.function.Predicate;

import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;

import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;

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

	public void renderMeterHighlights() {
		GlStateManager.disableTexture();
		GlStateManager.disableCull();

		renderMeters(this::shouldRenderHighlight, this::renderHighlight);
	}

	public void renderMeterNameTags() {
		GlStateManager.disableDepthTest();
		GlStateManager.enableTexture();

		renderMeters(this::shouldRenderNameTag, this::renderNameTag);

		GlStateManager.enableDepthTest();
	}

	private void renderMeters(Predicate<Meter> predicate, MeterPartRenderer renderer) {
		ClientMeterGroup meterGroup = client.isPreviewing() ? client.getMeterGroupPreview() : client.getMeterGroup();

		for (Meter meter : meterGroup.getMeters()) {
			if (meter.isIn(minecraft.level) && predicate.test(meter)) {
				renderer.render(meter);
			}
		}
	}

	private boolean shouldRenderHighlight(Meter meter) {
		switch (Options.RedstoneMultimeter.RENDER_METERS.get()) {
		case ALWAYS:
			return true;
		case IN_FOCUS:
			return !client.isPreviewing() && client.getHud().isFocusMode() && client.getHud().getFocussedMeter() == meter;
		case IN_FOCUS_MODE:
			return !client.isPreviewing() && client.getHud().isFocusMode();
		case NEVER:
			return false;
		default:
			throw new IllegalStateException("unknown meter highlight mode " + Options.RedstoneMultimeter.RENDER_METERS.getAsString());
		}
	}

	private void renderHighlight(Meter meter) {
		BlockPos pos = meter.getPos().getBlockPos();
		int color = meter.getColor();
		boolean movable = meter.isMovable();

		Camera camera = minecraft.gameRenderer.getMainCamera();
		Vec3 cameraPos = camera.getPosition();

		double dx = pos.getX() - cameraPos.x;
		double dy = pos.getY() - cameraPos.y;
		double dz = pos.getZ() - cameraPos.z;
		double distanceSquared = dx * dx + dy * dy + dz * dz;

		int range = Options.RedstoneMultimeter.METER_RANGE.get();
		double rangeSquared = range * range;

		if (range < 0 || distanceSquared < rangeSquared) {
			Tesselator tesselator = Tesselator.getInstance();
			BufferBuilder buffer = tesselator.getBuilder();

			int dim = getHighlightDimmingFactor(meter);

			float r = ColorUtils.getRed(color) / (float) 0xFF;
			float g = ColorUtils.getGreen(color) / (float) 0xFF;
			float b = ColorUtils.getBlue(color) / (float) 0xFF;

			GlStateManager.pushMatrix();;
			GlStateManager.translated(dx, dy, dz);

			renderMeterHighlight(tesselator, buffer, r, g, b, 0.5F / dim);

			if (movable) {
				renderMeterOutline(tesselator, buffer, r, g, b, 1.0F / dim);
			}

			GlStateManager.popMatrix();
		}
	}

	private int getHighlightDimmingFactor(Meter meter) {
		return shouldDimMeter(meter) ? 3 : 1;
	}

	private boolean shouldRenderNameTag(Meter meter) {
		switch (Options.RedstoneMultimeter.RENDER_METER_NAMES.get()) {
		case ALWAYS:
			return true;
		case IN_FOCUS_MODE:
			return !client.isPreviewing() && client.getHud().isFocusMode();
		case WHEN_PREVIEWING:
			return client.isPreviewing();
		case NEVER:
			return false;
		default:
			throw new IllegalStateException("unknown meter name tag mode " + Options.RedstoneMultimeter.RENDER_METER_NAMES.getAsString());
		}
	}

	private void renderNameTag(Meter meter) {
		String name = meter.getName();
		BlockPos pos = meter.getPos().getBlockPos();

		Camera camera = minecraft.gameRenderer.getMainCamera();
		Vec3 cameraPos = camera.getPosition();

		double dx = pos.getX() - cameraPos.x;
		double dy = pos.getY() - cameraPos.y;
		double dz = pos.getZ() - cameraPos.z;
		double distanceSquared = dx * dx + dy * dy + dz * dz;

		int range = Options.RedstoneMultimeter.METER_NAME_RANGE.get();
		double rangeSquared = range * range;

		if (distanceSquared < rangeSquared) {
			int dim = getNameTagDimmingFactor(meter);
			int color = ColorUtils.setAlpha(0xFFFFFF, 0xFF / dim);

			renderNameTag(minecraft.font, name, dx + 0.5D, dy + 0.75D, dz + 0.5D, color);
		}
	}


	private int getNameTagDimmingFactor(Meter meter) {
		return shouldDimMeter(meter) ? 2 : 1;
	}

	private boolean shouldDimMeter(Meter meter) {
		return !client.isPreviewing() && client.getHud().isFocusMode() && client.getHud().getFocussedMeter() != meter;
	}

	private void renderMeterHighlight(Tesselator tesselator, BufferBuilder buffer, float r, float g, float b, float a) {
		buffer.begin(GL11.GL_QUADS, DefaultVertexFormat.POSITION_COLOR);
		drawBox(buffer, r, g, b, a, false);
		tesselator.end();
	}

	private void renderMeterOutline(Tesselator tesselator, BufferBuilder buffer, float r, float g, float b, float a) {
		buffer.begin(GL11.GL_LINES, DefaultVertexFormat.POSITION_COLOR);
		drawBox(buffer, r, g, b, a, true);
		tesselator.end();
	}

	private void drawBox(BufferBuilder buffer, float r, float g, float b, float a, boolean outline) {
		// The box is slightly larger than 1x1 to prevent z-fighting
		float c0 = -0.002F;
		float c1 = 1.002F;

		// West face
		buffer.vertex(c0, c0, c0).color(r, g, b, a).endVertex();
		buffer.vertex(c0, c0, c1).color(r, g, b, a).endVertex();
		buffer.vertex(c0, c1, c1).color(r, g, b, a).endVertex();
		buffer.vertex(c0, c1, c0).color(r, g, b, a).endVertex();
		if (outline) {
			buffer.vertex(c0, c0, c0).color(r, g, b, a).endVertex();
		}

		// East face
		buffer.vertex(c1, c0, c0).color(r, g, b, a).endVertex();
		buffer.vertex(c1, c1, c0).color(r, g, b, a).endVertex();
		buffer.vertex(c1, c1, c1).color(r, g, b, a).endVertex();
		buffer.vertex(c1, c0, c1).color(r, g, b, a).endVertex();
		if (outline) {
			buffer.vertex(c1, c0, c0).color(r, g, b, a).endVertex();
		}

		// North face
		buffer.vertex(c0, c0, c0).color(r, g, b, a).endVertex();
		buffer.vertex(c0, c1, c0).color(r, g, b, a).endVertex();
		buffer.vertex(c1, c1, c0).color(r, g, b, a).endVertex();
		buffer.vertex(c1, c0, c0).color(r, g, b, a).endVertex();
		if (outline) {
			buffer.vertex(c0, c0, c0).color(r, g, b, a).endVertex();
		}

		// South face
		buffer.vertex(c0, c0, c1).color(r, g, b, a).endVertex();
		buffer.vertex(c1, c0, c1).color(r, g, b, a).endVertex();
		buffer.vertex(c1, c1, c1).color(r, g, b, a).endVertex();
		buffer.vertex(c0, c1, c1).color(r, g, b, a).endVertex();
		if (outline) {
			buffer.vertex(c0, c0, c1).color(r, g, b, a).endVertex();
		}

		// Bottom face
		buffer.vertex(c0, c0, c0).color(r, g, b, a).endVertex();
		buffer.vertex(c1, c0, c0).color(r, g, b, a).endVertex();
		buffer.vertex(c1, c0, c1).color(r, g, b, a).endVertex();
		buffer.vertex(c0, c0, c1).color(r, g, b, a).endVertex();
		if (outline) {
			buffer.vertex(c0, c0, c0).color(r, g, b, a).endVertex();
		}

		// Top face
		buffer.vertex(c0, c1, c0).color(r, g, b, a).endVertex();
		buffer.vertex(c0, c1, c1).color(r, g, b, a).endVertex();
		buffer.vertex(c1, c1, c1).color(r, g, b, a).endVertex();
		buffer.vertex(c1, c1, c0).color(r, g, b, a).endVertex();
		if (outline) {
			buffer.vertex(c0, c1, c0).color(r, g, b, a).endVertex();
		}
	}

	private void renderNameTag(Font font, String name, double dx, double dy, double dz, int color) {
		EntityRenderDispatcher entityRenderDispatcher = this.minecraft.getEntityRenderDispatcher();

		float rotX = entityRenderDispatcher.playerRotX;
		float rotY = entityRenderDispatcher.playerRotY;

		GlStateManager.pushMatrix();
		GlStateManager.translated(dx, dy, dz);
		GlStateManager.normal3f(0.0F, 1.0F, 0.0F);
		GlStateManager.rotatef(-rotY, 0.0F, 1.0F, 0.0F);
		GlStateManager.rotatef(rotX, 1.0F, 0.0F, 0.0F);
		GlStateManager.scalef(-0.025F, -0.025F, 0.025F);

		font.draw(name, -font.width(name) / 2, 0, color);

		GlStateManager.popMatrix();
	}

	@FunctionalInterface
	private interface MeterPartRenderer {

		void render(Meter meter);

	}
}
