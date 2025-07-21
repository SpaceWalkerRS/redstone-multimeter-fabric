package redstone.multimeter.client.render;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;

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
		VertexFormat.Mode.QUADS,
		131072,
		CompositeState.builder()
			.setShaderState(POSITION_COLOR_SHADER)
			.setTransparencyState(TRANSLUCENT_TRANSPARENCY)
			.setCullState(NO_CULL)
			.createCompositeState(false)
	);

	public static RenderType debugQuads() {
		return DEBUG_QUADS;
	}
}
