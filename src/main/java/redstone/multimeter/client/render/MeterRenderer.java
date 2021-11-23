package redstone.multimeter.client.render;

import org.lwjgl.opengl.GL11;

import net.minecraft.class_1015;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import redstone.multimeter.client.MultimeterClient;
import redstone.multimeter.common.meter.Meter;
import redstone.multimeter.util.ColorUtils;

public class MeterRenderer {
	
	private final MultimeterClient multimeterClient;
	private final MinecraftClient minecraftClient;
	
	public MeterRenderer(MultimeterClient multimeterClient) {
		this.multimeterClient = multimeterClient;
		this.minecraftClient = this.multimeterClient.getMinecraftClient();
	}
	
	public void renderMeters() {
		class_1015.method_4454();
		class_1015.method_4343(class_1015.class_1033.field_5138, class_1015.class_1027.field_5088, class_1015.class_1033.field_5140, class_1015.class_1027.field_5084);
		class_1015.method_4407();
		class_1015.method_4413(false);
		
		for (Meter meter : multimeterClient.getMeterGroup().getMeters()) {
			if (meter.isIn(minecraftClient.world)) {
				drawMeter(meter);
			}
		}
		
		class_1015.method_4413(true);
		class_1015.method_4397();
		class_1015.method_4439();
	}
	
	private void drawMeter(Meter meter) {
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder builder = tessellator.getBuffer();
		
		BlockPos pos = meter.getPos().getBlockPos();
		int color = meter.getColor();
		boolean movable = meter.isMovable();
		
		Entity camera = minecraftClient.getCameraEntity();
		Vec3d cameraPos = camera.getPosVector();
		
		class_1015.method_4461();
		class_1015.method_4412(pos.getX() - cameraPos.x, pos.getY() - cameraPos.y, pos.getZ() - cameraPos.z);
		
		float r = ColorUtils.getRed(color) / 255.0F;
		float g = ColorUtils.getGreen(color) / 255.0F;
		float b = ColorUtils.getBlue(color) / 255.0F;
		
		drawFilledBox(builder, tessellator, r, g, b, 0.5F);
		
		if (movable) {
			drawBoxOutline(builder, tessellator, r, g, b, 1.0F);
		}
		
		class_1015.method_4350();
	}
	
	private void drawFilledBox(BufferBuilder builder, Tessellator tessellator, float r, float g, float b, float a) {
		builder.begin(GL11.GL_QUADS, VertexFormats.POSITION_COLOR);
		drawBox(builder, r, g, b, a, false);
		tessellator.draw();
	}
	
	private void drawBoxOutline(BufferBuilder builder, Tessellator tessellator, float r, float g, float b, float a) {
		builder.begin(GL11.GL_LINES, VertexFormats.POSITION_COLOR);
		drawBox(builder, r, g, b, a, true);
		tessellator.draw();
	}
	
	private void drawBox(BufferBuilder builder, float r, float g, float b, float a, boolean outline) {
		// The box is slightly larger than 1x1 to prevent z-fighting
		float c0 = -0.002F;
		float c1 = 1.002F;
		
		// West face
		builder.vertex(c0, c0, c0).color(r, g, b, a).next();
		builder.vertex(c0, c0, c1).color(r, g, b, a).next();
		builder.vertex(c0, c1, c1).color(r, g, b, a).next();
		builder.vertex(c0, c1, c0).color(r, g, b, a).next();
		if (outline) {
			builder.vertex(c0, c0, c0).color(r, g, b, a).next();
		}
		
		// East face
		builder.vertex(c1, c0, c0).color(r, g, b, a).next();
		builder.vertex(c1, c1, c0).color(r, g, b, a).next();
		builder.vertex(c1, c1, c1).color(r, g, b, a).next();
		builder.vertex(c1, c0, c1).color(r, g, b, a).next();
		if (outline) {
			builder.vertex(c1, c0, c0).color(r, g, b, a).next();
		}
		
		// North face
		builder.vertex(c0, c0, c0).color(r, g, b, a).next();
		builder.vertex(c0, c1, c0).color(r, g, b, a).next();
		builder.vertex(c1, c1, c0).color(r, g, b, a).next();
		builder.vertex(c1, c0, c0).color(r, g, b, a).next();
		if (outline) {
			builder.vertex(c0, c0, c0).color(r, g, b, a).next();
		}
		
		// South face
		builder.vertex(c0, c0, c1).color(r, g, b, a).next();
		builder.vertex(c1, c0, c1).color(r, g, b, a).next();
		builder.vertex(c1, c1, c1).color(r, g, b, a).next();
		builder.vertex(c0, c1, c1).color(r, g, b, a).next();
		if (outline) {
			builder.vertex(c0, c0, c1).color(r, g, b, a).next();
		}
		
		// Bottom face
		builder.vertex(c0, c0, c0).color(r, g, b, a).next();
		builder.vertex(c1, c0, c0).color(r, g, b, a).next();
		builder.vertex(c1, c0, c1).color(r, g, b, a).next();
		builder.vertex(c0, c0, c1).color(r, g, b, a).next();
		if (outline) {
			builder.vertex(c0, c0, c0).color(r, g, b, a).next();
		}
		
		// Top face
		builder.vertex(c0, c1, c0).color(r, g, b, a).next();
		builder.vertex(c0, c1, c1).color(r, g, b, a).next();
		builder.vertex(c1, c1, c1).color(r, g, b, a).next();
		builder.vertex(c1, c1, c0).color(r, g, b, a).next();
		if (outline) {
			builder.vertex(c0, c1, c0).color(r, g, b, a).next();
		}
	}
}
