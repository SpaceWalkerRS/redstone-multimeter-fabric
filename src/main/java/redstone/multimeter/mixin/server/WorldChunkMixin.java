package redstone.multimeter.mixin.server;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

import redstone.multimeter.interfaces.mixin.IServerWorld;

@Mixin(Chunk.class)
public class WorldChunkMixin {
	
	@Shadow @Final private World world;
	
	@Inject(
			method = "method_16994",
			locals = LocalCapture.CAPTURE_FAILHARD,
			at = @At(
					value = "INVOKE",
					shift = Shift.BEFORE,
					target = "Lnet/minecraft/block/BlockState;method_16874(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;Z)V"
			)
	)
	private void onBlockStateChanged(BlockPos pos, BlockState newState, boolean moved, CallbackInfoReturnable<BlockState> cir, int sectionX, int y, int sectionZ, int prevHeight, BlockState oldState) {
		if (!world.method_16390()) {
			((IServerWorld)world).getMultimeter().onBlockChange(world, pos, oldState, newState);
		}
	}
}
