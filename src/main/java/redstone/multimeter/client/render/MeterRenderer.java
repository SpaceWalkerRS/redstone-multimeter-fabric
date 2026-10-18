package redstone.multimeter.client.render;

import java.util.function.Predicate;

import org.joml.Matrix4f;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font.DisplayMode;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import redstone.multimeter.client.MultimeterClient;
import redstone.multimeter.client.meter.ClientMeterGroup;
import redstone.multimeter.client.option.Options;
import redstone.multimeter.common.meter.Meter;
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

	public void renderMeterHighlights(PoseStack poses, SubmitNodeCollector collector) {
		renderMeters(poses, collector, this::shouldRenderHighlight, this::renderHighlight);
	}

	public void renderMeterNameTags(PoseStack poses, SubmitNodeCollector collector) {
		renderMeters(poses, collector, this::shouldRenderNameTag, this::renderNameTag);
	}

	private void renderMeters(PoseStack poses, SubmitNodeCollector collector, Predicate<Meter> predicate, MeterPartRenderer renderer) {
		ClientMeterGroup meterGroup = client.isPreviewing() ? client.getMeterGroupPreview() : client.getMeterGroup();

		for (Meter meter : meterGroup.getMeters()) {
			if (meter.isIn(minecraft.level) && predicate.test(meter)) {
				renderer.render(poses, collector, meter);
			}
		}
	}

	private boolean shouldRenderHighlight(Meter meter) {
		return switch (Options.RedstoneMultimeter.RENDER_METERS.get()) {
			case ALWAYS        -> true;
			case IN_FOCUS      -> client.isPreviewing() || !client.getHud().isFocusMode() || client.getHud().getFocussedMeter() == meter;
			case IN_FOCUS_MODE -> !client.isPreviewing() && client.getHud().isFocusMode();
			case NEVER         -> false;
			default -> throw new IllegalStateException("unknown meter highlight mode " + Options.RedstoneMultimeter.RENDER_METERS.getAsString());
		};
	}

	private void renderHighlight(PoseStack poses, SubmitNodeCollector collector, Meter meter) {
		BlockPos pos = meter.getPos().getBlockPos();
		int color = meter.getColor();
		boolean movable = meter.isMovable();

		Camera camera = minecraft.gameRenderer.mainCamera();
		Vec3 cameraPos = minecraft.gameRenderer.gameRenderState().levelRenderState.cameraRenderState.pos;

		double dx = pos.getX() - cameraPos.x;
		double dy = pos.getY() - cameraPos.y;
		double dz = pos.getZ() - cameraPos.z;

		int range = Options.RedstoneMultimeter.METER_RANGE.get();
		double rangeSquared = range * range;

		if (range < 0 || Mth.lengthSquared(dx, dy, dz) < rangeSquared) {
			int dim = getHighlightDimmingFactor(meter);

			poses.pushPose();
			poses.translate(dx, dy, dz);

			renderMeterHighlight(collector, poses, ColorUtils.setAlpha(color, 0x7F / dim));

			if (movable) {
				renderMeterOutline(collector, poses, ColorUtils.setAlpha(color, 0xFF / dim));
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
			case IN_FOCUS        -> !client.isPreviewing() && client.getHud().isFocusMode() && client.getHud().getFocussedMeter() == meter;
			case WHEN_PREVIEWING -> client.isPreviewing();
			case NEVER           -> false;
			default -> throw new IllegalStateException("unknown meter name tag mode " + Options.RedstoneMultimeter.RENDER_METER_NAMES.getAsString());
		};
	}

	private void renderNameTag(PoseStack poses, SubmitNodeCollector collector, Meter meter) {
		Component name = Component.literal(meter.getName());
		BlockPos pos = meter.getPos().getBlockPos();

		Camera camera = minecraft.gameRenderer.mainCamera();
		Vec3 cameraPos = camera.position();

		double dx = pos.getX() - cameraPos.x;
		double dy = pos.getY() - cameraPos.y;
		double dz = pos.getZ() - cameraPos.z;

		int range = Options.RedstoneMultimeter.METER_NAME_RANGE.get();
		double rangeSquared = range * range;

		if (Mth.lengthSquared(dx, dy, dz) < rangeSquared) {
			int dim = getNameTagDimmingFactor(meter);

			poses.pushPose();
			poses.translate(dx + 0.5D, dy + 0.75D, dz + 0.5D);
			poses.mulPose(camera.rotation().get(new Matrix4f()));
			poses.scale(0.025F, -0.025F, 0.025F);

			float x = -(minecraft.font.width(name) / 2.0F);
			float y = 0;

			collector.submitText(poses, x, y, name.getVisualOrderText(), false, DisplayMode.SEE_THROUGH, LightCoordsUtil.pack(15, 15), ColorUtils.setAlpha(0xFFFFFF, 0xFF / dim), 0, 0);

			poses.popPose();
		}
	}

	private int getNameTagDimmingFactor(Meter meter) {
		return shouldDimMeter(meter) ? 2 : 1;
	}

	private boolean shouldDimMeter(Meter meter) {
		return !client.isPreviewing() && client.getHud().isFocusMode() && client.getHud().getFocussedMeter() != meter;
	}

	private void renderMeterHighlight(SubmitNodeCollector collector, PoseStack poses, int color) {
		collector.submitCustomGeometry(poses, RenderTypes.debugQuads(), (pose, buffer) -> {
			float r = ColorUtils.getRed(color) / (float) 0xFF;
			float g = ColorUtils.getGreen(color) / (float) 0xFF;
			float b = ColorUtils.getBlue(color) / (float) 0xFF;
			float a = ColorUtils.getAlpha(color) / (float) 0xFF;

			drawBox(buffer, pose, r, g, b, a);
		});
	}

	private void renderMeterOutline(SubmitNodeCollector collector, PoseStack poses, int color) {
		collector.submitShapeOutline(poses, OUTLINE_SHAPE, RenderTypes.lines(), color, 2.0F, true);
	}

	private void drawBox(VertexConsumer buffer, PoseStack.Pose pose, float r, float g, float b, float a) {
		// The box is slightly larger than 1x1 to prevent z-fighting
		float c0 = -0.002F;
		float c1 = 1.002F;

		// West face
		buffer.addVertex(pose, c0, c0, c0).setColor(r, g, b, a);
		buffer.addVertex(pose, c0, c0, c1).setColor(r, g, b, a);
		buffer.addVertex(pose, c0, c1, c1).setColor(r, g, b, a);
		buffer.addVertex(pose, c0, c1, c0).setColor(r, g, b, a);

		// East face
		buffer.addVertex(pose, c1, c0, c0).setColor(r, g, b, a);
		buffer.addVertex(pose, c1, c1, c0).setColor(r, g, b, a);
		buffer.addVertex(pose, c1, c1, c1).setColor(r, g, b, a);
		buffer.addVertex(pose, c1, c0, c1).setColor(r, g, b, a);

		// North face
		buffer.addVertex(pose, c0, c0, c0).setColor(r, g, b, a);
		buffer.addVertex(pose, c0, c1, c0).setColor(r, g, b, a);
		buffer.addVertex(pose, c1, c1, c0).setColor(r, g, b, a);
		buffer.addVertex(pose, c1, c0, c0).setColor(r, g, b, a);

		// South face
		buffer.addVertex(pose, c0, c0, c1).setColor(r, g, b, a);
		buffer.addVertex(pose, c1, c0, c1).setColor(r, g, b, a);
		buffer.addVertex(pose, c1, c1, c1).setColor(r, g, b, a);
		buffer.addVertex(pose, c0, c1, c1).setColor(r, g, b, a);

		// Bottom face
		buffer.addVertex(pose, c0, c0, c0).setColor(r, g, b, a);
		buffer.addVertex(pose, c1, c0, c0).setColor(r, g, b, a);
		buffer.addVertex(pose, c1, c0, c1).setColor(r, g, b, a);
		buffer.addVertex(pose, c0, c0, c1).setColor(r, g, b, a);

		// Top face
		buffer.addVertex(pose, c0, c1, c0).setColor(r, g, b, a);
		buffer.addVertex(pose, c0, c1, c1).setColor(r, g, b, a);
		buffer.addVertex(pose, c1, c1, c1).setColor(r, g, b, a);
		buffer.addVertex(pose, c1, c1, c0).setColor(r, g, b, a);
	}

	@FunctionalInterface
	private interface MeterPartRenderer {

		void render(PoseStack poses, SubmitNodeCollector collector, Meter meter);

	}
}
