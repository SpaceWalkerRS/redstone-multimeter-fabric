package redstone.multimeter.client.render;

import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;

import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderType.CompositeState;

public class RenderTypes extends RenderStateShard {

	private RenderTypes() {
		super(null, null, null);
	}

	private static final RenderType DEBUG_QUADS = RenderType.create(
		"debug_quads",
		DefaultVertexFormat.POSITION_COLOR,
		GL11.GL_QUADS,
		131072,
		CompositeState.builder()
			.setShadeModelState(SMOOTH_SHADE)
			.setTransparencyState(TRANSLUCENT_TRANSPARENCY)
			.setCullState(NO_CULL)
			.createCompositeState(false)
	);

	public static RenderType debugQuads() {
		return DEBUG_QUADS;
	}
}
