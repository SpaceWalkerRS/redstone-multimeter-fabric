package redstone.multimeter.mixin.common;

import java.util.Iterator;
import java.util.Set;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.block.Block;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.BlockEvent;
import net.minecraft.server.world.ScheduledTick;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.world.World;
import net.minecraft.world.WorldData;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.chunk.WorldChunkSection;
import net.minecraft.world.dimension.Dimension;
import net.minecraft.world.storage.WorldStorage;

import redstone.multimeter.common.TickTask;
import redstone.multimeter.interfaces.mixin.IMinecraftServer;
import redstone.multimeter.interfaces.mixin.IServerWorld;
import redstone.multimeter.server.MultimeterServer;

@Mixin(ServerWorld.class)
public abstract class ServerWorldMixin extends World implements IServerWorld {

	@Shadow @Final private MinecraftServer server;
	@Shadow @Final private Set<ScheduledTick> scheduledTicks;

	private int rsmm$scheduledTicks;
	private int rsmm$currentDepth;

	private ServerWorldMixin(WorldStorage storage, WorldData data, Dimension dimension, Profiler profiler, boolean isClient) {
		super(storage, data, dimension, profiler, isClient);
	}

	@Inject(
		method = "tick",
		at = @At(
			value = "HEAD"
		)
	)
	private void startTickTaskTickLevel(CallbackInfo ci) {
		rsmm$startTickTask(TickTask.TICK_LEVEL, dimension.getType().getKey());
	}

	@Inject(
		method = "tick",
		at = @At(
			value = "INVOKE_STRING",
			target = "Lnet/minecraft/util/profiler/Profiler;push(Ljava/lang/String;)V",
			args = "ldc=mobSpawner"
		)
	)
	private void startTickTaskMobSpawning(CallbackInfo ci) {
		rsmm$startTickTask(TickTask.MOB_SPAWNING);
	}

	@Inject(
		method = "tick",
		at = @At(
			value = "INVOKE_STRING",
			target = "Lnet/minecraft/util/profiler/Profiler;swap(Ljava/lang/String;)V",
			args = "ldc=chunkSource"
		)
	)
	private void swapTickTaskChunkSource(CallbackInfo ci) {
		rsmm$swapTickTask(TickTask.CHUNK_SOURCE);
	}

