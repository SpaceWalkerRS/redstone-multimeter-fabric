package redstone.multimeter.mixin.common;

import java.util.Iterator;
import java.util.function.BooleanSupplier;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;

import net.minecraft.block.Block;
import net.minecraft.fluid.Fluid;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.BlockEvent;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.server.world.tick.ScheduledTick;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.world.World;
import net.minecraft.world.WorldData;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.chunk.WorldChunkSection;
import net.minecraft.world.dimension.Dimension;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.storage.DimensionDataStorage;
import net.minecraft.world.storage.WorldStorage;

import redstone.multimeter.common.BlockEventStatus;
import redstone.multimeter.common.TickTask;
import redstone.multimeter.interfaces.mixin.IMinecraftServer;
import redstone.multimeter.interfaces.mixin.IServerWorld;
import redstone.multimeter.server.MultimeterServer;

@Mixin(ServerWorld.class)
public abstract class ServerWorldMixin extends World implements IServerWorld {

	@Shadow @Final private MinecraftServer server;
	@Shadow @Final private ObjectLinkedOpenHashSet<BlockEvent> blockEvents;

	private int rsmm$queueSize;
	private int rsmm$currentDepth;
	private int rsmm$currentBatch;

	private ServerWorldMixin(WorldStorage storage, DimensionDataStorage dimensionStorage, WorldData data, Dimension dimension, Profiler profiler, boolean isClient) {
		super(storage, dimensionStorage, data, dimension, profiler, isClient);
	}

	@Inject(
		method = "tick",
		at = @At(
			value = "HEAD"
		)
	)
	private void startTickTaskTickLevel(BooleanSupplier hasTimeLeft, CallbackInfo ci) {
		rsmm$startTickTask(TickTask.TICK_LEVEL, DimensionType.getKey(dimension.getType()).toString());
	}

	@Inject(
		method = "tick",
		at = @At(
			value = "INVOKE_STRING",
			target = "Lnet/minecraft/util/profiler/Profiler;push(Ljava/lang/String;)V",
			args = "ldc=spawner"
		)
	)
	private void startTickTaskMobSpawning(BooleanSupplier hasTimeLeft, CallbackInfo ci) {
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
	private void swapTickTaskChunkSource(BooleanSupplier hasTimeLeft, CallbackInfo ci) {
		rsmm$swapTickTask(TickTask.CHUNK_SOURCE);
	}

	@Inject(
		method = "tick",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/WorldData;setTime(J)V"
		)
	)
	private void swapTickTaskTickTime(BooleanSupplier hasTimeLeft, CallbackInfo ci) {
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
	private void swapTickTaskScheduledTicks(BooleanSupplier hasTimeLeft, CallbackInfo ci) {
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
	private void swapTickTaskTickChunks(BooleanSupplier hasTimeLeft, CallbackInfo ci) {
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
	private void swapTickTaskChunkMap(BooleanSupplier hasTimeLeft, CallbackInfo ci) {
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
	private void swapTickTaskVillages(BooleanSupplier hasTimeLeft, CallbackInfo ci) {
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
	private void swapTickTaskPortals(BooleanSupplier hasTimeLeft, CallbackInfo ci) {
		rsmm$swapTickTask(TickTask.PORTALS);
	}

	@Inject(
		method = "tick",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/server/world/ServerWorld;doBlockEvents()V"
		)
	)
	private void swapTickTaskBlockEvents(BooleanSupplier hasTimeLeft, CallbackInfo ci) {
		rsmm$swapTickTask(TickTask.BLOCK_EVENTS);
	}

	@Inject(
		method = "tick",
		at = @At(
			value = "TAIL"
		)
	)
	private void endTickTaskTickBlockEventsAndLevel(BooleanSupplier hasTimeLeft, CallbackInfo ci) {
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

	@Inject(
		method = "doScheduledTicks",
		at = @At(
			value = "FIELD",
			target = "Lnet/minecraft/server/world/ServerWorld;blockTicks:Lnet/minecraft/server/world/tick/ServerTickList;"
		)
	)
	private void startTickTaskBlockTicks(CallbackInfo ci) {
		rsmm$startTickTask(TickTask.BLOCK_TICKS);
	}

	@Inject(
		method = "doScheduledTicks",
		at = @At(
			value = "FIELD",
			target = "Lnet/minecraft/server/world/ServerWorld;fluidTicks:Lnet/minecraft/server/world/tick/ServerTickList;"
		)
	)
	private void swapTickTaskFluidTicks(CallbackInfo ci) {
		rsmm$swapTickTask(TickTask.FLUID_TICKS);
	}

	@Inject(
		method = "doScheduledTicks",
		slice = @Slice(
			from = @At(
				value = "FIELD",
				target = "Lnet/minecraft/server/world/ServerWorld;fluidTicks:Lnet/minecraft/server/world/tick/ServerTickList;"
			)
		),
		at = @At(
			value = "INVOKE",
			ordinal = 0,
			shift = Shift.AFTER,
			target = "Lnet/minecraft/server/world/tick/ServerTickList;tick()V"
		)
	)
	private void endTickTaskFluidTicks(CallbackInfo ci) {
		rsmm$endTickTask();
	}

	@Inject(
		method = "tickFluid",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/fluid/state/FluidState;tick(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;)V"
		)
	)
	private void logFluidTick(ScheduledTick<Fluid> scheduledTick, CallbackInfo ci) {
		getMultimeter().logScheduledTick(this, scheduledTick.pos, scheduledTick.priority, false);
	}

	@Inject(
		method = "tickBlock",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/block/state/BlockState;tick(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Ljava/util/Random;)V"
		)
	)
	private void logBlockTick(ScheduledTick<Block> scheduledTick, CallbackInfo ci) {
		getMultimeter().logScheduledTick(this, scheduledTick.pos, scheduledTick.priority, false);
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
			target = "Lnet/minecraft/block/state/BlockState;randomTick(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Ljava/util/Random;)V"
		)
	)
	private void logRandomBlockTick(CallbackInfo ci, int randomTicks, boolean isRaining, boolean isThundering, Iterator<WorldChunk> it, WorldChunk chunk, int minX, int minZ, WorldChunkSection[] sections, int sectionIndex, int maxSectionIndex, WorldChunkSection section, int attempt, int randomNumber, int localX, int localZ, int localY) {
		getMultimeter().logRandomTick(this, new BlockPos(minX + localX, section.getOffsetY() + localY, minZ + localZ));
	}

	@Inject(
		method = "tickChunks",
		locals = LocalCapture.CAPTURE_FAILHARD,
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/fluid/state/FluidState;randomTick(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Ljava/util/Random;)V"
		)
	)
	private void logRandomFluidTick(CallbackInfo ci, int randomTicks, boolean isRaining, boolean isThundering, Iterator<WorldChunk> it, WorldChunk chunk, int minX, int minZ, WorldChunkSection[] sections, int sectionIndex, int maxSectionIndex, WorldChunkSection section, int attempt, int randomNumber, int localX, int localZ, int localY) {
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
			value = "HEAD"
		)
	)
	private void onAddBlockEvent(BlockPos pos, Block block, int type, int data, CallbackInfo ci) {
		rsmm$queueSize = blockEvents.size();
	}

