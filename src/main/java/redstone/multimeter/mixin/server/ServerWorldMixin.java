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

import net.minecraft.class_30;
import net.minecraft.class_37;
import net.minecraft.block.Block;
import net.minecraft.fluid.Fluid;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.BlockAction;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.profiler.ProfilerSystem;
import net.minecraft.world.ScheduledTick;
import net.minecraft.world.World;
import net.minecraft.world.dimension.Dimension;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.level.LevelProperties;

import redstone.multimeter.common.TickTask;
import redstone.multimeter.interfaces.mixin.IMinecraftServer;
import redstone.multimeter.interfaces.mixin.IServerWorld;
import redstone.multimeter.server.MultimeterServer;

@Mixin(ServerWorld.class)
public abstract class ServerWorldMixin extends World implements IServerWorld {
	
	@Shadow @Final private MinecraftServer server;
	
	protected ServerWorldMixin(class_30 arg, class_37 arg2, LevelProperties levelProperties, Dimension dimension, ProfilerSystem profilerSystem, boolean bl) {
		super(arg, arg2, levelProperties, dimension, profilerSystem, bl);
	}
	
	@Inject(
			method = "method_8441",
			at = @At(
					value = "INVOKE_STRING",
					target = "Lnet/minecraft/util/profiler/ProfilerSystem;method_15396(Ljava/lang/String;)V",
					args = "ldc=spawner"
			)
	)
	private void startTickTaskMobSpawning(BooleanSupplier isAheadOfTime, CallbackInfo ci) {
		startTickTask(TickTask.MOB_SPAWNING);
	}
	
	@Inject(
			method = "method_8441",
			at = @At(
					value = "INVOKE_STRING",
					target = "Lnet/minecraft/util/profiler/ProfilerSystem;method_15405(Ljava/lang/String;)V",
					args = "ldc=chunkSource"
			)
	)
	private void swapTickTaskChunkSource(BooleanSupplier isAheadOfTime, CallbackInfo ci) {
		swapTickTask(TickTask.CHUNK_SOURCE);
	}
	
	@Inject(
			method = "method_8441",
			at = @At(
					value = "INVOKE",
					shift = Shift.AFTER,
					target = "Lnet/minecraft/world/level/LevelProperties;setTime(J)V"
			)
	)
	private void onTickTime(BooleanSupplier isAheadOfTime, CallbackInfo ci) {
		if (dimension.getType() == DimensionType.OVERWORLD) {
			getMultimeterServer().onOverworldTickTime();
		}
	}
	
	@Inject(
			method = "method_8441",
			at = @At(
					value = "INVOKE_STRING",
					target = "Lnet/minecraft/util/profiler/ProfilerSystem;method_15405(Ljava/lang/String;)V",
					args = "ldc=tickPending"
			)
	)
	private void swapTickTaskScheduledTicks(BooleanSupplier isAheadOfTime, CallbackInfo ci) {
		swapTickTask(TickTask.SCHEDULED_TICKS);
	}
	
	@Inject(
			method = "method_8441",
			at = @At(
					value = "INVOKE_STRING",
					target = "Lnet/minecraft/util/profiler/ProfilerSystem;method_15405(Ljava/lang/String;)V",
					args = "ldc=tickBlocks"
			)
	)
	private void swapTickTaskTickChunks(BooleanSupplier isAheadOfTime, CallbackInfo ci) {
		swapTickTask(TickTask.TICK_CHUNKS);
	}
	
	@Inject(
			method = "method_8441",
			at = @At(
					value = "INVOKE_STRING",
					target = "Lnet/minecraft/util/profiler/ProfilerSystem;method_15405(Ljava/lang/String;)V",
					args = "ldc=chunkMap"
			)
	)
	private void swapTickTaskChunkMap(BooleanSupplier isAheadOfTime, CallbackInfo ci) {
		swapTickTask(TickTask.CHUNK_MAP);
	}
	
