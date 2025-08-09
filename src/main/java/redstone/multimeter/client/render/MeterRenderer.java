package redstone.multimeter.client.render;

import java.util.function.Predicate;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import com.mojang.math.Matrix4f;

import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource.BufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import redstone.multimeter.client.MultimeterClient;
import redstone.multimeter.client.meter.ClientMeterGroup;
import redstone.multimeter.client.option.Options;
import redstone.multimeter.common.meter.Meter;
import redstone.multimeter.mixin.client.LevelRendererAccessor;
import redstone.multimeter.util.ColorUtils;

public class MeterRenderer {

	// The box is slightly larger than 1x1 to prevent z-fighting
	private static final VoxelShape OUTLINE_SHAPE = Shapes.box(-0.002F, -0.002F, -0.002F, 1.002F, 1.002F, 1.002F);

	private final MultimeterClient client;
	private final Minecraft minecraft;

	public MeterRenderer(MultimeterClient client) {
		this.client = client;
		this.minecraft = this.client.getMinecraft();
	}

	public void renderMeterHighlights(PoseStack poses, BufferSource bufferSource) {
		renderMeters(poses, bufferSource, this::shouldRenderHighlight, this::renderHighlight);
	}

	public void renderMeterNameTags(PoseStack poses, BufferSource bufferSource) {
		renderMeters(poses, bufferSource, this::shouldRenderNameTag, this::renderNameTag);
	}

	private void renderMeters(PoseStack poses, BufferSource bufferSource, Predicate<Meter> predicate, MeterPartRenderer renderer) {
		ClientMeterGroup meterGroup = client.isPreviewing() ? client.getMeterGroupPreview() : client.getMeterGroup();

		for (Meter meter : meterGroup.getMeters()) {
			if (meter.isIn(minecraft.level) && predicate.test(meter)) {
				renderer.render(poses, bufferSource, meter);
			}
		}
	}

	private boolean shouldRenderHighlight(Meter meter) {
		return switch (Options.RedstoneMultimeter.RENDER_METERS.get()) {
			case ALWAYS        -> true;
			case IN_FOCUS      -> !client.isPreviewing() && client.getHud().isFocusMode() && client.getHud().getFocussedMeter() == meter;
			case IN_FOCUS_MODE -> !client.isPreviewing() && client.getHud().isFocusMode();
			case NEVER         -> false;
			default -> throw new IllegalStateException("unknown meter highlight mode " + Options.RedstoneMultimeter.RENDER_METERS.getAsString());
		};
	}

