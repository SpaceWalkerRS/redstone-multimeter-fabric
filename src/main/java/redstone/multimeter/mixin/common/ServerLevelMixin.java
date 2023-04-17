package redstone.multimeter.mixin.common;

import java.util.function.BiFunction;
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

import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.DerivedServerLevel;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockEventData;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.TickNextTickData;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.chunk.ChunkSource;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.dimension.Dimension;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.storage.LevelData;

import redstone.multimeter.common.TickTask;
import redstone.multimeter.interfaces.mixin.IMinecraftServer;
import redstone.multimeter.interfaces.mixin.IServerLevel;
import redstone.multimeter.server.MultimeterServer;

@Mixin(ServerLevel.class)
public abstract class ServerLevelMixin extends Level implements IServerLevel {

	@Shadow @Final private MinecraftServer server;
	@Shadow @Final private ObjectLinkedOpenHashSet<BlockEventData> blockEvents;

	private int rsmm$queueSize;
	private int rsmm$currentDepth;
	private int rsmm$currentBatch;

	private ServerLevelMixin(LevelData data, DimensionType dimension, BiFunction<Level, Dimension, ChunkSource> chunkSource, ProfilerFiller profiler, boolean isClientSide) {
		super(data, dimension, chunkSource, profiler, isClientSide);
	}

	@Inject(
		method = "tick",
		at = @At(
			value = "HEAD"
		)
	)
	private void startTickTaskTickLevel(BooleanSupplier hasTimeLeft, CallbackInfo ci) {
		rsmm$startTickTask(TickTask.TICK_LEVEL, DimensionType.getName(dimension.getType()).toString());
	}

	@Inject(
		method = "tick",
		at = @At(
			value = "INVOKE_STRING",
			target = "Lnet/minecraft/util/profiling/ProfilerFiller;push(Ljava/lang/String;)V",
			args = "ldc=world border"
		)
	)
	private void startTickTaskWorldBorder(BooleanSupplier hasTimeLeft, CallbackInfo ci) {
		rsmm$startTickTask(TickTask.WORLD_BORDER);
	}

	@Inject(
		method = "tick",
		at = @At(
			value = "INVOKE_STRING",
			target = "Lnet/minecraft/util/profiling/ProfilerFiller;popPush(Ljava/lang/String;)V",
			args = "ldc=weather"
		)
	)
	private void swapTickTaskWeather(BooleanSupplier hasTimeLeft, CallbackInfo ci) {
		rsmm$swapTickTask(TickTask.WEATHER);
	}

	@Inject(
		method = "tick",
		slice = @Slice(
			from = @At(
				value = "FIELD",
				target = "Lnet/minecraft/world/level/GameRules;RULE_DAYLIGHT:Lnet/minecraft/world/level/GameRules$Key;"
			)
		),
		at = @At(
			value = "INVOKE",
			ordinal = 0,
			target = "Ljava/util/stream/Stream;forEach(Ljava/util/function/Consumer;)V"
		)
	)
	private void startTickTaskWakeSleepingPlayers(CallbackInfo ci) {
		rsmm$startTickTask(TickTask.WAKE_SLEEPING_PLAYERS);
	}

	@Inject(
		method = "tick",
		slice = @Slice(
			from = @At(
				value = "FIELD",
				target = "Lnet/minecraft/world/level/GameRules;RULE_DAYLIGHT:Lnet/minecraft/world/level/GameRules$Key;"
			)
		),
		at = @At(
			value = "INVOKE",
			ordinal = 0,
			shift = Shift.AFTER,
			target = "Ljava/util/stream/Stream;forEach(Ljava/util/function/Consumer;)V"
		)
	)
	private void endTickTaskWakeSleepingPlayers(CallbackInfo ci) {
		rsmm$endTickTask();
	}

