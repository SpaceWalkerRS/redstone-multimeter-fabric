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
public class ChunkMixin {
	
	@Shadow @Final private World world;
	
	@Inject(
			method = "getBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;)Lnet/minecraft/block/BlockState;",
			locals = LocalCapture.CAPTURE_FAILHARD,
			at = @At(
					value = "INVOKE",
					shift = Shift.AFTER,
					target = "Lnet/minecraft/world/chunk/ChunkSection;setBlockState(IIILnet/minecraft/block/BlockState;)V"
			)
	)
	private void onBlockStateChanged(BlockPos pos, BlockState newState, CallbackInfoReturnable<BlockState> cir, int sectionX, int y, int sectionZ, int idk, int prevHeight, BlockState oldState) {
		if (!world.isClient) {
			((IServerWorld)world).getMultimeter().onBlockChange(world, pos, oldState, newState);
		}
	}
}