	@Inject(
		method = "addBlockEvent",
		at = @At(
			value = "TAIL"
		)
	)
	private void postAddBlockEvent(BlockPos pos, Block block, int type, int data, CallbackInfo ci) {
		// The queue is a set; only one block event for the same block and position
		// can exist at any time. To check whether the block event was added, we
		// check the queue size before and after the new block event was offered.
		if (rsmm$queueSize < blockEvents.size()) {
			getMultimeter().logBlockEvent(this, pos, type, rsmm$currentDepth + 1, BlockEventStatus.QUEUED);
		}
	}

	@Inject(
		method = "doBlockEvents",
		at = @At(
			value = "HEAD"
		)
	)
	private void onRunBlockEvents(CallbackInfo ci) {
		rsmm$currentDepth = 0;
		rsmm$currentBatch = blockEvents.size();
	}

	@Inject(
		method = "doBlockEvents",
		at = @At(
			value = "INVOKE",
			target = "Lit/unimi/dsi/fastutil/objects/ObjectLinkedOpenHashSet;isEmpty()Z"
		)
	)
	private void onRunBlockEvent(CallbackInfo ci) {
		if (rsmm$currentBatch == 0) {
			rsmm$currentDepth++;
			rsmm$currentBatch = blockEvents.size();
		}

		rsmm$currentBatch--;
	}

	@Inject(
		method = "doBlockEvents",
		at = @At(
			value = "TAIL"
		)
	)
	private void postRunBlockEvents(CallbackInfo ci) {
		rsmm$currentDepth = -1;
		rsmm$currentBatch = 0;
	}

	@Inject(
		method = "doBlockEvent",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/block/state/BlockState;doEvent(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;II)Z"
		)
	)
	private void logBlockEvent(BlockEvent blockEvent, CallbackInfoReturnable<Boolean> cir) {
		getMultimeter().logBlockEvent(this, blockEvent.getPos(), blockEvent.getType(), rsmm$currentDepth, BlockEventStatus.TRIGGERED);
	}

	@Inject(
		method = "doBlockEvent",
		at = @At(
			value = "RETURN"
		)
	)
	private void logBlockEventResult(BlockEvent blockEvent, CallbackInfoReturnable<Boolean> cir) {
		getMultimeter().logBlockEvent(this, blockEvent.getPos(), blockEvent.getType(), rsmm$currentDepth, cir.getReturnValue() ? BlockEventStatus.SUCCESS : BlockEventStatus.FAILURE);
	}

	@Override
	public MultimeterServer getMultimeterServer() {
		return ((IMinecraftServer)server).getMultimeterServer();
	}
}