	@Inject(
			method = "method_8441",
			at = @At(
					value = "INVOKE_STRING",
					target = "Lnet/minecraft/util/profiler/ProfilerSystem;method_15405(Ljava/lang/String;)V",
					args = "ldc=village"
			)
	)
	private void swapTickTaskVillages(BooleanSupplier isAheadOfTime, CallbackInfo ci) {
		swapTickTask(TickTask.VILLAGES);
	}
	
	@Inject(
			method = "method_8441",
			at = @At(
					value = "INVOKE_STRING",
					target = "Lnet/minecraft/util/profiler/ProfilerSystem;method_15405(Ljava/lang/String;)V",
					args = "ldc=portalForcer"
			)
	)
	private void swapTickTaskPortals(BooleanSupplier isAheadOfTime, CallbackInfo ci) {
		swapTickTask(TickTask.PORTALS);
	}
	
	@Inject(
			method = "method_8441",
			at = @At(
					value = "INVOKE",
					shift = Shift.BEFORE,
					target = "Lnet/minecraft/server/world/ServerWorld;sendBlockActions()V"
			)
	)
	private void swapTickTaskBlockEvents(BooleanSupplier isAheadOfTime, CallbackInfo ci) {
		swapTickTask(TickTask.BLOCK_EVENTS);
	}
	
	@Inject(
			method = "method_8441",
			at = @At(
					value = "RETURN"
			)
	)
	private void endTickTaskBlockEvents(BooleanSupplier isAheadOfTime, CallbackInfo ci) {
		endTickTask();
	}
	
	@Inject(
			method = "method_14200",
			at = @At(
					value = "HEAD"
			)
	)
	private void startTickTaskWakeSleepingPlayers(CallbackInfo ci) {
		startTickTask(TickTask.WAKE_SLEEPING_PLAYERS);
	}
	
	@Inject(
			method = "method_14200",
			at = @At(
					value = "RETURN"
			)
	)
	private void endTickTaskWakeSleepingPlayers(CallbackInfo ci) {
		endTickTask();
	}
	
	@Inject(
			method = "method_73212",
			at = @At(
					value = "INVOKE_STRING",
					target = "Lnet/minecraft/util/profiler/ProfilerSystem;method_15396(Ljava/lang/String;)V",
					args = "ldc=pollingChunks"
			)
	)
	private void startDummyTask(CallbackInfo ci) {
		startTickTask(TickTask.UNKNOWN);
	}
	
	@Inject(
			method = "method_73212",
			at = @At(
					value = "INVOKE",
					target = "Ljava/util/Iterator;hasNext()Z"
			)
	)
	private void endTickTaskRandomTicks(CallbackInfo ci) {
		endTickTask();
	}
	
	@Inject(
			method = "method_73212",
			at = @At(
					value = "INVOKE_STRING",
					target = "Lnet/minecraft/util/profiler/ProfilerSystem;method_15405(Ljava/lang/String;)V",
					args = "ldc=tickChunk"
			)
	)
	private void startTickTaskTickChunk(CallbackInfo ci) {
		startTickTask(TickTask.TICK_CHUNK);
	}
	
	@Inject(
			method = "method_73212",
			at = @At(
					value = "INVOKE_STRING",
					target = "Lnet/minecraft/util/profiler/ProfilerSystem;method_15405(Ljava/lang/String;)V",
					args = "ldc=thunder"
					)
			)
	private void swapTickTaskThunder(CallbackInfo ci) {
		swapTickTask(TickTask.THUNDER);
	}
	
	@Inject(
			method = "method_73212",
			at = @At(
					value = "INVOKE_STRING",
					target = "Lnet/minecraft/util/profiler/ProfilerSystem;method_15405(Ljava/lang/String;)V",
					args = "ldc=iceandsnow"
			)
	)
	private void swapTickTaskPrecipitation(CallbackInfo ci) {
		swapTickTask(TickTask.PRECIPITATION);
	}
	
