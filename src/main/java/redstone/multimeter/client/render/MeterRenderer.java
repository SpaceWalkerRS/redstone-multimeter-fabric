package redstone.multimeter.client.render;

import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.platform.GLX;
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
		GL11.glEnable(GL11.GL_BLEND);
		GLX.blendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glDepthMask(false);
		GL11.glEnable(GL11.GL_DEPTH_TEST);

		renderMeters(this::renderMeterHighlight, tickDelta);

		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glDepthMask(true);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glDisable(GL11.GL_BLEND);
	}

	public void renderMeterNames(float tickDelta) {
		MeterNameMode mode = Options.RedstoneMultimeter.RENDER_METER_NAMES.get();

		if (mode == MeterNameMode.ALWAYS
			|| (mode == MeterNameMode.WHEN_PREVIEWING && client.isPreviewing())
			|| (mode == MeterNameMode.IN_FOCUS_MODE && client.getHud().isFocusMode() && !client.isPreviewing())) {
			renderMeters(this::renderMeterName, tickDelta);
		}
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

		float r = ColorUtils.getRed(color) / 255.0F;
		float g = ColorUtils.getGreen(color) / 255.0F;
		float b = ColorUtils.getBlue(color) / 255.0F;

		renderMeterHighlight(bufferBuilder, r, g, b, 0.5F);

		if (movable) {
			renderMeterOutline(bufferBuilder, r, g, b, 1.0F);
		}

		GL11.glPopMatrix();
	}

	private void renderMeterName(Meter meter, float tickDelta) {
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

	private void drawBox(BufferBuilder bufferBuilder, float r, float g, float b, float a, boolean outline) {
		// The box is slightly larger than 1x1 to prevent z-fighting
		float c0 = -0.002F;
		float c1 = 1.002F;

		bufferBuilder.color(r, g, b, a);

		// West face
		bufferBuilder.vertex(c0, c0, c0);
		bufferBuilder.vertex(c0, c0, c1);
		bufferBuilder.vertex(c0, c1, c1);
		bufferBuilder.vertex(c0, c1, c0);
		if (outline) {
			bufferBuilder.vertex(c0, c0, c0);
		}

		// East face
		bufferBuilder.vertex(c1, c0, c0);
		bufferBuilder.vertex(c1, c1, c0);
		bufferBuilder.vertex(c1, c1, c1);
		bufferBuilder.vertex(c1, c0, c1);
		if (outline) {
			bufferBuilder.vertex(c1, c0, c0);
		}

		// North face
		bufferBuilder.vertex(c0, c0, c0);
		bufferBuilder.vertex(c0, c1, c0);
		bufferBuilder.vertex(c1, c1, c0);
		bufferBuilder.vertex(c1, c0, c0);
		if (outline) {
			bufferBuilder.vertex(c0, c0, c0);
		}

		// South face
		bufferBuilder.vertex(c0, c0, c1);
		bufferBuilder.vertex(c1, c0, c1);
		bufferBuilder.vertex(c1, c1, c1);
		bufferBuilder.vertex(c0, c1, c1);
		if (outline) {
			bufferBuilder.vertex(c0, c0, c1);
		}

		// Bottom face
		bufferBuilder.vertex(c0, c0, c0);
		bufferBuilder.vertex(c1, c0, c0);
		bufferBuilder.vertex(c1, c0, c1);
		bufferBuilder.vertex(c0, c0, c1);
		if (outline) {
			bufferBuilder.vertex(c0, c0, c0);
		}

		// Top face
		bufferBuilder.vertex(c0, c1, c0);
		bufferBuilder.vertex(c0, c1, c1);
		bufferBuilder.vertex(c1, c1, c1);
		bufferBuilder.vertex(c1, c1, c0);
		if (outline) {
			bufferBuilder.vertex(c0, c1, c0);
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
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glDepthMask(true);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GLX.blendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
		GL11.glEnable(GL11.GL_TEXTURE_2D);

		textRenderer.draw(name, -textRenderer.getWidth(name) / 2, 0, 0xFFFFFFFF);

		GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		GL11.glPopMatrix();
	}

	@FunctionalInterface
	private interface MeterPartRenderer {

		void render(Meter meter, float tickDelta);

	}
}
