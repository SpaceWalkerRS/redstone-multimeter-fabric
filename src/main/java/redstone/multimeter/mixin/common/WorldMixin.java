package redstone.multimeter.mixin.common;

import java.util.Iterator;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import redstone.multimeter.common.TickTask;
import redstone.multimeter.interfaces.mixin.IBlock;
import redstone.multimeter.interfaces.mixin.IWorldServer;
import redstone.multimeter.interfaces.mixin.TickTaskExecutor;
import redstone.multimeter.server.Multimeter;
import redstone.multimeter.server.MultimeterServer;

@Mixin(World.class)
public abstract class WorldMixin implements TickTaskExecutor {
	
	@Shadow @Final private boolean isRemote;
	
	@Inject(
			method = "neighborChanged",
			locals = LocalCapture.CAPTURE_FAILHARD,
			at = @At(
					value = "INVOKE",
					shift = Shift.BEFORE,
					target = "Lnet/minecraft/block/state/IBlockState;neighborChanged(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/Block;Lnet/minecraft/util/math/BlockPos;)V"
			)
	)
	private void onBlockUpdate(BlockPos pos, Block fromBlock, BlockPos fromPos, CallbackInfo ci, IBlockState state) {
		if (isRemote) {
			return;
		}
		
		MultimeterServer server = ((IWorldServer)this).getMultimeterServer();
		Multimeter multimeter = server.getMultimeter();
		
		multimeter.logBlockUpdate((World)(Object)this, pos);
		
		// 'powered' changes for most meterable blocks are handled in those classes
		// to reduce expensive calls to
		// World.isBlockPowered and World.getRedstonePowerFromNeighbors
		if (((IBlock)state.getBlock()).logPoweredOnBlockUpdateRSMM()) {
			multimeter.logPowered((World)(Object)this, pos, state);
		}
	}
	
	@Inject(
			method = "observedNeighborChanged",
			at = @At(
					value = "INVOKE",
					shift = Shift.BEFORE,
					target = "Lnet/minecraft/block/Block;observedNeighborChange(Lnet/minecraft/block/state/IBlockState;Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/Block;Lnet/minecraft/util/math/BlockPos;)V"
			)
	)
	private void onObserverUpdate(BlockPos pos, Block fromBlock, BlockPos fromPos, CallbackInfo ci) {
		if (!isRemote) {
			((IWorldServer)this).getMultimeter().logObserverUpdate((World)(Object)this, pos);
		}
	}
	
	@Inject(
			method = "updateEntities",
			at = @At(
					value = "INVOKE_STRING",
					target = "Lnet/minecraft/profiler/Profiler;startSection(Ljava/lang/String;)V",
					args = "ldc=global"
			)
	)
	private void startTickTaskGlobalEntities(CallbackInfo ci) {
		startTickTaskRSMM(TickTask.GLOBAL_ENTITIES);
	}
	
	@Inject(
			method = "updateEntities",
			locals = LocalCapture.CAPTURE_FAILHARD,
			at = @At(
					value = "INVOKE",
					shift = Shift.BEFORE,
					target = "Lnet/minecraft/entity/Entity;onUpdate()V"
			)
	)
	private void onGlobalEntityTick(CallbackInfo ci, int index, Entity entity) {
		if (!isRemote) {
			((IWorldServer)this).getMultimeter().logEntityTick((World)(Object)this, entity);
		}
	}
	
	@Inject(
			method = "updateEntities",
			at = @At(
					value = "INVOKE_STRING",
					target = "Lnet/minecraft/profiler/Profiler;endStartSection(Ljava/lang/String;)V",
					args = "ldc=regular"
			)
	)
	private void swapTickTaskRegularEntities(CallbackInfo ci) {
		swapTickTaskRSMM(TickTask.REGULAR_ENTITIES);
	}
	
	@Inject(
			method = "updateEntities",
			at = @At(
					value = "INVOKE_STRING",
					target = "Lnet/minecraft/profiler/Profiler;endStartSection(Ljava/lang/String;)V",
					args = "ldc=blockEntities"
			)
	)
	private void swapTickTaskBlockEntities(CallbackInfo ci) {
		swapTickTaskRSMM(TickTask.BLOCK_ENTITIES);
	}
	
	@Inject(
			method = "updateEntities",
			locals = LocalCapture.CAPTURE_FAILHARD,
			at = @At(
					value = "INVOKE",
					shift = Shift.BEFORE,
					target = "Lnet/minecraft/util/ITickable;update()V"
			)
	)
	private void onBlockEntityTick(CallbackInfo ci, Iterator<TileEntity> it, TileEntity blockEntity) {
		if (!isRemote) {
			((IWorldServer)this).getMultimeter().logBlockEntityTick((World)(Object)this, blockEntity);
		}
	}
	
	@Inject(
			method = "updateEntities",
			at = @At(
					value = "RETURN"
			)
	)
	private void endTickTaskBlockEntities(CallbackInfo ci) {
		endTickTaskRSMM();
	}
	
	@Inject(
			method = "updateEntityWithOptionalForce",
			at = @At(
					value = "INVOKE",
					shift = Shift.BEFORE,
					target = "Lnet/minecraft/entity/Entity;onUpdate()V"
			)
	)
	private void onTickEntity(Entity entity, boolean force, CallbackInfo ci) {
		if (!isRemote) {
			((IWorldServer)this).getMultimeter().logEntityTick((World)(Object)this, entity);
		}
	}
	
	@Inject(
			method = "tick",
			at = @At(
					value = "INVOKE",
					shift = Shift.BEFORE,
					target = "Lnet/minecraft/world/World;updateWeather()V"
			)
	)
	private void startTickTaskWeather(CallbackInfo ci) {
		startTickTaskRSMM(TickTask.WEATHER);
	}
	
	@Inject(
			method = "tick",
			at = @At(
					value = "INVOKE",
					shift = Shift.AFTER,
					target = "Lnet/minecraft/world/World;updateWeather()V"
			)
	)
	private void endTickTaskWeather(CallbackInfo ci) {
		endTickTaskRSMM();
	}
	
	@Inject(
			method = "updateComparatorOutputLevel",
			locals = LocalCapture.CAPTURE_FAILHARD,
			at = @At(
					value = "INVOKE",
					shift = Shift.BEFORE,
					target = "Lnet/minecraft/block/Block;onNeighborChange(Lnet/minecraft/world/IBlockAccess;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/math/BlockPos;)V"
			)
	)
	private void onComparatorUpdate(BlockPos fromPos, Block fromBlock, CallbackInfo ci, EnumFacing[] dirs, int i, int j, EnumFacing dir, BlockPos pos) {
		if (!isRemote) {
			((IWorldServer)this).getMultimeter().logComparatorUpdate((World)(Object)this, pos);
		}
	}
}
