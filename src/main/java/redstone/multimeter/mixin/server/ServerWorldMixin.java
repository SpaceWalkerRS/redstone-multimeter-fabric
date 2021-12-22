package redstone.multimeter.mixin.server;

import java.util.function.BooleanSupplier;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.class_3793;
import net.minecraft.class_4023;
import net.minecraft.class_4070;
import net.minecraft.block.Block;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.BlockAction;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.TickableEntry;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.world.SaveHandler;
import net.minecraft.world.World;
import net.minecraft.world.dimension.Dimension;
import net.minecraft.world.level.LevelProperties;

import redstone.multimeter.common.TickTask;
import redstone.multimeter.interfaces.mixin.IMinecraftServer;
import redstone.multimeter.interfaces.mixin.IServerWorld;
import redstone.multimeter.server.MultimeterServer;

@Mixin(ServerWorld.class)
public abstract class ServerWorldMixin extends World implements IServerWorld {
	
	@Shadow @Final private MinecraftServer server;
	
	protected ServerWorldMixin(SaveHandler saveHandler, class_4070 arg, LevelProperties levelProperties, Dimension dimension, Profiler profiler, boolean bl) {
		super(saveHandler, arg, levelProperties, dimension, profiler, bl);
	}
	
	@Inject(
			method = "method_16327",
			at = @At(
					value = "INVOKE_STRING",
					target = "Lnet/minecraft/util/profiler/Profiler;push(Ljava/lang/String;)V",
					args = "ldc=spawner"
			)
	)
	private void startTickTaskMobSpawning(BooleanSupplier isAheadOfTime, CallbackInfo ci) {
		startTickTask(TickTask.MOB_SPAWNING);
	}
	
	@Inject(
			method = "method_16327",
			at = @At(
					value = "INVOKE_STRING",
					target = "Lnet/minecraft/util/profiler/Profiler;swap(Ljava/lang/String;)V",
					args = "ldc=chunkSource"
			)
	)
	private void swapTickTaskChunkSource(BooleanSupplier isAheadOfTime, CallbackInfo ci) {
		swapTickTask(TickTask.CHUNK_SOURCE);
	}
	
	@Inject(
			method = "method_16327",
			at = @At(
					value = "INVOKE",
					shift = Shift.AFTER,
					target = "Lnet/minecraft/world/level/LevelProperties;setTime(J)V"
			)
	)
	private void onTickTime(BooleanSupplier isAheadOfTime, CallbackInfo ci) {
		if (dimension.method_11789() == class_3793.field_18954) {
			getMultimeterServer().onOverworldTickTime();
		}
	}
	
	@Inject(
			method = "method_16327",
			at = @At(
					value = "INVOKE_STRING",
					target = "Lnet/minecraft/util/profiler/Profiler;swap(Ljava/lang/String;)V",
					args = "ldc=tickPending"
			)
	)
	private void swapTickTaskScheduledTicks(BooleanSupplier isAheadOfTime, CallbackInfo ci) {
		swapTickTask(TickTask.SCHEDULED_TICKS);
	}
	
	@Inject(
			method = "method_16327",
			at = @At(
					value = "INVOKE_STRING",
					target = "Lnet/minecraft/util/profiler/Profiler;swap(Ljava/lang/String;)V",
					args = "ldc=tickBlocks"
			)
	)
	private void swapTickTaskTickChunks(BooleanSupplier isAheadOfTime, CallbackInfo ci) {
		swapTickTask(TickTask.TICK_CHUNKS);
	}
	
	@Inject(
			method = "method_16327",
			at = @At(
					value = "INVOKE_STRING",
					target = "Lnet/minecraft/util/profiler/Profiler;swap(Ljava/lang/String;)V",
					args = "ldc=chunkMap"
			)
	)
	private void swapTickTaskChunkMap(BooleanSupplier isAheadOfTime, CallbackInfo ci) {
		swapTickTask(TickTask.CHUNK_MAP);
	}
	
	@Inject(
			method = "method_16327",
			at = @At(
					value = "INVOKE_STRING",
					target = "Lnet/minecraft/util/profiler/Profiler;swap(Ljava/lang/String;)V",
					args = "ldc=village"
			)
	)
	private void swapTickTaskVillages(BooleanSupplier isAheadOfTime, CallbackInfo ci) {
		swapTickTask(TickTask.VILLAGES);
	}
	
	@Inject(
			method = "method_16327",
			at = @At(
					value = "INVOKE_STRING",
					target = "Lnet/minecraft/util/profiler/Profiler;swap(Ljava/lang/String;)V",
					args = "ldc=portalForcer"
			)
	)
	private void swapTickTaskPortals(BooleanSupplier isAheadOfTime, CallbackInfo ci) {
		swapTickTask(TickTask.PORTALS);
	}
	
	@Inject(
			method = "method_16327",
			at = @At(
					value = "INVOKE",
					shift = Shift.BEFORE,
					target = "Lnet/minecraft/server/world/ServerWorld;method_2131()V"
			)
	)
	private void swapTickTaskBlockEvents(BooleanSupplier isAheadOfTime, CallbackInfo ci) {
		swapTickTask(TickTask.BLOCK_EVENTS);
	}
	
	@Inject(
			method = "method_16327",
			at = @At(
					value = "RETURN"
			)
	)
	private void endTickTaskBlockEvents(BooleanSupplier isAheadOfTime, CallbackInfo ci) {
		endTickTask();
	}
	
	@Inject(
			method = "method_2141",
			at = @At(
					value = "HEAD"
			)
	)
	private void startTickTaskWakeSleepingPlayers(CallbackInfo ci) {
		startTickTask(TickTask.WAKE_SLEEPING_PLAYERS);
	}
	
