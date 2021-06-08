package rsmm.fabric.client;

import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Vec3d;

import rsmm.fabric.common.Meter;
import rsmm.fabric.util.ColorUtils;

public class MeterRenderer {
	
	private final MultimeterClient multimeterClient;
	private final MinecraftClient minecraftClient;
	
	public MeterRenderer(MultimeterClient multimeterClient) {
		this.multimeterClient = multimeterClient;
		this.minecraftClient = this.multimeterClient.getMinecraftClient();
	}
	
	public void renderMeters(MatrixStack matrices) {
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		RenderSystem.disableTexture();
		RenderSystem.depthMask(false);
		RenderSystem.disableLighting();
		
		for (Meter meter : multimeterClient.getMeterGroup().getMeters()) {
			if (meter.isIn(minecraftClient.world)) {
				drawMeter(matrices, meter);
			}
		}
		
		RenderSystem.enableLighting();
		RenderSystem.depthMask(true);
		RenderSystem.enableTexture();
		RenderSystem.disableBlend();
	}
	
	private void drawMeter(MatrixStack matrices, Meter meter) {
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder builder = tessellator.getBuffer();
		
		BlockPos pos = meter.getPos().asBlockPos();
		int color = meter.getColor();
		boolean movable = meter.isMovable();
		
		Camera camera = minecraftClient.gameRenderer.getCamera();
		Vec3d cameraPos = camera.getPos();
		
		matrices.push();
		matrices.translate(pos.getX() - cameraPos.x, pos.getY() - cameraPos.y, pos.getZ() - cameraPos.z);
		
		Matrix4f model = matrices.peek().getModel();
		
		float r = ColorUtils.getRed(color) / 255.0F;
		float g = ColorUtils.getGreen(color) / 255.0F;
		float b = ColorUtils.getBlue(color) / 255.0F;
		
		drawFilledBox(builder, tessellator, model, r, g, b, 0.5F);
		
		if (movable) {
			drawBoxOutline(builder, tessellator, model, r, g, b, 1.0F);
		}
		
		matrices.pop();
	}
	
	private void drawFilledBox(BufferBuilder builder, Tessellator tessellator, Matrix4f model, float r, float g, float b, float a) {
		builder.begin(GL11.GL_QUADS, VertexFormats.POSITION_COLOR);
		drawBox(builder, model, r, g, b, a, false);
		tessellator.draw();
	}
	
	private void drawBoxOutline(BufferBuilder builder, Tessellator tessellator, Matrix4f model, float r, float g, float b, float a) {
		builder.begin(GL11.GL_LINES, VertexFormats.POSITION_COLOR);
		drawBox(builder, model, r, g, b, a, true);
		tessellator.draw();
	}
	
	private void drawBox(BufferBuilder builder, Matrix4f model, float r, float g, float b, float a, boolean outline) {
		// The box is slightly larger than 1x1 to prevent z-fighting
		float c0 = -0.002F;
		float c1 = 1.002F;
		
		// Back Face
		builder.vertex(model, c0, c0, c0).color(r, g, b, a).next();
		builder.vertex(model, c0, c0, c1).color(r, g, b, a).next();
		builder.vertex(model, c0, c1, c1).color(r, g, b, a).next();
		builder.vertex(model, c0, c1, c0).color(r, g, b, a).next();
		if (outline) {
			builder.vertex(model, c0, c0, c0).color(r, g, b, a).next();
		}
		
		// Front Face
		builder.vertex(model, c1, c0, c0).color(r, g, b, a).next();
		builder.vertex(model, c1, c1, c0).color(r, g, b, a).next();
		builder.vertex(model, c1, c1, c1).color(r, g, b, a).next();
		builder.vertex(model, c1, c0, c1).color(r, g, b, a).next();
		if (outline) {
			builder.vertex(model, c1, c0, c0).color(r, g, b, a).next();
		}
		
		// Right Face
		builder.vertex(model, c0, c0, c0).color(r, g, b, a).next();
		builder.vertex(model, c0, c1, c0).color(r, g, b, a).next();
		builder.vertex(model, c1, c1, c0).color(r, g, b, a).next();
		builder.vertex(model, c1, c0, c0).color(r, g, b, a).next();
		if (outline) {
			builder.vertex(model, c0, c0, c0).color(r, g, b, a).next();
		}
		
		// Left Face
		builder.vertex(model, c0, c0, c1).color(r, g, b, a).next();
		builder.vertex(model, c1, c0, c1).color(r, g, b, a).next();
		builder.vertex(model, c1, c1, c1).color(r, g, b, a).next();
		builder.vertex(model, c0, c1, c1).color(r, g, b, a).next();
		if (outline) {
			builder.vertex(model, c0, c0, c1).color(r, g, b, a).next();
		}
		
		// Bottom Face
		builder.vertex(model, c0, c0, c0).color(r, g, b, a).next();
		builder.vertex(model, c1, c0, c0).color(r, g, b, a).next();
		builder.vertex(model, c1, c0, c1).color(r, g, b, a).next();
		builder.vertex(model, c0, c0, c1).color(r, g, b, a).next();
		if (outline) {
			builder.vertex(model, c0, c0, c0).color(r, g, b, a).next();
		}
		
		// Top Face
		builder.vertex(model, c0, c1, c0).color(r, g, b, a).next();
		builder.vertex(model, c0, c1, c1).color(r, g, b, a).next();
		builder.vertex(model, c1, c1, c1).color(r, g, b, a).next();
		builder.vertex(model, c1, c1, c0).color(r, g, b, a).next();
		if (outline) {
			builder.vertex(model, c0, c1, c0).color(r, g, b, a).next();
		}
	}
}
