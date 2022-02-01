package redstone.multimeter.mixin.common;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

import redstone.multimeter.interfaces.mixin.IWorldServer;

@Mixin(Chunk.class)
public class ChunkMixin {
	
	@Shadow @Final private World world;
	
	@Inject(
			method = "setBlockState",
			locals = LocalCapture.CAPTURE_FAILHARD,
			at = @At(
					value = "INVOKE",
					shift = Shift.AFTER,
					target = "Lnet/minecraft/world/chunk/storage/ExtendedBlockStorage;set(IIILnet/minecraft/block/state/IBlockState;)V"
			)
	)
	private void onBlockStateChanged(BlockPos pos, IBlockState newState, CallbackInfoReturnable<IBlockState> cir, int sectionX, int y, int sectionZ, int heightmapIndex, int prevHeight, IBlockState oldState) {
		if (!world.isRemote) {
			((IWorldServer)world).getMultimeter().onBlockChange(world, pos, oldState, newState);
		}
	}
}