	@Inject(
			method = "method_2141",
			at = @At(
					value = "RETURN"
			)
	)
	private void endTickTaskWakeSleepingPlayers(CallbackInfo ci) {
		endTickTask();
	}
	
	@Inject(
			method = "tickBlocks",
			at = @At(
					value = "INVOKE_STRING",
					target = "Lnet/minecraft/util/profiler/Profiler;push(Ljava/lang/String;)V",
					args = "ldc=pollingChunks"
			)
	)
	private void startDummyTask(CallbackInfo ci) {
		startTickTask(TickTask.UNKNOWN);
	}
	
	@Inject(
			method = "tickBlocks",
			at = @At(
					value = "INVOKE",
					target = "Ljava/util/Iterator;hasNext()Z"
			)
	)
	private void endTickTaskRandomTicks(CallbackInfo ci) {
		endTickTask();
	}
	
	@Inject(
			method = "tickBlocks",
			at = @At(
					value = "INVOKE_STRING",
					target = "Lnet/minecraft/util/profiler/Profiler;swap(Ljava/lang/String;)V",
					args = "ldc=tickChunk"
			)
	)
	private void startTickTaskTickChunk(CallbackInfo ci) {
		startTickTask(TickTask.TICK_CHUNK);
	}
	
	@Inject(
			method = "tickBlocks",
			at = @At(
					value = "INVOKE_STRING",
					target = "Lnet/minecraft/util/profiler/Profiler;swap(Ljava/lang/String;)V",
					args = "ldc=thunder"
					)
			)
	private void swapTickTaskThunder(CallbackInfo ci) {
		swapTickTask(TickTask.THUNDER);
	}
	
	@Inject(
			method = "tickBlocks",
			at = @At(
					value = "INVOKE_STRING",
					target = "Lnet/minecraft/util/profiler/Profiler;swap(Ljava/lang/String;)V",
					args = "ldc=iceandsnow"
			)
	)
	private void swapTickTaskPrecipitation(CallbackInfo ci) {
		swapTickTask(TickTask.PRECIPITATION);
	}
	
	@Inject(
			method = "tickBlocks",
			at = @At(
					value = "INVOKE_STRING",
					target = "Lnet/minecraft/util/profiler/Profiler;swap(Ljava/lang/String;)V",
					args = "ldc=tickBlocks"
			)
	)
	private void swapTickTaskRandomTicks(CallbackInfo ci) {
		swapTickTask(TickTask.RANDOM_TICKS);
	}
	
	@Inject(
			method = "method_11491",
			at = @At(
					value = "HEAD"
			)
	)
	private void startTickTaskPlayers(CallbackInfo ci) {
		startTickTask(TickTask.PLAYERS);
	}
	
	@Inject(
			method = "method_11491",
			at = @At(
					value = "RETURN"
			)
	)
	private void endTickTaskPlayers(CallbackInfo ci) {
		endTickTask();
	}
	
	@Inject(
			method = "method_21269",
			at = @At(
					value = "FIELD",
					shift = Shift.BEFORE,
					target = "Lnet/minecraft/server/world/ServerWorld;field_21842:Lnet/minecraft/class_3603;"
			)
	)
	private void startTickTaskBlockTicks(CallbackInfo ci) {
		startTickTask(TickTask.BLOCK_TICKS);
	}
	
	@Inject(
			method = "method_21269",
			at = @At(
					value = "INVOKE",
					ordinal = 0,
					shift = Shift.AFTER,
					target = "Lnet/minecraft/class_3603;method_16409()V"
			)
	)
	private void endTickTaskBlockTicks(CallbackInfo ci) {
		endTickTask();
	}
	
	@Inject(
			method = "method_21269",
			at = @At(
					value = "FIELD",
					shift = Shift.BEFORE,
					target = "Lnet/minecraft/server/world/ServerWorld;field_21843:Lnet/minecraft/class_3603;"
			)
	)
	private void startTickTaskFluidTicks(CallbackInfo ci) {
		startTickTask(TickTask.FLUID_TICKS);
	}
	
	@Inject(
			method = "method_21269",
			at = @At(
					value = "INVOKE",
					ordinal = 1,
					shift = Shift.AFTER,
					target = "Lnet/minecraft/class_3603;method_16409()V"
			)
	)
	private void endTickTaskFluidTicks(CallbackInfo ci) {
		endTickTask();
	}
	
	@Inject(
			method = "method_21258",
			at = @At(
					value = "INVOKE",
					shift = Shift.BEFORE,
					target = "Lnet/minecraft/class_4024;method_17801(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;)V"
			)
	)
	private void onTickFluid(TickableEntry<class_4023> scheduledTick, CallbackInfo ci) {
		getMultimeter().logScheduledTick((World)(Object)this, scheduledTick);
	}
	
	@Inject(
			method = "method_21264",
			at = @At(
					value = "INVOKE",
					shift = Shift.BEFORE,
					target = "Lnet/minecraft/block/BlockState;method_16875(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Ljava/util/Random;)V"
			)
	)
	private void onTickBlock(TickableEntry<Block> scheduledTick, CallbackInfo ci) {
		getMultimeter().logScheduledTick((World)(Object)this, scheduledTick);
	}
	
	@Inject(
			method = "method_2137",
			at = @At(
					value = "INVOKE",
					shift = Shift.BEFORE,
					target = "Lnet/minecraft/block/BlockState;method_16868(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;II)Z"
			)
	)
	private void onBlockEvent(BlockAction blockEvent, CallbackInfoReturnable<Boolean> cir) {
		getMultimeter().logBlockEvent((World)(Object)this, blockEvent);
	}
	
	@Override
	public MultimeterServer getMultimeterServer() {
		return ((IMinecraftServer)server).getMultimeterServer();
	}
}
