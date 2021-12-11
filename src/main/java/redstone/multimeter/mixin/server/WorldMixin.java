package redstone.multimeter.mixin.server;

import java.util.Iterator;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import redstone.multimeter.common.TickTask;
import redstone.multimeter.interfaces.mixin.IBlock;
import redstone.multimeter.interfaces.mixin.IServerWorld;
import redstone.multimeter.interfaces.mixin.IWorld;
import redstone.multimeter.server.Multimeter;
import redstone.multimeter.server.MultimeterServer;

@Mixin(World.class)
public abstract class WorldMixin implements IWorld {
	
	@Shadow private boolean isClient;
	
	@Inject(
			method = "neighbourUpdate",
			locals = LocalCapture.CAPTURE_FAILHARD,
			at = @At(
					value = "INVOKE",
					shift = Shift.BEFORE,
					target = "Lnet/minecraft/block/Block;neighborUpdate(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;Lnet/minecraft/block/Block;)V"
			)
	)
	private void onBlockUpdate(BlockPos pos, Block fromBlock, CallbackInfo ci, BlockState state) {
		if (isClient) {
			return;
		}
		
		MultimeterServer server = ((IServerWorld)this).getMultimeterServer();
		Multimeter multimeter = server.getMultimeter();
		
		multimeter.logBlockUpdate((World)(Object)this, pos);
		
		// 'powered' changes for most meterable blocks are handled in those classes
		// to reduce expensive calls to 
		// World.isReceivingRedstonePower and World.getReceivedRedstonePower
		if (((IBlock)state.getBlock()).logPoweredOnBlockUpdate()) {
			multimeter.logPowered((World)(Object)this, pos, state);
		}
	}
	
	@Inject(
			method = "tickEntities",
			at = @At(
					value = "HEAD"
			)
	)
	private void startTickTaskEntities(CallbackInfo ci) {
		startTickTask(TickTask.ENTITIES);
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
		startTickTask(TickTask.GLOBAL_ENTITIES);
	}
	
	@Inject(
			method = "tickEntities",
			locals = LocalCapture.CAPTURE_FAILHARD,
			at = @At(
					value = "INVOKE",
					shift = Shift.BEFORE,
					target = "Lnet/minecraft/entity/Entity;tick()V"
			)
	)
	private void onGlobalEntityTick(CallbackInfo ci, int index, Entity entity) {
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
		swapTickTask(TickTask.REGULAR_ENTITIES);
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
		swapTickTask(TickTask.BLOCK_ENTITIES);
	}
	
	@Inject(
			method = "tickEntities",
			locals = LocalCapture.CAPTURE_FAILHARD,
			at = @At(
					value = "INVOKE",
					shift = Shift.BEFORE,
					target = "Lnet/minecraft/util/Tickable;tick()V"
			)
	)
	private void onBlockEntityTick(CallbackInfo ci, Iterator<BlockEntity> it, BlockEntity blockEntity) {
		if (!isClient) {
			((IServerWorld)this).getMultimeter().logBlockEntityTick((World)(Object)this, blockEntity);
		}
	}
	
	@Inject(
			method = "tickEntities",
			at = @At(
					value = "HEAD"
			)
	)
	private void endTickTaskBlockEntitiesAndEntities(CallbackInfo ci) {
		endTickTask();
		endTickTask();
	}
	
	@Inject(
			method = "method_335",
			at = @At(
					value = "INVOKE",
					shift = Shift.BEFORE,
					target = "Lnet/minecraft/entity/Entity;tick()V"
			)
	)
	private void onEntityTick(Entity entity, boolean bl, CallbackInfo ci) {
		if (!isClient) {
			((IServerWorld)this).getMultimeter().logEntityTick((World)(Object)this, entity);
		}
	}
	
	@Inject(
			method = "updateHorizontalAdjacent",
			locals = LocalCapture.CAPTURE_FAILHARD,
			at = @At(
					value = "INVOKE",
					shift = Shift.BEFORE,
					target = "Lnet/minecraft/block/Block;neighborUpdate(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;Lnet/minecraft/block/Block;)V"
			)
	)
	private void onComparatorUpdate(BlockPos fromPos, Block fromBlock, CallbackInfo ci, Iterator<Direction> it, Direction dir, BlockPos pos) {
		if (!isClient) {
			((IServerWorld)this).getMultimeter().logComparatorUpdate((World)(Object)this, pos);
		}
	}
}