	@Inject(
			method = "method_73212",
			at = @At(
					value = "INVOKE_STRING",
					target = "Lnet/minecraft/util/profiler/ProfilerSystem;method_15405(Ljava/lang/String;)V",
					args = "ldc=tickBlocks"
			)
	)
	private void swapTickTaskRandomTicks(CallbackInfo ci) {
		swapTickTask(TickTask.RANDOM_TICKS);
	}
	
	@Inject(
			method = "method_8541",
			at = @At(
					value = "HEAD"
			)
	)
	private void startTickTaskPlayers(CallbackInfo ci) {
		startTickTask(TickTask.PLAYERS);
	}
	
	@Inject(
			method = "method_8541",
			at = @At(
					value = "HEAD"
			)
	)
	private void endTickTaskPlayers(CallbackInfo ci) {
		endTickTask();
	}
	
	@Inject(
			method = "method_14181",
			at = @At(
					value = "FIELD",
					shift = Shift.BEFORE,
					target = "Lnet/minecraft/server/world/ServerWorld;blockTickScheduler:Lnet/minecraft/server/world/ServerTickScheduler;"
			)
	)
	private void startTickTaskBlockTicks(CallbackInfo ci) {
		startTickTask(TickTask.BLOCK_TICKS);
	}
	
	@Inject(
			method = "method_14181",
			at = @At(
					value = "INVOKE",
					ordinal = 0,
					shift = Shift.AFTER,
					target = "Lnet/minecraft/server/world/ServerTickScheduler;tick()V"
			)
	)
	private void endTickTaskBlockTicks(CallbackInfo ci) {
		endTickTask();
	}
	
	@Inject(
			method = "method_14181",
			at = @At(
					value = "FIELD",
					shift = Shift.BEFORE,
					target = "Lnet/minecraft/server/world/ServerWorld;fluidTickScheduler:Lnet/minecraft/server/world/ServerTickScheduler;"
			)
	)
	private void startTickTaskFluidTicks(CallbackInfo ci) {
		startTickTask(TickTask.FLUID_TICKS);
	}
	
	@Inject(
			method = "method_14181",
			at = @At(
					value = "INVOKE",
					ordinal = 1,
					shift = Shift.AFTER,
					target = "Lnet/minecraft/server/world/ServerTickScheduler;tick()V"
			)
	)
	private void endTickTaskFluidTicks(CallbackInfo ci) {
		endTickTask();
	}
	
	@Inject(
			method = "tickFluid",
			at = @At(
					value = "INVOKE",
					shift = Shift.BEFORE,
					target = "Lnet/minecraft/fluid/FluidState;onScheduledTick(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;)V"
			)
	)
	private void onTickFluid(ScheduledTick<Fluid> scheduledTick, CallbackInfo ci) {
		getMultimeter().logScheduledTick((World)(Object)this, scheduledTick);
	}
	
	@Inject(
			method = "tickBlock",
			at = @At(
					value = "INVOKE",
					shift = Shift.BEFORE,
					target = "Lnet/minecraft/BlockState;method_73270(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Ljava/util/Random;)V"
			)
	)
	private void onTickBlock(ScheduledTick<Block> scheduledTick, CallbackInfo ci) {
		getMultimeter().logScheduledTick((World)(Object)this, scheduledTick);
	}
	
	@Inject(
			method = "method_14174",
			at = @At(
					value = "INVOKE",
					shift = Shift.BEFORE,
					target = "Lnet/minecraft/BlockState;method_73263(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;II)Z"
			)
	)
	private void onProcessBlockEvent(BlockAction blockEvent, CallbackInfoReturnable<Boolean> cir) {
		getMultimeter().logBlockEvent((World)(Object)this, blockEvent);
	}
	
	@Override
	public MultimeterServer getMultimeterServer() {
		return ((IMinecraftServer)server).getMultimeterServer();
	}
}
