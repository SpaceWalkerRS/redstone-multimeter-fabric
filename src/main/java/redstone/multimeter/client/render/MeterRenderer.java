package redstone.multimeter.client.render;

import org.joml.Matrix4f;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;

import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;

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

	public void renderMeters(PoseStack poses) {
		RenderSystem.setShader(() -> GameRenderer.getPositionColorShader());
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		RenderSystem.disableTexture();
		RenderSystem.depthMask(false);

		for (Meter meter : client.getMeterGroup().getMeters()) {
			if (meter.isIn(minecraft.level)) {
				drawMeter(poses, meter);
			}
		}

		RenderSystem.depthMask(true);
		RenderSystem.enableTexture();
		RenderSystem.disableBlend();
	}

	private void drawMeter(PoseStack poses, Meter meter) {
		Tesselator tessellator = Tesselator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBuilder();

		BlockPos pos = meter.getPos().getBlockPos();
		int color = meter.getColor();
		boolean movable = meter.isMovable();

		Camera camera = minecraft.gameRenderer.getMainCamera();
		Vec3 cameraPos = camera.getPosition();

		poses.pushPose();
		poses.translate(pos.getX() - cameraPos.x, pos.getY() - cameraPos.y, pos.getZ() - cameraPos.z);

		Matrix4f pose = poses.last().pose();

		float r = ColorUtils.getRed(color) / 255.0F;
		float g = ColorUtils.getGreen(color) / 255.0F;
		float b = ColorUtils.getBlue(color) / 255.0F;

		drawFilledBox(bufferBuilder, tessellator, pose, r, g, b, 0.5F);

		if (movable) {
			drawBoxOutline(bufferBuilder, tessellator, pose, r, g, b, 1.0F);
		}

		poses.popPose();
	}

	private void drawFilledBox(BufferBuilder bufferBuilder, Tesselator tessellator, Matrix4f pose, float r, float g, float b, float a) {
		bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
		drawBox(bufferBuilder, pose, r, g, b, a, false);
		tessellator.end();
	}

	private void drawBoxOutline(BufferBuilder bufferBuilder, Tesselator tessellator, Matrix4f pose, float r, float g, float b, float a) {
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
}
