package redstone.multimeter.mixin.common;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.world.World;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.chunk.WorldChunkSection;

import redstone.multimeter.interfaces.mixin.IServerWorld;

@Mixin(WorldChunk.class)
public class WorldChunkMixin {

	@Shadow @Final private World world;
	@Shadow @Final public int chunkX;
	@Shadow @Final public int chunkZ;

	@Inject(
		method = "setBlockWithMetadataAt",
		locals = LocalCapture.CAPTURE_FAILHARD,
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/block/Block;onRemoved(Lnet/minecraft/world/World;IIIII)V"
		)
	)
	private void logBlockChange(int localX, int y, int localZ, int block, int metadata, CallbackInfoReturnable<Boolean> cir, int index, int oldHeight, int oldBlock, int oldMetadata, WorldChunkSection section, int heightIncreased /* the fuck? */, int x, int z) {
		((IServerWorld)world).getMultimeter().onBlockChange(world, x, y, z, oldBlock, oldMetadata, block, oldMetadata /* metadata has not changed yet! */);
	}

	@Inject(
		method = "setBlockWithMetadataAt",
		locals = LocalCapture.CAPTURE_FAILHARD,
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/block/Block;onAdded(Lnet/minecraft/world/World;III)V"
		)
	)
	private void logBlockMetadataChange(int localX, int y, int localZ, int block, int metadata, CallbackInfoReturnable<Boolean> cir, int index, int oldHeight, int oldBlock, int oldMetadata, WorldChunkSection section, int heightIncreased /* the fuck? */, int x, int z) {
		((IServerWorld)world).getMultimeter().onBlockChange(world, x, y, z, block /* block change was already handled */, oldMetadata, block, metadata);
	}

	@Inject(
		method = "setBlockMetadataAt",
		locals = LocalCapture.CAPTURE_FAILHARD,
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/chunk/WorldChunkSection;setBlockMetadata(IIII)V"
		)
	)
	private void logBlockMetadataChange(int localX, int y, int localZ, int metadata, CallbackInfoReturnable<Boolean> cir, WorldChunkSection section) {
		if (!world.isMultiplayer) {
			int x = (chunkX << 4) + localX;
			int z = (chunkZ << 4) + localZ;
			int block = section.getBlock(localX, y & 15, localZ);
			int oldMetadata = section.getBlockMetadata(localX, y & 15, localZ);

			((IServerWorld)world).getMultimeter().onBlockChange(world, x, y, z, block, oldMetadata, block, metadata);
		}
	}
}
