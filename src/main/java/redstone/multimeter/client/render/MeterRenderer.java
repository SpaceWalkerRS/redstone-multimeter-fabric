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

	public void renderMeters() {
		GlStateManager.enableBlend();
		GlStateManager.blendFuncSeparate(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA, SourceFactor.ONE, DestFactor.ZERO);
		GlStateManager.disableTexture();
		GlStateManager.enableDepthTest();
		GlStateManager.depthMask(false);

		renderMeters(this::renderMeterHighlight);

		GlStateManager.depthMask(true);
		GlStateManager.enableTexture();
		GlStateManager.disableBlend();
	}

	public void renderMeterNames() {
		MeterNameMode mode = Options.RedstoneMultimeter.RENDER_METER_NAMES.get();

		if (mode == MeterNameMode.ALWAYS
			|| (mode == MeterNameMode.WHEN_PREVIEWING && client.isPreviewing())
			|| (mode == MeterNameMode.IN_FOCUS_MODE && client.getHud().isFocusMode() && !client.isPreviewing())) {
			renderMeters(this::renderMeterName);
		}
	}

	private void renderMeters(MeterPartRenderer renderer) {
		if (client.isPreviewing() || !client.getHud().isFocusMode()) {
			ClientMeterGroup meterGroup = client.isPreviewing() ? client.getMeterGroupPreview() : client.getMeterGroup();

			for (Meter meter : meterGroup.getMeters()) {
				if (meter.isIn(minecraft.level)) {
					renderer.render(meter);
				}
			}
		} else {
			Meter focussed = client.getHud().getFocussedMeter();

			if (focussed != null) {
				if (focussed.isIn(minecraft.level)) {
					renderer.render(focussed);
				}
			}
		}
	}

	private void renderMeterHighlight(Meter meter) {
		Tesselator tesselator = Tesselator.getInstance();
		BufferBuilder bufferBuilder = tesselator.getBuilder();

		BlockPos pos = meter.getPos().getBlockPos();
		int color = meter.getColor();
		boolean movable = meter.isMovable();

		Camera camera = minecraft.gameRenderer.getMainCamera();
		Vec3 cameraPos = camera.getPosition();

		double dx = pos.getX() - cameraPos.x;
		double dy = pos.getY() - cameraPos.y;
		double dz = pos.getZ() - cameraPos.z;

		GlStateManager.pushMatrix();;
		GlStateManager.translated(dx, dy, dz);

		float r = ColorUtils.getRed(color) / 255.0F;
		float g = ColorUtils.getGreen(color) / 255.0F;
		float b = ColorUtils.getBlue(color) / 255.0F;

		renderMeterHighlight(bufferBuilder, tesselator, r, g, b, 0.5F);

		if (movable) {
			renderMeterOutline(bufferBuilder, tesselator, r, g, b, 1.0F);
		}

		GlStateManager.popMatrix();
	}

	private void renderMeterName(Meter meter) {
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
			renderNameTag(minecraft.font, name, dx + 0.5D, dy + 0.75D, dz + 0.5D);
		}
	}

	private void renderMeterHighlight(BufferBuilder bufferBuilder, Tesselator tessellator, float r, float g, float b, float a) {
		bufferBuilder.begin(GL11.GL_QUADS, DefaultVertexFormat.POSITION_COLOR);
		drawBox(bufferBuilder, r, g, b, a, false);
		tessellator.end();
	}

	private void renderMeterOutline(BufferBuilder bufferBuilder, Tesselator tessellator, float r, float g, float b, float a) {
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

	private void renderNameTag(Font font, String name, double dx, double dy, double dz) {
		EntityRenderDispatcher entityRenderDispatcher = this.minecraft.getEntityRenderDispatcher();

		float rotX = entityRenderDispatcher.playerRotX;
		float rotY = entityRenderDispatcher.playerRotY;

		GlStateManager.pushMatrix();
		GlStateManager.translated(dx, dy, dz);
		GlStateManager.normal3f(0.0F, 1.0F, 0.0F);
		GlStateManager.rotatef(-rotY, 0.0F, 1.0F, 0.0F);
		GlStateManager.rotatef(rotX, 1.0F, 0.0F, 0.0F);
		GlStateManager.scalef(-0.025F, -0.025F, 0.025F);
		GlStateManager.enableBlend();
		GlStateManager.disableLighting();
		GlStateManager.depthMask(true);
		GlStateManager.disableDepthTest();
		GlStateManager.blendFuncSeparate(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA, SourceFactor.ONE, DestFactor.ZERO);
		GlStateManager.enableTexture();

		font.draw(name, -font.width(name) / 2, 0, 0xFFFFFFFF);

		GlStateManager.enableDepthTest();
		GlStateManager.enableLighting();
		GlStateManager.disableBlend();
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.popMatrix();
	}

	@FunctionalInterface
	private interface MeterPartRenderer {

		void render(Meter meter);

	}
}