	private void renderHighlight(PoseStack poses, BufferSource bufferSource, Meter meter) {
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
			int dim = getHighlightDimmingFactor(meter);

			float r = ColorUtils.getRed(color) / (float) 0xFF;
			float g = ColorUtils.getGreen(color) / (float) 0xFF;
			float b = ColorUtils.getBlue(color) / (float) 0xFF;

			poses.pushPose();
			poses.translate(dx, dy, dz);

			renderMeterHighlight(bufferSource, poses, r, g, b, 0.5F / dim);

			if (movable) {
				renderMeterOutline(bufferSource, poses, r, g, b, 1.0F / dim);
			}

			poses.popPose();
		}
	}

	private int getHighlightDimmingFactor(Meter meter) {
		return shouldDimMeter(meter) ? 3 : 1;
	}

	private boolean shouldRenderNameTag(Meter meter) {
		return switch (Options.RedstoneMultimeter.RENDER_METER_NAMES.get()) {
			case ALWAYS          -> true;
			case IN_FOCUS_MODE   -> !client.isPreviewing() && client.getHud().isFocusMode();
			case WHEN_PREVIEWING -> client.isPreviewing();
			case NEVER           -> false;
			default -> throw new IllegalStateException("unknown meter name tag mode " + Options.RedstoneMultimeter.RENDER_METER_NAMES.getAsString());
		};
	}

	private void renderNameTag(PoseStack poses, BufferSource bufferSource, Meter meter) {
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

			poses.pushPose();
			poses.translate(dx + 0.5D, dy + 0.75D, dz + 0.5D);
			poses.mulPose(camera.rotation());
			poses.scale(-0.025F, -0.025F, 0.025F);

			Matrix4f pose = poses.last().pose();

			float x = -(minecraft.font.width(name) / 2.0F);
			float y = 0;

			minecraft.font.drawInBatch(name, x, y, ColorUtils.setAlpha(0xFFFFFF, 0xFF / dim), false, pose, bufferSource, true, 0, LightTexture.pack(15, 15));

			poses.popPose();
		}
	}

	private int getNameTagDimmingFactor(Meter meter) {
		return shouldDimMeter(meter) ? 2 : 1;
	}

	private boolean shouldDimMeter(Meter meter) {
		return !client.isPreviewing() && client.getHud().isFocusMode() && client.getHud().getFocussedMeter() != meter;
	}

	private void renderMeterHighlight(BufferSource bufferSource, PoseStack poses, float r, float g, float b, float a) {
		VertexConsumer buffer = bufferSource.getBuffer(RenderTypes.debugQuads());
		Matrix4f pose = poses.last().pose();

		drawBox(buffer, pose, r, g, b, a);
	}

	private void renderMeterOutline(BufferSource bufferSource, PoseStack poses, float r, float g, float b, float a) {
		VertexConsumer buffer = bufferSource.getBuffer(RenderType.lines());
		LevelRendererAccessor.rsmm$renderShape(poses, buffer, OUTLINE_SHAPE, 0.0D, 0.0D, 0.0D, r, g, b, a);
	}

	private void drawBox(VertexConsumer buffer, Matrix4f pose, float r, float g, float b, float a) {
		// The box is slightly larger than 1x1 to prevent z-fighting
		float c0 = -0.002F;
		float c1 = 1.002F;

		// West face
		buffer.vertex(pose, c0, c0, c0).color(r, g, b, a).endVertex();
		buffer.vertex(pose, c0, c0, c1).color(r, g, b, a).endVertex();
		buffer.vertex(pose, c0, c1, c1).color(r, g, b, a).endVertex();
		buffer.vertex(pose, c0, c1, c0).color(r, g, b, a).endVertex();

		// East face
		buffer.vertex(pose, c1, c0, c0).color(r, g, b, a).endVertex();
		buffer.vertex(pose, c1, c1, c0).color(r, g, b, a).endVertex();
		buffer.vertex(pose, c1, c1, c1).color(r, g, b, a).endVertex();
		buffer.vertex(pose, c1, c0, c1).color(r, g, b, a).endVertex();

		// North face
		buffer.vertex(pose, c0, c0, c0).color(r, g, b, a).endVertex();
		buffer.vertex(pose, c0, c1, c0).color(r, g, b, a).endVertex();
		buffer.vertex(pose, c1, c1, c0).color(r, g, b, a).endVertex();
		buffer.vertex(pose, c1, c0, c0).color(r, g, b, a).endVertex();

		// South face
		buffer.vertex(pose, c0, c0, c1).color(r, g, b, a).endVertex();
		buffer.vertex(pose, c1, c0, c1).color(r, g, b, a).endVertex();
		buffer.vertex(pose, c1, c1, c1).color(r, g, b, a).endVertex();
		buffer.vertex(pose, c0, c1, c1).color(r, g, b, a).endVertex();

		// Bottom face
		buffer.vertex(pose, c0, c0, c0).color(r, g, b, a).endVertex();
		buffer.vertex(pose, c1, c0, c0).color(r, g, b, a).endVertex();
		buffer.vertex(pose, c1, c0, c1).color(r, g, b, a).endVertex();
		buffer.vertex(pose, c0, c0, c1).color(r, g, b, a).endVertex();

		// Top face
		buffer.vertex(pose, c0, c1, c0).color(r, g, b, a).endVertex();
		buffer.vertex(pose, c0, c1, c1).color(r, g, b, a).endVertex();
		buffer.vertex(pose, c1, c1, c1).color(r, g, b, a).endVertex();
		buffer.vertex(pose, c1, c1, c0).color(r, g, b, a).endVertex();
	}

	@FunctionalInterface
	private interface MeterPartRenderer {

		void render(PoseStack poses, BufferSource bufferSource, Meter meter);

	}
}