	@Inject(
		method = "tick",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/WorldData;setTime(J)V"
		)
	)
	private void swapTickTaskTickTime(CallbackInfo ci) {
		rsmm$swapTickTask(TickTask.TICK_TIME);
		getMultimeterServer().tickTime(this);
	}

	@Inject(
		method = "tick",
		at = @At(
			value = "INVOKE_STRING",
			target = "Lnet/minecraft/util/profiler/Profiler;swap(Ljava/lang/String;)V",
			args = "ldc=tickPending"
		)
	)
	private void swapTickTaskScheduledTicks(CallbackInfo ci) {
		rsmm$swapTickTask(TickTask.SCHEDULED_TICKS);
	}

	@Inject(
		method = "tick",
		at = @At(
			value = "INVOKE_STRING",
			target = "Lnet/minecraft/util/profiler/Profiler;swap(Ljava/lang/String;)V",
			args = "ldc=tickBlocks"
		)
	)
	private void swapTickTaskTickChunks(CallbackInfo ci) {
		rsmm$swapTickTask(TickTask.TICK_CHUNKS);
	}

	@Inject(
		method = "tick",
		at = @At(
			value = "INVOKE_STRING",
			target = "Lnet/minecraft/util/profiler/Profiler;swap(Ljava/lang/String;)V",
			args = "ldc=chunkMap"
		)
	)
	private void swapTickTaskChunkMap(CallbackInfo ci) {
		rsmm$swapTickTask(TickTask.CHUNK_MAP);
	}

	@Inject(
		method = "tick",
		at = @At(
			value = "INVOKE_STRING",
			target = "Lnet/minecraft/util/profiler/Profiler;swap(Ljava/lang/String;)V",
			args = "ldc=village"
		)
	)
	private void swapTickTaskVillages(CallbackInfo ci) {
		rsmm$swapTickTask(TickTask.VILLAGES);
	}

	@Inject(
		method = "tick",
		at = @At(
			value = "INVOKE_STRING",
			target = "Lnet/minecraft/util/profiler/Profiler;swap(Ljava/lang/String;)V",
			args = "ldc=portalForcer"
		)
	)
	private void swapTickTaskPortals(CallbackInfo ci) {
		rsmm$swapTickTask(TickTask.PORTALS);
	}

	@Inject(
		method = "tick",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/server/world/ServerWorld;doBlockEvents()V"
		)
	)
	private void swapTickTaskBlockEvents(CallbackInfo ci) {
		rsmm$swapTickTask(TickTask.BLOCK_EVENTS);
	}

	@Inject(
		method = "tick",
		at = @At(
			value = "TAIL"
		)
	)
	private void endTickTaskTickBlockEventsAndLevel(CallbackInfo ci) {
		rsmm$endTickTask();
		rsmm$endTickTask();
	}

	@Inject(
		method = "wakeSleepingPlayers",
		at = @At(
			value = "HEAD"
		)
	)
	private void startTickTaskWakeSleepingPlayers(CallbackInfo ci) {
		rsmm$startTickTask(TickTask.WAKE_SLEEPING_PLAYERS);
	}

	@Inject(
		method = "wakeSleepingPlayers",
		at = @At(
			value = "TAIL"
		)
	)
	private void endTickTaskWakeSleepingPlayers(CallbackInfo ci) {
		rsmm$endTickTask();
	}

	private boolean rsmm$firstTickingChunk; // pseudo local variable

	@Inject(
		method = "tickChunks",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/server/ChunkMap;getTickingChunks()Ljava/util/Iterator;"
		)
	)
	private void pollTickingChunks(CallbackInfo ci) {
		rsmm$firstTickingChunk = true;
	}

	@Inject(
		method = "tickChunks",
		locals = LocalCapture.CAPTURE_FAILHARD,
		at = @At(
			value = "INVOKE",
			ordinal = 0,
			target = "Ljava/util/Iterator;hasNext()Z"
		)
	)
	private void startOrSwapOrEndTickTaskTickChunkDebug(CallbackInfo ci, Iterator<WorldChunk> it) {
		startOrSwapOrEndTickTaskTickChunk(it);
	}

	@Inject(
		method = "tickChunks",
		locals = LocalCapture.CAPTURE_FAILHARD,
		at = @At(
			value = "INVOKE",
			ordinal = 1,
			target = "Ljava/util/Iterator;hasNext()Z"
		)
	)
	private void startOrSwapOrEndTickTaskTickChunk(CallbackInfo ci, int randomTicks, boolean isRaining, boolean isThundering, Iterator<WorldChunk> it) {
		startOrSwapOrEndTickTaskTickChunk(it);
	}

	private void startOrSwapOrEndTickTaskTickChunk(Iterator<WorldChunk> it) {
		if (it.hasNext()) {
			if (rsmm$firstTickingChunk) {
				rsmm$startTickTask(TickTask.TICK_CHUNK);
			} else {
				rsmm$swapTickTask(TickTask.TICK_CHUNK);
			}
		} else {
			if (rsmm$firstTickingChunk) {
				// no ticking chunks
			} else {
				rsmm$endTickTask();
			}
		}

		rsmm$firstTickingChunk = false;
	}

	@Inject(
		method = "tickChunks",
		at = @At(
			value = "INVOKE_STRING",
			target = "Lnet/minecraft/util/profiler/Profiler;swap(Ljava/lang/String;)V",
			args = "ldc=thunder"
		)
	)
	private void swapTickTaskThunder(CallbackInfo ci) {
		rsmm$swapTickTask(TickTask.THUNDER);
	}

	@Inject(
		method = "tickChunks",
		at = @At(
			value = "INVOKE_STRING",
			target = "Lnet/minecraft/util/profiler/Profiler;swap(Ljava/lang/String;)V",
			args = "ldc=iceandsnow"
		)
	)
	private void swapTickTaskPrecipitation(CallbackInfo ci) {
		rsmm$swapTickTask(TickTask.PRECIPITATION);
	}

	@Inject(
		method = "tickChunks",
		at = @At(
			value = "INVOKE_STRING",
			target = "Lnet/minecraft/util/profiler/Profiler;swap(Ljava/lang/String;)V",
			args = "ldc=tickBlocks"
		)
	)
	private void swapTickTaskRandomTicks(CallbackInfo ci) {
		rsmm$swapTickTask(TickTask.RANDOM_TICKS);
	}

	@Inject(
		method = "tickChunks",
		locals = LocalCapture.CAPTURE_FAILHARD,
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/block/Block;randomTick(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/state/BlockState;Ljava/util/Random;)V"
		)
	)
	private void logRandomTick(CallbackInfo ci, int randomTicks, boolean isRaining, boolean isThundering, Iterator<WorldChunk> it, WorldChunk chunk, int minX, int minZ, WorldChunkSection[] sections, int sectionIndex, int maxSectionIndex, WorldChunkSection section, int attempt, int randomNumber, int localX, int localZ, int localY) {
		getMultimeter().logRandomTick(this, new BlockPos(minX + localX, section.getOffsetY() + localY, minZ + localZ));
	}

	@Inject(
		method = "tickPlayers",
		at = @At(
			value = "HEAD"
		)
	)
	private void startTickTaskPlayers(CallbackInfo ci) {
		rsmm$startTickTask(TickTask.PLAYERS);
	}

	@Inject(
		method = "tickPlayers",
		at = @At(
			value = "TAIL"
		)
	)
	private void endTickTaskPlayers(CallbackInfo ci) {
		rsmm$endTickTask();
	}

	@Inject(
		method = "scheduleTick(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/Block;II)V",
		at = @At(
			value = "HEAD"
		)
	)
	private void captureScheduledTicks(BlockPos pos, Block block, int delay, int priority, CallbackInfo ci) {
		rsmm$scheduledTicks = scheduledTicks.size();
	}

	@Inject(
		method = "scheduleTick(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/Block;II)V",
		at = @At(
			value = "TAIL"
		)
	)
	private void logScheduleTick(BlockPos pos, Block block, int delay, int priority, CallbackInfo ci) {
		if (rsmm$scheduledTicks != scheduledTicks.size()) {
			getMultimeter().logScheduledTick(this, pos, priority, true);
		}
	}

	@Inject(
		method = "doScheduledTicks",
		locals = LocalCapture.CAPTURE_FAILHARD,
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/block/Block;tick(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/state/BlockState;Ljava/util/Random;)V"
		)
	)
	private void logScheduledTick(boolean debug, CallbackInfoReturnable<Boolean> cir, Iterator<ScheduledTick> scheduledTicksThisTick, ScheduledTick scheduledTick) {
		getMultimeter().logScheduledTick(this, scheduledTick.pos, scheduledTick.priority, false);
	}

	@Inject(
		method = "tickWeather",
		at = @At(
			value = "HEAD"
		)
	)
	private void startTickTaskWeather(CallbackInfo ci) {
		rsmm$startTickTask(TickTask.WEATHER);
	}

	@Inject(
		method = "tickWeather",
		at = @At(
			value = "TAIL"
		)
	)
	private void endTickTaskWeather(CallbackInfo ci) {
		rsmm$endTickTask();
	}

	@Inject(
		method = "addBlockEvent",
		at = @At(
			value = "TAIL"
		)
	)
	private void addBlockEvent(BlockPos pos, Block block, int type, int data, CallbackInfo ci) {
		getMultimeter().logBlockEvent(this, pos, type, rsmm$currentDepth + 1, true);
	}

	@Inject(
		method = "doBlockEvents",
		at = @At(
			value = "HEAD"
		)
	)
	private void onRunBlockEvents(CallbackInfo ci) {
		rsmm$currentDepth = 0;
	}

	@Inject(
		method = "doBlockEvents",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/server/world/ServerWorld$BlockEventQueue;isEmpty()Z"
		)
	)
	private void onNextBlockEventQueue(CallbackInfo ci) {
		rsmm$currentDepth++;
	}

	@Inject(
		method = "doBlockEvents",
		at = @At(
			value = "TAIL"
		)
	)
	private void postRunBlockEvents(CallbackInfo ci) {
		rsmm$currentDepth = -1;
	}

	@Inject(
		method = "doBlockEvent",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/block/state/BlockState;doEvent(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;II)Z"
		)
	)
	private void logBlockEvent(BlockEvent blockEvent, CallbackInfoReturnable<Boolean> cir) {
		getMultimeter().logBlockEvent(this, blockEvent.getPos(), blockEvent.getType(), rsmm$currentDepth, false);
	}

	@Override
	public MultimeterServer getMultimeterServer() {
		return ((IMinecraftServer)server).getMultimeterServer();
	}
}
