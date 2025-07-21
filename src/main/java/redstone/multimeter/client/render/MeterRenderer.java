package redstone.multimeter.client.render;

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

	public void renderMeters(float tickDelta) {
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glDisable(GL11.GL_CULL_FACE);

		renderMeters(this::renderMeterHighlight, tickDelta);
	}

	public void renderMeterNameTags(float tickDelta) {
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glEnable(GL11.GL_TEXTURE_2D);

		MeterNameMode mode = Options.RedstoneMultimeter.RENDER_METER_NAMES.get();

		if (mode == MeterNameMode.ALWAYS
			|| (mode == MeterNameMode.WHEN_PREVIEWING && client.isPreviewing())
			|| (mode == MeterNameMode.IN_FOCUS_MODE && client.getHud().isFocusMode() && !client.isPreviewing())) {
			renderMeters(this::renderMeterNameTag, tickDelta);
		}

		GL11.glEnable(GL11.GL_DEPTH_TEST);
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
		BufferBuilder bufferBuilder = BufferBuilder.INSTANCE;

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

		GL11.glPushMatrix();;
		GL11.glTranslated(dx, dy, dz);

		float r = ColorUtils.getRed(color) / (float) 0xFF;
		float g = ColorUtils.getGreen(color) / (float) 0xFF;
		float b = ColorUtils.getBlue(color) / (float) 0xFF;

		renderMeterHighlight(bufferBuilder, r, g, b, 0.5F);

		if (movable) {
			renderMeterOutline(bufferBuilder, r, g, b, 1.0F);
		}

		GL11.glPopMatrix();
	}

	private void renderMeterNameTag(Meter meter, float tickDelta) {
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
			renderNameTag(minecraft.textRenderer, name, dx + 0.5D, dy + 0.75D, dz + 0.5D);
		}
	}

	private void renderMeterHighlight(BufferBuilder bufferBuilder, float r, float g, float b, float a) {
		bufferBuilder.start(GL11.GL_QUADS);
		drawBox(bufferBuilder, r, g, b, a, false);
		bufferBuilder.end();
	}

	private void renderMeterOutline(BufferBuilder bufferBuilder, float r, float g, float b, float a) {
		bufferBuilder.start(GL11.GL_LINES);
		drawBox(bufferBuilder, r, g, b, a, true);
		bufferBuilder.end();
	}

	private void drawBox(BufferBuilder buffer, float r, float g, float b, float a, boolean outline) {
		// The box is slightly larger than 1x1 to prevent z-fighting
		float c0 = -0.002F;
		float c1 = 1.002F;

		buffer.color(r, g, b, a);

		// West face
		buffer.vertex(c0, c0, c0);
		buffer.vertex(c0, c0, c1);
		buffer.vertex(c0, c1, c1);
		buffer.vertex(c0, c1, c0);
		if (outline) {
			buffer.vertex(c0, c0, c0);
		}

		// East face
		buffer.vertex(c1, c0, c0);
		buffer.vertex(c1, c1, c0);
		buffer.vertex(c1, c1, c1);
		buffer.vertex(c1, c0, c1);
		if (outline) {
			buffer.vertex(c1, c0, c0);
		}

		// North face
		buffer.vertex(c0, c0, c0);
		buffer.vertex(c0, c1, c0);
		buffer.vertex(c1, c1, c0);
		buffer.vertex(c1, c0, c0);
		if (outline) {
			buffer.vertex(c0, c0, c0);
		}

		// South face
		buffer.vertex(c0, c0, c1);
		buffer.vertex(c1, c0, c1);
		buffer.vertex(c1, c1, c1);
		buffer.vertex(c0, c1, c1);
		if (outline) {
			buffer.vertex(c0, c0, c1);
		}

		// Bottom face
		buffer.vertex(c0, c0, c0);
		buffer.vertex(c1, c0, c0);
		buffer.vertex(c1, c0, c1);
		buffer.vertex(c0, c0, c1);
		if (outline) {
			buffer.vertex(c0, c0, c0);
		}

		// Top face
		buffer.vertex(c0, c1, c0);
		buffer.vertex(c0, c1, c1);
		buffer.vertex(c1, c1, c1);
		buffer.vertex(c1, c1, c0);
		if (outline) {
			buffer.vertex(c0, c1, c0);
		}
	}

	private void renderNameTag(TextRenderer textRenderer, String name, double dx, double dy, double dz) {
		EntityRenderDispatcher entityRenderDispatcher = EntityRenderDispatcher.INSTANCE;

		float yaw = entityRenderDispatcher.cameraYaw;
		float pitch = entityRenderDispatcher.cameraPitch;

		GL11.glPushMatrix();
		GL11.glTranslated(dx, dy, dz);
		GL11.glNormal3f(0.0F, 1.0F, 0.0F);
		GL11.glRotatef(-yaw, 0.0F, 1.0F, 0.0F);
		GL11.glRotatef(pitch, 1.0F, 0.0F, 0.0F);
		GL11.glScalef(-0.025F, -0.025F, 0.025F);

		textRenderer.draw(name, -textRenderer.getWidth(name) / 2, 0, 0xFFFFFFFF);

		GL11.glPopMatrix();
	}

	@FunctionalInterface
	private interface MeterPartRenderer {

		void render(Meter meter, float tickDelta);

	}
}