	@Inject(
		method = "tick",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/server/level/ServerLevel;tickTime()V"
		)
	)
	private void swapTickTaskTickTime(BooleanSupplier hasTimeLeft, CallbackInfo ci) {
		if (!((Level)this instanceof DerivedServerLevel)) {
			rsmm$swapTickTask(TickTask.TICK_TIME);
		}

		getMultimeterServer().tickTime(this);
	}

	@Inject(
		method = "tick",
		at = @At(
			value = "INVOKE_STRING",
			target = "Lnet/minecraft/util/profiling/ProfilerFiller;popPush(Ljava/lang/String;)V",
			args = "ldc=tickPending"
		)
	)
	private void swapTickTaskScheduledTicks(BooleanSupplier hasTimeLeft, CallbackInfo ci) {
		rsmm$swapTickTask(TickTask.SCHEDULED_TICKS);
	}

	@Inject(
		method = "tick",
		at = @At(
			value = "FIELD",
			target = "Lnet/minecraft/server/level/ServerLevel;blockTicks:Lnet/minecraft/world/level/ServerTickList;"
		)
	)
	private void startTickTaskBlockTicks(BooleanSupplier hasTimeLeft, CallbackInfo ci) {
		rsmm$startTickTask(TickTask.BLOCK_TICKS);
	}

	@Inject(
		method = "tick",
		at = @At(
			value = "FIELD",
			target = "Lnet/minecraft/server/level/ServerLevel;liquidTicks:Lnet/minecraft/world/level/ServerTickList;"
		)
	)
	private void swapTickTaskFluidTicks(BooleanSupplier hasTimeLeft, CallbackInfo ci) {
		rsmm$swapTickTask(TickTask.FLUID_TICKS);
	}

	@Inject(
		method = "tick",
		slice = @Slice(
			from = @At(
				value = "FIELD",
				target = "Lnet/minecraft/server/level/ServerLevel;liquidTicks:Lnet/minecraft/world/level/ServerTickList;"
			)
		),
		at = @At(
			value = "INVOKE",
			ordinal = 0,
			shift = Shift.AFTER,
			target = "Lnet/minecraft/world/level/ServerTickList;tick()V"
		)
	)
	private void endTickTaskFluidTicks(BooleanSupplier hasTimeLeft, CallbackInfo ci) {
		rsmm$endTickTask();
	}

	@Inject(
		method = "tick",
		at = @At(
			value = "INVOKE_STRING",
			target = "Lnet/minecraft/util/profiling/ProfilerFiller;popPush(Ljava/lang/String;)V",
			args = "ldc=raid"
		)
	)
	private void swapTickTaskRaids(BooleanSupplier hasTimeLeft, CallbackInfo ci) {
		rsmm$swapTickTask(TickTask.RAIDS);
	}

	@Inject(
		method = "tick",
		at = @At(
			value = "INVOKE_STRING",
			target = "Lnet/minecraft/util/profiling/ProfilerFiller;popPush(Ljava/lang/String;)V",
			args = "ldc=chunkSource"
		)
	)
	private void swapTickTaskChunkSource(BooleanSupplier hasTimeLeft, CallbackInfo ci) {
		rsmm$swapTickTask(TickTask.CHUNK_SOURCE);
	}

	@Inject(
		method = "tick",
		at = @At(
			value = "INVOKE_STRING",
			target = "Lnet/minecraft/util/profiling/ProfilerFiller;popPush(Ljava/lang/String;)V",
			args = "ldc=blockEvents"
		)
	)
	private void swapTickTaskBlockEvents(BooleanSupplier hasTimeLeft, CallbackInfo ci) {
		rsmm$swapTickTask(TickTask.BLOCK_EVENTS);
	}

	@Inject(
		method = "tick",
		at = @At(
			value = "INVOKE_STRING",
			target = "Lnet/minecraft/util/profiling/ProfilerFiller;popPush(Ljava/lang/String;)V",
			args = "ldc=entities"
		)
	)
	private void swapTickTaskEntities(BooleanSupplier hasTimeLeft, CallbackInfo ci) {
		rsmm$swapTickTask(TickTask.ENTITIES);
	}

	@Inject(
		method = "tick",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/server/level/ServerLevel;tickBlockEntities()V"
		)
	)
	private void endTickTaskEntities(BooleanSupplier hasTimeLeft, CallbackInfo ci) {
		rsmm$endTickTask();
	}

	@Inject(
		method = "tick",
		at = @At(
			value = "TAIL"
		)
	)
	private void endTickTaskTickLevel(BooleanSupplier hasTimeLeft, CallbackInfo ci) {
		rsmm$endTickTask();
	}

	@Inject(
		method = "tickChunk",
		at = @At(
			value = "HEAD"
		)
	)
	private void startTickTaskTickChunk(LevelChunk chunk, int randomTicks, CallbackInfo ci) {
		rsmm$startTickTask(false, TickTask.TICK_CHUNK);
	}

	@Inject(
		method = "tickChunk",
		at = @At(
			value = "INVOKE_STRING",
			target = "Lnet/minecraft/util/profiling/ProfilerFiller;push(Ljava/lang/String;)V",
			args = "ldc=thunder"
		)
	)
	private void startTickTaskThunder(LevelChunk chunk, int randomTicks, CallbackInfo ci) {
		rsmm$startTickTask(false, TickTask.THUNDER);
	}

	@Inject(
		method = "tickChunk",
		at = @At(
			value = "INVOKE_STRING",
			target = "Lnet/minecraft/util/profiling/ProfilerFiller;popPush(Ljava/lang/String;)V",
			args = "ldc=iceandsnow"
		)
	)
	private void swapTickTaskPrecipitation(LevelChunk chunk, int randomTicks, CallbackInfo ci) {
		rsmm$swapTickTask(false, TickTask.PRECIPITATION);
	}

	@Inject(
		method = "tickChunk",
		at = @At(
			value = "INVOKE_STRING",
			target = "Lnet/minecraft/util/profiling/ProfilerFiller;popPush(Ljava/lang/String;)V",
			args = "ldc=tickBlocks"
		)
	)
	private void swapTickTaskRandomTicks(LevelChunk chunk, int randomTicks, CallbackInfo ci) {
		rsmm$swapTickTask(false, TickTask.RANDOM_TICKS);
	}

	@Inject(
		method = "tickChunk",
		locals = LocalCapture.CAPTURE_FAILHARD,
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/level/material/FluidState;randomTick(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Ljava/util/Random;)V"
		)
	)
	private void logFluidRandomTick(LevelChunk chunk, int randomTicks, CallbackInfo ci, ChunkPos chunkPos, boolean isRaining, int minX, int minZ, ProfilerFiller profiler, LevelChunkSection[] sections, int sectionIndex, int maxSectionIndex, LevelChunkSection section, int bottomY, int attempt, BlockPos randomTickPos) {
		getMultimeter().logRandomTick(this, randomTickPos);
	}

	@Inject(
		method = "tickChunk",
		at = @At(
			value = "TAIL"
		)
	)
	private void endTickTaskRandomTicksAndTickChunk(LevelChunk chunk, int randomTicks, CallbackInfo ci) {
		rsmm$endTickTask(false);
		rsmm$endTickTask(false);
	}

	@Inject(
		method = "tickLiquid",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/level/material/FluidState;tick(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;)V"
		)
	)
	private void logFluidTick(TickNextTickData<Fluid> scheduledTick, CallbackInfo ci) {
		getMultimeter().logScheduledTick(this, scheduledTick.pos, scheduledTick.priority, false);
	}

	@Inject(
		method = "tickBlock",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/level/block/state/BlockState;tick(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Ljava/util/Random;)V"
		)
	)
	private void logBlockTick(TickNextTickData<Block> scheduledTick, CallbackInfo ci) {
		getMultimeter().logScheduledTick(this, scheduledTick.pos, scheduledTick.priority, false);
	}

	@Inject(
		method = "tickNonPassenger",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/entity/Entity;tick()V"
		)
	)
	private void logEntityTick(Entity entity, CallbackInfo ci) {
		getMultimeter().logEntityTick(this, entity);
	}

	@Inject(
		method = "blockEvent",
		at = @At(
			value = "HEAD"
		)
	)
	private void onBlockEvent(BlockPos pos, Block block, int type, int data, CallbackInfo ci) {
		rsmm$queueSize = blockEvents.size();
	}

	@Inject(
		method = "blockEvent",
		at = @At(
			value = "TAIL"
		)
	)
	private void postBlockEvent(BlockPos pos, Block block, int type, int data, CallbackInfo ci) {
		// The queue is a set; only one block event for the same block and position
		// can exist at any time. To check whether the block event was added, we
		// check the queue size before and after the new block event was offered.
		if (rsmm$queueSize < blockEvents.size()) {
			getMultimeter().logBlockEvent(this, pos, type, rsmm$currentDepth + 1, true);
		}
	}

	@Inject(
		method = "runBlockEvents",
		at = @At(
			value = "HEAD"
		)
	)
	private void onRunBlockEvents(CallbackInfo ci) {
		rsmm$currentDepth = 0;
		rsmm$currentBatch = blockEvents.size();
	}

	@Inject(
		method = "runBlockEvents",
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
		method = "runBlockEvents",
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
			target = "Lnet/minecraft/world/level/block/state/BlockState;triggerEvent(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;II)Z"
		)
	)
	private void logBlockEvent(BlockEventData blockEvent, CallbackInfoReturnable<Boolean> cir) {
		getMultimeter().logBlockEvent(this, blockEvent.getPos(), blockEvent.getParamA(), rsmm$currentDepth, false);
	}

	@Override
	public MultimeterServer getMultimeterServer() {
		return ((IMinecraftServer)server).getMultimeterServer();
	}
}
