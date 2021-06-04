package rsmm.fabric.mixin.server;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import rsmm.fabric.common.TickPhase;
import rsmm.fabric.interfaces.mixin.IBlock;
import rsmm.fabric.interfaces.mixin.IServerWorld;
import rsmm.fabric.server.Multimeter;
import rsmm.fabric.server.MultimeterServer;

@Mixin(World.class)
public abstract class WorldMixin {
	
	@Shadow public abstract boolean isClient();
	@Shadow public abstract boolean isReceivingRedstonePower(BlockPos pos);
	
	@Inject(
			method = "tickBlockEntities",
			at = @At(
					value = "HEAD"
			)
	)
	private void onTickBlockEntitiesInjectAtHead(CallbackInfo ci) {
		if (!isClient()) {
			((IServerWorld)this).getMultimeterServer().getMultimeter().onTickPhase(TickPhase.TICK_BLOCK_ENTITIES);
		}
	}
	
	@Inject(
			method = "updateNeighbor",
			locals = LocalCapture.CAPTURE_FAILHARD,
			at = @At(
					value = "INVOKE",
					shift = Shift.BEFORE,
					target = "Lnet/minecraft/block/BlockState;neighborUpdate(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/Block;Lnet/minecraft/util/math/BlockPos;Z)V"
			)
	)
	private void onUpdateNeighborInjectBeforeNeighborUpdate(BlockPos pos, Block fromBlock, BlockPos fromPos, CallbackInfo ci, BlockState state) {
		if (isClient()) {
			return;
		}
		
		Block block = state.getBlock();
		
		// Block updates for most meterable blocks are handled in those classes
		// to reduce expensive calls to 
		// World.isReceivingRedstonePower and World.getReceivedRedstonePower
		if (((IBlock)block).standardIsPowered()) {
			boolean powered = ((IBlock)block).isPowered((World)(Object)this, pos, state);
			
			MultimeterServer server = ((IServerWorld)this).getMultimeterServer();
			Multimeter multimeter = server.getMultimeter();
			
			multimeter.logPowered((World)(Object)this, pos, powered);
		}
	}
}
