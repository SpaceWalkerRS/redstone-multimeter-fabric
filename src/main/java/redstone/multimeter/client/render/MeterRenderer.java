package redstone.multimeter.client.render;

import java.util.function.Predicate;

import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.vertex.BufferBuilder;

import net.minecraft.client.Minecraft;
import net.minecraft.client.render.TextRenderer;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.entity.Entity;

import redstone.multimeter.client.MultimeterClient;
import redstone.multimeter.common.DimPos;
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

	public void renderMeterHighlights(float tickDelta) {
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glDisable(GL11.GL_CULL_FACE);

		renderMeters(this::shouldRenderHighlight, this::renderHighlight, tickDelta);
	}

	public void renderMeterNameTags(float tickDelta) {
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glEnable(GL11.GL_TEXTURE_2D);

		renderMeters(this::shouldRenderNameTag, this::renderNameTag, tickDelta);

		GL11.glEnable(GL11.GL_DEPTH_TEST);
	}

	private void renderMeters(Predicate<Meter> predicate, MeterPartRenderer renderer, float tickDelta) {
		ClientMeterGroup meterGroup = client.isPreviewing() ? client.getMeterGroupPreview() : client.getMeterGroup();

		for (Meter meter : meterGroup.getMeters()) {
			if (meter.isIn(minecraft.world) && predicate.test(meter)) {
				renderer.render(meter, tickDelta);
			}
		}
	}

	private boolean shouldRenderHighlight(Meter meter) {
		switch (Options.RedstoneMultimeter.RENDER_METERS.get()) {
		case ALWAYS:
			return true;
		case IN_FOCUS:
			return client.isPreviewing() || !client.getHud().isFocusMode() || client.getHud().getFocussedMeter() == meter;
		case IN_FOCUS_MODE:
			return !client.isPreviewing() && client.getHud().isFocusMode();
		case NEVER:
			return false;
		default:
			throw new IllegalStateException("unknown meter highlight mode " + Options.RedstoneMultimeter.RENDER_METERS.getAsString());
		}
	}

	private void renderHighlight(Meter meter, float tickDelta) {
		DimPos pos = meter.getPos();
		int color = meter.getColor();
		boolean movable = meter.isMovable();

		Entity camera = minecraft.camera;
		double cameraX = camera.prevX + (camera.x - camera.prevX) * tickDelta;
		double cameraY = camera.prevY + (camera.y - camera.prevY) * tickDelta;
		double cameraZ = camera.prevZ + (camera.z - camera.prevZ) * tickDelta;

		double dx = pos.getX() - cameraX;
		double dy = pos.getY() - cameraY;
		double dz = pos.getZ() - cameraZ;
		double distanceSquared = dx * dx + dy * dy + dz * dz;

		int range = Options.RedstoneMultimeter.METER_RANGE.get();
		double rangeSquared = range * range;

		if (range < 0 || distanceSquared < rangeSquared) {
			BufferBuilder tesselator = BufferBuilder.INSTANCE;

			int dim = getHighlightDimmingFactor(meter);

			float r = ColorUtils.getRed(color) / (float) 0xFF;
			float g = ColorUtils.getGreen(color) / (float) 0xFF;
			float b = ColorUtils.getBlue(color) / (float) 0xFF;

			GL11.glPushMatrix();;
			GL11.glTranslated(dx, dy, dz);

			renderMeterHighlight(tesselator, r, g, b, 0.5F / dim);

			if (movable) {
				renderMeterOutline(tesselator, r, g, b, 1.0F / dim);
			}

			GL11.glPopMatrix();
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
		case IN_FOCUS:
			return !client.isPreviewing() && client.getHud().isFocusMode() && client.getHud().getFocussedMeter() == meter;
		case WHEN_PREVIEWING:
			return client.isPreviewing();
		case NEVER:
			return false;
		default:
			throw new IllegalStateException("unknown meter name tag mode " + Options.RedstoneMultimeter.RENDER_METER_NAMES.getAsString());
		}
	}

	private void renderNameTag(Meter meter, float tickDelta) {
		String name = meter.getName();
		DimPos pos = meter.getPos();

		Entity camera = minecraft.camera;
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
			int dim = getNameTagDimmingFactor(meter);
			int color = ColorUtils.setAlpha(0xFFFFFF, 0xFF / dim);

			renderNameTag(minecraft.textRenderer, name, dx + 0.5D, dy + 0.75D, dz + 0.5D, color);
		}
	}

	private int getNameTagDimmingFactor(Meter meter) {
		return shouldDimMeter(meter) ? 2 : 1;
	}

	private boolean shouldDimMeter(Meter meter) {
		return !client.isPreviewing() && client.getHud().isFocusMode() && client.getHud().getFocussedMeter() != meter;
	}

	private void renderMeterHighlight(BufferBuilder tesselator, float r, float g, float b, float a) {
		tesselator.start(GL11.GL_QUADS);
		drawBox(tesselator, r, g, b, a, false);
		tesselator.end();
	}

	private void renderMeterOutline(BufferBuilder tesselator, float r, float g, float b, float a) {
		tesselator.start(GL11.GL_LINES);
		drawBox(tesselator, r, g, b, a, true);
		tesselator.end();
	}

	private void drawBox(BufferBuilder tesselator, float r, float g, float b, float a, boolean outline) {
		// The box is slightly larger than 1x1 to prevent z-fighting
		float c0 = -0.002F;
		float c1 = 1.002F;

		tesselator.color(r, g, b, a);

		// West face
		tesselator.vertex(c0, c0, c0);
		tesselator.vertex(c0, c0, c1);
		tesselator.vertex(c0, c1, c1);
		tesselator.vertex(c0, c1, c0);
		if (outline) {
			tesselator.vertex(c0, c0, c0);
		}

		// East face
		tesselator.vertex(c1, c0, c0);
		tesselator.vertex(c1, c1, c0);
		tesselator.vertex(c1, c1, c1);
		tesselator.vertex(c1, c0, c1);
		if (outline) {
			tesselator.vertex(c1, c0, c0);
		}

		// North face
		tesselator.vertex(c0, c0, c0);
		tesselator.vertex(c0, c1, c0);
		tesselator.vertex(c1, c1, c0);
		tesselator.vertex(c1, c0, c0);
		if (outline) {
			tesselator.vertex(c0, c0, c0);
		}

		// South face
		tesselator.vertex(c0, c0, c1);
		tesselator.vertex(c1, c0, c1);
		tesselator.vertex(c1, c1, c1);
		tesselator.vertex(c0, c1, c1);
		if (outline) {
			tesselator.vertex(c0, c0, c1);
		}

		// Bottom face
		tesselator.vertex(c0, c0, c0);
		tesselator.vertex(c1, c0, c0);
		tesselator.vertex(c1, c0, c1);
		tesselator.vertex(c0, c0, c1);
		if (outline) {
			tesselator.vertex(c0, c0, c0);
		}

		// Top face
		tesselator.vertex(c0, c1, c0);
		tesselator.vertex(c0, c1, c1);
		tesselator.vertex(c1, c1, c1);
		tesselator.vertex(c1, c1, c0);
		if (outline) {
			tesselator.vertex(c0, c1, c0);
		}
	}

	private void renderNameTag(TextRenderer textRenderer, String name, double dx, double dy, double dz, int color) {
		EntityRenderDispatcher entityRenderDispatcher = EntityRenderDispatcher.INSTANCE;

		float yaw = entityRenderDispatcher.cameraYaw;
		float pitch = entityRenderDispatcher.cameraPitch;

		GL11.glPushMatrix();
		GL11.glTranslated(dx, dy, dz);
		GL11.glNormal3f(0.0F, 1.0F, 0.0F);
		GL11.glRotatef(-yaw, 0.0F, 1.0F, 0.0F);
		GL11.glRotatef(pitch, 1.0F, 0.0F, 0.0F);
		GL11.glScalef(-0.025F, -0.025F, 0.025F);

		textRenderer.draw(name, -textRenderer.getWidth(name) / 2, 0, color);

		GL11.glPopMatrix();
	}

	@FunctionalInterface
	private interface MeterPartRenderer {

		void render(Meter meter, float tickDelta);

	}
}
