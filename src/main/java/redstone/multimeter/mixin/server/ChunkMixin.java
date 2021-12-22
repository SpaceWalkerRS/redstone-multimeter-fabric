package redstone.multimeter.mixin.server;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.block.Block;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkSection;

import redstone.multimeter.interfaces.mixin.IServerWorld;

@Mixin(Chunk.class)
public abstract class ChunkMixin {
	
	@Shadow @Final private World world;
	@Shadow @Final public int chunkX;
	@Shadow @Final public int chunkZ;
	
	@Shadow public abstract Block getBlockAtPos(int sectionX, int y, int sectionZ);
	@Shadow public abstract int getBlockData(int sectionX, int y, int sectionZ);
	
	@Inject(
			method = "method_3881",
			locals = LocalCapture.CAPTURE_FAILHARD,
			at = @At(
					value = "INVOKE",
					shift = Shift.BEFORE,
					target = "Lnet/minecraft/block/Block;method_411(Lnet/minecraft/world/World;IIILnet/minecraft/block/Block;I)V"
			)
	)
	private void onSetBlockWithMeta(int sectionX, int y, int sectionZ, Block newBlock, int newMetadata, CallbackInfoReturnable<Boolean> ci, int idk, int idk2, Block oldBlock, int oldMetadata, ChunkSection section, int idk3, int x, int z) {
		if (!world.isClient) {
			((IServerWorld)world).getMultimeter().onBlockChange(world, x, y, z, oldBlock, oldMetadata, newBlock, newMetadata);
		}
	}
	
	@Inject(
			method = "method_3900",
			locals = LocalCapture.CAPTURE_FAILHARD,
			at = @At(
					value = "INVOKE",
					shift = Shift.BEFORE,
					target = "Lnet/minecraft/world/chunk/ChunkSection;method_3932(IIII)V"
			)
	)
	private void onSetBlockMeta(int sectionX, int y, int sectionZ, int newMetadata, CallbackInfoReturnable<Boolean> ci, ChunkSection section) {
		if (!world.isClient) {
			int x = 16 * chunkX + sectionX;
			int z = 16 * chunkZ + sectionZ;
			Block block = getBlockAtPos(sectionX, y, sectionZ);
			int oldMetadata = section.getBlockData(sectionX, y & 0xF, sectionZ);
			
			((IServerWorld)world).getMultimeter().onBlockChange(world, x, y, z, block, oldMetadata, block, newMetadata);
		}
	}
}
