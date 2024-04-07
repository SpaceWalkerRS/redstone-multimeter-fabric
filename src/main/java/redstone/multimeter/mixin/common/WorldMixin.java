package redstone.multimeter.mixin.common;

import java.util.Iterator;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.state.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.dimension.Dimension;

import redstone.multimeter.common.TickTask;
import redstone.multimeter.interfaces.mixin.IBlock;
import redstone.multimeter.interfaces.mixin.IServerWorld;
import redstone.multimeter.interfaces.mixin.TickTaskExecutor;
import redstone.multimeter.server.Multimeter;
import redstone.multimeter.server.MultimeterServer;
import redstone.multimeter.util.DimensionUtils;

@Mixin(World.class)
public class WorldMixin implements TickTaskExecutor {

	@Shadow @Final private Dimension dimension;
	@Shadow @Final private boolean isClient;


	@Inject(
		method = "tickEntities",
		at = @At(
			value = "INVOKE_STRING",
			target = "Lnet/minecraft/util/profiler/Profiler;push(Ljava/lang/String;)V",
			args = "ldc=entities"
		)
	)
	private void startTickTaskEntities(CallbackInfo ci) {
		rsmm$startTickTask(TickTask.ENTITIES, DimensionUtils.getKey(dimension));
	}

	@Inject(
		method = "tickEntities",
		at = @At(
			value = "INVOKE_STRING",
			target = "Lnet/minecraft/util/profiler/Profiler;push(Ljava/lang/String;)V",
			args = "ldc=global"
		)
	)
	private void startTickTaskGlobalEntities(CallbackInfo ci) {
		rsmm$startTickTask(TickTask.GLOBAL_ENTITIES);
	}

	@Inject(
		method = "tickEntities",
		locals = LocalCapture.CAPTURE_FAILHARD,
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/entity/Entity;tick()V"
		)
	)
	private void logGlobalEntityTick(CallbackInfo ci, int index, Entity entity) {
		if (!isClient) {
			((IServerWorld)this).getMultimeter().logEntityTick((World)(Object)this, entity);
		}
	}

	@Inject(
		method = "tickEntities",
		at = @At(
			value = "INVOKE_STRING",
			target = "Lnet/minecraft/util/profiler/Profiler;swap(Ljava/lang/String;)V",
			args = "ldc=regular"
		)
	)
	private void swapTickTaskRegularEntities(CallbackInfo ci) {
		rsmm$swapTickTask(TickTask.REGULAR_ENTITIES);
	}

	@Inject(
		method = "tickEntities",
		at = @At(
			value = "INVOKE_STRING",
			target = "Lnet/minecraft/util/profiler/Profiler;swap(Ljava/lang/String;)V",
			args = "ldc=blockEntities"
		)
	)
	private void swapTickTaskBlockEntities(CallbackInfo ci) {
		rsmm$swapTickTask(TickTask.BLOCK_ENTITIES);
	}

	@Inject(
		method = "tickEntities",
		locals = LocalCapture.CAPTURE_FAILHARD,
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/util/Tickable;tick()V"
		)
	)
	private void logBlockEntityTick(CallbackInfo ci, Iterator<BlockEntity> it, BlockEntity blockEntity) {
		if (!isClient) {
			((IServerWorld)this).getMultimeter().logBlockEntityTick((World)(Object)this, blockEntity);
		}
	}

	@Inject(
		method = "tickEntities",
		at = @At(
			value = "TAIL"
		)
	)
	private void endTickTaskBlockEntitiesAndEntities(CallbackInfo ci) {
		rsmm$endTickTask();
		rsmm$endTickTask();
	}

	@Inject(
		method = "updateEntity(Lnet/minecraft/entity/Entity;Z)V",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/entity/Entity;tick()V"
		)
	)
	private void logEntityTick(Entity entity, boolean requireLoaded, CallbackInfo ci) {
		if (!isClient) {
			((IServerWorld)this).getMultimeter().logEntityTick((World)(Object)this, entity);
		}
	}

	@Inject(
		method = "neighborChanged",
		locals = LocalCapture.CAPTURE_FAILHARD,
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/block/state/BlockState;neighborChanged(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/Block;Lnet/minecraft/util/math/BlockPos;)V"
		)
	)
	private void logBlockUpdate(BlockPos pos, Block neighborBlock, BlockPos neighborPos, CallbackInfo ci, BlockState state) {
		if (!isClient) {
			MultimeterServer server = ((IServerWorld)this).getMultimeterServer();
			Multimeter multimeter = server.getMultimeter();

			multimeter.logBlockUpdate((World)(Object)this, pos);

			// 'powered' changes for most meterable blocks are handled in those classes
			// to reduce expensive calls to
			// World.hasNeighborSignal and World.getNeighborSignal
			if (((IBlock)state.getBlock()).rsmm$logPoweredOnBlockUpdate()) {
				multimeter.logPowered((World)(Object)this, pos, state);
			}
		}
	}

	@Inject(
		method = "neighborStateChanged",
		locals = LocalCapture.CAPTURE_FAILHARD,
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/block/ObserverBlock;neighborStateChanged(Lnet/minecraft/block/state/BlockState;Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/Block;Lnet/minecraft/util/math/BlockPos;)V"
		)
	)
	private void logObserverUpdate(BlockPos pos, Block neighborBlock, BlockPos neighborPos, CallbackInfo ci, BlockState state) {
		if (!isClient) {
			((IServerWorld)this).getMultimeterServer().getMultimeter().logObserverUpdate((World)(Object)this, pos);
		}
	}

	@Inject(
		method = "updateNeighborComparators",
		locals = LocalCapture.CAPTURE_FAILHARD,
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/block/state/BlockState;neighborChanged(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/Block;Lnet/minecraft/util/math/BlockPos;)V"
		)
	)
	private void logComparatorUpdate(BlockPos neighborPos, Block neighborBlock, CallbackInfo ci, Iterator<Direction> it, Direction dir, BlockPos pos) {
		if (!isClient) {
			((IServerWorld)this).getMultimeterServer().getMultimeter().logComparatorUpdate((World)(Object)this, pos);
		}
	}
}
