package redstone.multimeter.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.world.phys.shapes.VoxelShape;

@Mixin(LevelRenderer.class)
public interface LevelRendererAccessor {

	@Invoker("renderShape")
	public static void rsmm$renderShape(PoseStack poses, VertexConsumer buffer, VoxelShape shape, double dx, double dy, double dz, float r, float g, float b, float a) {
		throw new UnsupportedOperationException();
	}
}
