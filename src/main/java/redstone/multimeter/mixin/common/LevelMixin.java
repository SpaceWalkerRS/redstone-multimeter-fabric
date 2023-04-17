package redstone.multimeter.mixin.common;

import java.util.Iterator;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import redstone.multimeter.common.TickTask;
import redstone.multimeter.interfaces.mixin.IBlock;
import redstone.multimeter.interfaces.mixin.IServerLevel;
import redstone.multimeter.interfaces.mixin.TickTaskExecutor;
import redstone.multimeter.server.Multimeter;
import redstone.multimeter.server.MultimeterServer;

@Mixin(Level.class)
public class LevelMixin implements TickTaskExecutor {

	@Shadow public boolean isClientSide() { return false; }

	@Inject(
		method = "tickBlockEntities",
		at = @At(
			value = "HEAD"
		)
	)
	private void startTickTaskBlockEntities(CallbackInfo ci) {
		rsmm$startTickTask(TickTask.BLOCK_ENTITIES);
	}

	@Inject(
		method = "tickBlockEntities",
		at = @At(
			value = "TAIL"
		)
	)
	private void endTickTaskBlockEntities(CallbackInfo ci) {
		rsmm$endTickTask();
	}

	@Inject(
		method = "neighborChanged",
		locals = LocalCapture.CAPTURE_FAILHARD,
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/level/block/state/BlockState;neighborChanged(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/Block;Lnet/minecraft/core/BlockPos;Z)V"
		)
	)
	private void logBlockUpdate(BlockPos pos, Block neighborBlock, BlockPos neighborPos, CallbackInfo ci, BlockState state) {
		if (!isClientSide()) {
			MultimeterServer server = ((IServerLevel)this).getMultimeterServer();
			Multimeter multimeter = server.getMultimeter();

			multimeter.logBlockUpdate((Level)(Object)this, pos);

			// 'powered' changes for most meterable blocks are handled in those classes
			// to reduce expensive calls to
			// Level.hasNeighborSignal and Level.getNeighborSignal
			if (((IBlock)state.getBlock()).rsmm$logPoweredOnBlockUpdate()) {
				multimeter.logPowered((Level)(Object)this, pos, state);
			}
		}
	}

	@Inject(
		method = "updateNeighbourForOutputSignal",
		locals = LocalCapture.CAPTURE_FAILHARD,
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/level/block/state/BlockState;neighborChanged(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/Block;Lnet/minecraft/core/BlockPos;Z)V"
		)
	)
	private void logComparatorUpdate(BlockPos neighborPos, Block neighborBlock, CallbackInfo ci, Iterator<Direction> it, Direction dir, BlockPos pos) {
		if (!isClientSide()) {
			((IServerLevel)this).getMultimeterServer().getMultimeter().logComparatorUpdate((Level)(Object)this, pos);
		}
	}
}
