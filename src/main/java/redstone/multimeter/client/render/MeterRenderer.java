package redstone.multimeter.client.render;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;

import com.mojang.math.Matrix4f;

import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
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

	public void renderMeters(PoseStack poses) {
		RenderSystem.setShader(() -> GameRenderer.getPositionColorShader());
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		RenderSystem.disableTexture();
		RenderSystem.enableDepthTest();
		RenderSystem.depthMask(false);
		RenderSystem.enableDepthTest();

		renderMeters(poses, null, this::renderMeterHighlight);

		RenderSystem.depthMask(true);
		RenderSystem.disableDepthTest();
		RenderSystem.enableTexture();
		RenderSystem.disableBlend();
	}

	public void renderMeterNames(PoseStack poses, MultiBufferSource.BufferSource bufferSource) {
		MeterNameMode mode = Options.RedstoneMultimeter.RENDER_METER_NAMES.get();

		if (mode == MeterNameMode.ALWAYS
			|| (mode == MeterNameMode.WHEN_PREVIEWING && client.isPreviewing())
			|| (mode == MeterNameMode.IN_FOCUS_MODE && client.getHud().isFocusMode() && !client.isPreviewing())) {
			renderMeters(poses, bufferSource, this::renderMeterName);
		}
	}

	private void renderMeters(PoseStack poses, MultiBufferSource.BufferSource bufferSource, MeterPartRenderer renderer) {
		if (client.isPreviewing() || !client.getHud().isFocusMode()) {
			ClientMeterGroup meterGroup = client.isPreviewing() ? client.getMeterGroupPreview() : client.getMeterGroup();

			for (Meter meter : meterGroup.getMeters()) {
				if (meter.isIn(minecraft.level)) {
					renderer.render(poses, bufferSource, meter);
				}
			}
		} else {
			Meter focussed = client.getHud().getFocussedMeter();

			if (focussed != null) {
				if (focussed.isIn(minecraft.level)) {
					renderer.render(poses, bufferSource, focussed);
				}
			}
		}
	}

	private void renderMeterHighlight(PoseStack poses, MultiBufferSource.BufferSource bufferSource, Meter meter) {
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

		poses.pushPose();
		poses.translate(dx, dy, dz);

		Matrix4f pose = poses.last().pose();

		float r = ColorUtils.getRed(color) / 255.0F;
		float g = ColorUtils.getGreen(color) / 255.0F;
		float b = ColorUtils.getBlue(color) / 255.0F;

		renderMeterHighlight(bufferBuilder, tesselator, pose, r, g, b, 0.5F);

		if (movable) {
			renderMeterOutline(bufferBuilder, tesselator, pose, r, g, b, 1.0F);
		}

		poses.popPose();
	}

	private void renderMeterName(PoseStack poses, MultiBufferSource.BufferSource bufferSource, Meter meter) {
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
			poses.pushPose();
			poses.translate(dx + 0.5D, dy + 0.75D, dz + 0.5D);
			poses.mulPose(camera.rotation());
			poses.scale(-0.025F, -0.025F, 0.025F);

			Matrix4f pose = poses.last().pose();

			float x = -(minecraft.font.width(name) / 2.0F);
			float y = 0;

			minecraft.font.drawInBatch(name, x, y, 0xFFFFFFFF, false, pose, bufferSource, true, 0, LightTexture.pack(15, 15));

			poses.popPose();
		}
	}

	private void renderMeterHighlight(BufferBuilder bufferBuilder, Tesselator tessellator, Matrix4f pose, float r, float g, float b, float a) {
		bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
		drawBox(bufferBuilder, pose, r, g, b, a, false);
		tessellator.end();
	}

	private void renderMeterOutline(BufferBuilder bufferBuilder, Tesselator tessellator, Matrix4f pose, float r, float g, float b, float a) {
		bufferBuilder.begin(VertexFormat.Mode.DEBUG_LINES, DefaultVertexFormat.POSITION_COLOR);
		drawBox(bufferBuilder, pose, r, g, b, a, true);
		tessellator.end();
	}

	private void drawBox(BufferBuilder bufferBuilder, Matrix4f pose, float r, float g, float b, float a, boolean outline) {
		// The box is slightly larger than 1x1 to prevent z-fighting
		float c0 = -0.002F;
		float c1 = 1.002F;

		// West face
		bufferBuilder.vertex(pose, c0, c0, c0).color(r, g, b, a).endVertex();
		bufferBuilder.vertex(pose, c0, c0, c1).color(r, g, b, a).endVertex();
		bufferBuilder.vertex(pose, c0, c1, c1).color(r, g, b, a).endVertex();
		bufferBuilder.vertex(pose, c0, c1, c0).color(r, g, b, a).endVertex();
		if (outline) {
			bufferBuilder.vertex(pose, c0, c0, c0).color(r, g, b, a).endVertex();
		}

		// East face
		bufferBuilder.vertex(pose, c1, c0, c0).color(r, g, b, a).endVertex();
		bufferBuilder.vertex(pose, c1, c1, c0).color(r, g, b, a).endVertex();
		bufferBuilder.vertex(pose, c1, c1, c1).color(r, g, b, a).endVertex();
		bufferBuilder.vertex(pose, c1, c0, c1).color(r, g, b, a).endVertex();
		if (outline) {
			bufferBuilder.vertex(pose, c1, c0, c0).color(r, g, b, a).endVertex();
		}

		// North face
		bufferBuilder.vertex(pose, c0, c0, c0).color(r, g, b, a).endVertex();
		bufferBuilder.vertex(pose, c0, c1, c0).color(r, g, b, a).endVertex();
		bufferBuilder.vertex(pose, c1, c1, c0).color(r, g, b, a).endVertex();
		bufferBuilder.vertex(pose, c1, c0, c0).color(r, g, b, a).endVertex();
		if (outline) {
			bufferBuilder.vertex(pose, c0, c0, c0).color(r, g, b, a).endVertex();
		}

		// South face
		bufferBuilder.vertex(pose, c0, c0, c1).color(r, g, b, a).endVertex();
		bufferBuilder.vertex(pose, c1, c0, c1).color(r, g, b, a).endVertex();
		bufferBuilder.vertex(pose, c1, c1, c1).color(r, g, b, a).endVertex();
		bufferBuilder.vertex(pose, c0, c1, c1).color(r, g, b, a).endVertex();
		if (outline) {
			bufferBuilder.vertex(pose, c0, c0, c1).color(r, g, b, a).endVertex();
		}

		// Bottom face
		bufferBuilder.vertex(pose, c0, c0, c0).color(r, g, b, a).endVertex();
		bufferBuilder.vertex(pose, c1, c0, c0).color(r, g, b, a).endVertex();
		bufferBuilder.vertex(pose, c1, c0, c1).color(r, g, b, a).endVertex();
		bufferBuilder.vertex(pose, c0, c0, c1).color(r, g, b, a).endVertex();
		if (outline) {
			bufferBuilder.vertex(pose, c0, c0, c0).color(r, g, b, a).endVertex();
		}

		// Top face
		bufferBuilder.vertex(pose, c0, c1, c0).color(r, g, b, a).endVertex();
		bufferBuilder.vertex(pose, c0, c1, c1).color(r, g, b, a).endVertex();
		bufferBuilder.vertex(pose, c1, c1, c1).color(r, g, b, a).endVertex();
		bufferBuilder.vertex(pose, c1, c1, c0).color(r, g, b, a).endVertex();
		if (outline) {
			bufferBuilder.vertex(pose, c0, c1, c0).color(r, g, b, a).endVertex();
		}
	}

	@FunctionalInterface
	private interface MeterPartRenderer {

		void render(PoseStack poses, MultiBufferSource.BufferSource bufferSource, Meter meter);

	}
}
