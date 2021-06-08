package rsmm.fabric.mixin.server;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.WorldChunk;

import rsmm.fabric.block.Meterable;
import rsmm.fabric.block.PowerSource;
import rsmm.fabric.interfaces.mixin.IBlock;
import rsmm.fabric.interfaces.mixin.IServerWorld;
import rsmm.fabric.server.Multimeter;
import rsmm.fabric.server.MultimeterServer;

@Mixin(WorldChunk.class)
public class WorldChunkMixin {
	
	@Shadow @Final private World world;
	
	@Inject(
			method = "setBlockState",
			locals = LocalCapture.CAPTURE_FAILHARD,
			at = @At(
					value = "INVOKE",
					shift = Shift.BEFORE,
					target = "Lnet/minecraft/block/BlockState;onBlockRemoved(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;Z)V"
			)
	)
	private void onSetBlockStateInjectBeforeStateReplaced(BlockPos pos, BlockState newState, boolean moved, CallbackInfoReturnable<BlockState> cir, int chunkX, int y, int chunkZ, ChunkSection chunkSection, boolean isEmpty, BlockState oldState, Block newBlock, Block oldBlock) {
		if (world.isClient()) {
			return;
		}
		
		MultimeterServer server = ((IServerWorld)world).getMultimeterServer();
		Multimeter multimeter = server.getMultimeter();
		
		if (oldBlock == newBlock) {
			if (((IBlock)newBlock).isPowerSource() && ((PowerSource)newBlock).standardLogPowerChange()) {
				int oldPower = ((PowerSource)oldBlock).getPowerLevel(world, pos, oldState);
				int newPower = ((PowerSource)newBlock).getPowerLevel(world, pos, newState);
				
				multimeter.logPowerChange(world, pos, oldPower, newPower);
			}
		} else {
			multimeter.blockChanged(world, pos, oldBlock, newBlock);
		}
		
		boolean prevMeterable = ((IBlock)oldBlock).isMeterable();
		boolean newMeterable = ((IBlock)newBlock).isMeterable();
		
		if (prevMeterable || newMeterable) {
			boolean active = newMeterable && ((Meterable)newBlock).isActive(world, pos, newState);
			multimeter.logActive(world, pos, active);
		}
	}
}
