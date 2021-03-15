package rsmm.fabric.client;

import java.util.Map.Entry;

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
import rsmm.fabric.common.MeterGroup;

public class MeterRenderer {
	
	private final MultimeterClient multimeterClient;
	private final MinecraftClient minecraftClient;
	
	public MeterRenderer(MultimeterClient multimeterClient) {
		this.multimeterClient = multimeterClient;
		this.minecraftClient = this.multimeterClient.getMinecraftClient();
	}
	
	public void renderMeters(MatrixStack matrices) {
		MeterGroup meterGroup = multimeterClient.getMeterGroup();
		
		if (meterGroup == null) {
			return;
		}
		
		RenderSystem.disableTexture();
		RenderSystem.enableDepthTest();
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		RenderSystem.enableCull();
		
		matrices.push();
		
		Camera camera = minecraftClient.gameRenderer.getCamera();
		Vec3d cameraPos = camera.getPos();
		
		matrices.translate(-cameraPos.x, -cameraPos.y, -cameraPos.z);
		
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder builder = tessellator.getBuffer();
		
		builder.begin(GL11.GL_QUADS, VertexFormats.POSITION_COLOR);
		
		for (Entry<BlockPos, Meter> entry : meterGroup.getMeters().entrySet()) {
			BlockPos pos = entry.getKey();
			Meter meter = entry.getValue();
			
			int color = meter.getColor();
			
			float r = (color >> 16) / 255.0F;
			float g = (color >> 8) / 255.0F;
			float b = color / 255.0F;
			float a = 0.5F;
			
			drawBox(matrices, builder, pos, r, g, b, a);
		}
		
		tessellator.draw();
		
		matrices.pop();
		
		RenderSystem.disableBlend();
		RenderSystem.enableDepthTest();
		RenderSystem.enableTexture();
	}
	
	private void drawBox(MatrixStack matrices, BufferBuilder builder, BlockPos pos, float r, float g, float b, float a) {
		Matrix4f model = matrices.peek().getModel();

		float x0 = pos.getX();
		float y0 = pos.getY();
		float z0 = pos.getZ();

		float x1 = pos.getX() + 1.0F;
		float y1 = pos.getY() + 1.0F;
		float z1 = pos.getZ() + 1.0F;

		// Back Face
		builder.vertex(model, x0, y0, z0).color(r, g, b, a).next();
		builder.vertex(model, x0, y0, z1).color(r, g, b, a).next();
		builder.vertex(model, x0, y1, z1).color(r, g, b, a).next();
		builder.vertex(model, x0, y1, z0).color(r, g, b, a).next();

		// Front Face
		builder.vertex(model, x1, y0, z0).color(r, g, b, a).next();
		builder.vertex(model, x1, y1, z0).color(r, g, b, a).next();
		builder.vertex(model, x1, y1, z1).color(r, g, b, a).next();
		builder.vertex(model, x1, y0, z1).color(r, g, b, a).next();

		// Right Face
		builder.vertex(model, x0, y0, z0).color(r, g, b, a).next();
		builder.vertex(model, x0, y1, z0).color(r, g, b, a).next();
		builder.vertex(model, x1, y1, z0).color(r, g, b, a).next();
		builder.vertex(model, x1, y0, z0).color(r, g, b, a).next();

		// Left Face
		builder.vertex(model, x0, y0, z1).color(r, g, b, a).next();
		builder.vertex(model, x1, y0, z1).color(r, g, b, a).next();
		builder.vertex(model, x1, y1, z1).color(r, g, b, a).next();
		builder.vertex(model, x0, y1, z1).color(r, g, b, a).next();

		// Bottom Face
		builder.vertex(model, x0, y0, z0).color(r, g, b, a).next();
		builder.vertex(model, x1, y0, z0).color(r, g, b, a).next();
		builder.vertex(model, x1, y0, z1).color(r, g, b, a).next();
		builder.vertex(model, x0, y0, z1).color(r, g, b, a).next();

		// Top Face
		builder.vertex(model, x0, y1, z0).color(r, g, b, a).next();
		builder.vertex(model, x0, y1, z1).color(r, g, b, a).next();
		builder.vertex(model, x1, y1, z1).color(r, g, b, a).next();
		builder.vertex(model, x1, y1, z0).color(r, g, b, a).next();
	}
}
