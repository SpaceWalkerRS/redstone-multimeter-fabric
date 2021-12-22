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
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
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
	
	@Shadow public abstract int method_3777(int x, int y, int z);
	
	@Inject(
			method = "method_3723",
			locals = LocalCapture.CAPTURE_FAILHARD,
			at = @At(
					value = "INVOKE",
					shift = Shift.BEFORE,
					target = "Lnet/minecraft/block/Block;method_408(Lnet/minecraft/world/World;IIILnet/minecraft/block/Block;)V"
			)
	)
	private void onBlockUpdate(int x, int y, int z, Block fromBlock, CallbackInfo ci, Block block) {
		if (isClient) {
			return;
		}
		
		MultimeterServer server = ((IServerWorld)this).getMultimeterServer();
		Multimeter multimeter = server.getMultimeter();
		
		multimeter.logBlockUpdate((World)(Object)this, x, y, z);
		
		// 'powered' changes for most meterable blocks are handled in those classes
		// to reduce expensive calls to 
		// World.isReceivingRedstonePower and World.getReceivedRedstonePower
		if (((IBlock)block).logPoweredOnBlockUpdate()) {
			multimeter.logPowered((World)(Object)this, x, y, z, block, method_3777(x, y, z));
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
					target = "Lnet/minecraft/block/entity/BlockEntity;method_545()V"
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
					value = "RETURN"
			)
	)
	private void endTickTaskBlockEntitiesAndEntities(CallbackInfo ci) {
		endTickTask();
		endTickTask();
	}
	
	@Inject(
			method = "method_3636",
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
			method = "method_4725",
			locals = LocalCapture.CAPTURE_FAILHARD,
			at = @At(
					value = "INVOKE",
					shift = Shift.BEFORE,
					target = "Lnet/minecraft/block/Block;method_408(Lnet/minecraft/world/World;IIILnet/minecraft/block/Block;)V"
			)
	)
	private void onComparatorUpdate(int fromX, int y, int fromZ, Block fromBlock, CallbackInfo ci, int dir, int x, int z, Block block) {
		if (!isClient) {
			((IServerWorld)this).getMultimeter().logComparatorUpdate((World)(Object)this, x, y, z);
		}
	}
}
