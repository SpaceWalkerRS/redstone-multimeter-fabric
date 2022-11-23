package redstone.multimeter.mixin.common;

import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.fluid.Fluid;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.BlockEvent;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.world.MutableWorldProperties;
import net.minecraft.world.World;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.tick.OrderedTick;
import net.minecraft.world.tick.WorldTickScheduler;

import redstone.multimeter.common.TickTask;
import redstone.multimeter.interfaces.mixin.IMinecraftServer;
import redstone.multimeter.interfaces.mixin.IServerWorld;
import redstone.multimeter.interfaces.mixin.IWorldTickScheduler;
import redstone.multimeter.server.MultimeterServer;

@Mixin(ServerWorld.class)
public abstract class ServerWorldMixin extends World implements IServerWorld {
	
	@Shadow @Final private MinecraftServer server;
	@Shadow @Final private WorldTickScheduler<Block> blockTickScheduler;
	@Shadow @Final private WorldTickScheduler<Fluid> fluidTickScheduler;
	@Shadow @Final private ObjectLinkedOpenHashSet<BlockEvent> syncedBlockEventQueue;
	@Shadow @Final private boolean shouldTickTime;
	
	private OrderedTick<?> scheduledTickRSMM;
	private int queueSize;
	private int currentDepth;
	private int currentBatch;
	
	private ServerWorldMixin(MutableWorldProperties properties, RegistryKey<World> registryRef, RegistryEntry<DimensionType> dimension, Supplier<Profiler> profiler, boolean isClient, boolean debugWorld, long seed, int maxChainedNeighborUpdates) {
		super(properties, registryRef, dimension, profiler, isClient, debugWorld, seed, maxChainedNeighborUpdates);
	}
	
	@Inject(
			method = "<init>",
			at = @At(
					value = "RETURN"
			)
	)
	private void setTickConsumers(CallbackInfo ci) {
		((IWorldTickScheduler)blockTickScheduler).setTickScheduleConsumerRSMM(scheduledTick -> {
			getMultimeter().logScheduledTick((World)(Object)this, scheduledTick.pos(), scheduledTick.priority(), true);
		});
		((IWorldTickScheduler)blockTickScheduler).setTickExecutionConsumerRSMM(scheduledTick -> {
			this.scheduledTickRSMM = scheduledTick;
		});
		((IWorldTickScheduler)fluidTickScheduler).setTickScheduleConsumerRSMM(scheduledTick -> {
			getMultimeter().logScheduledTick((World)(Object)this, scheduledTick.pos(), scheduledTick.priority(), true);
		});
		((IWorldTickScheduler)fluidTickScheduler).setTickExecutionConsumerRSMM(scheduledTick -> {
			this.scheduledTickRSMM = scheduledTick;
		});
	}
	
	@Inject(
			method = "tick",
			at = @At(
					value = "HEAD"
			)
	)
	private void startTickTaskTickWorld(BooleanSupplier isAheadOfTime, CallbackInfo ci) {
		startTickTaskRSMM(TickTask.TICK_WORLD, getRegistryKey().getValue().toString());
	}
	
	@Inject(
			method = "tick",
			at = @At(
					value = "INVOKE_STRING",
					target = "Lnet/minecraft/util/profiler/Profiler;push(Ljava/lang/String;)V",
					args = "ldc=world border"
			)
	)
	private void startTickTaskWorldBorder(BooleanSupplier isAheadOfTime, CallbackInfo ci) {
		startTickTaskRSMM(TickTask.WORLD_BORDER);
	}
	
	@Inject(
			method = "tick",
			at = @At(
					value = "INVOKE_STRING",
					target = "Lnet/minecraft/util/profiler/Profiler;swap(Ljava/lang/String;)V",
					args = "ldc=weather"
			)
	)
	private void swapTickTaskWeather(BooleanSupplier isAheadOfTime, CallbackInfo ci) {
		swapTickTaskRSMM(TickTask.WEATHER);
	}
	
	@Inject(
			method = "tick",
			at = @At(
					value = "INVOKE_STRING",
					target = "Lnet/minecraft/util/profiler/Profiler;swap(Ljava/lang/String;)V",
					args = "ldc=tickPending"
			)
	)
	private void swapTickTaskScheduledTicks(BooleanSupplier isAheadOfTime, CallbackInfo ci) {
		swapTickTaskRSMM(TickTask.SCHEDULED_TICKS);
	}
	
	@Inject(
			method = "tick",
			at = @At(
					value = "INVOKE_STRING",
					target = "Lnet/minecraft/util/profiler/Profiler;push(Ljava/lang/String;)V",
					args = "ldc=blockTicks"
			)
	)
	private void startTickTaskBlockTicks(BooleanSupplier isAheadOfTime, CallbackInfo ci) {
		startTickTaskRSMM(TickTask.BLOCK_TICKS);
	}
	
	@Inject(
			method = "tick",
			at = @At(
					value = "INVOKE_STRING",
					target = "Lnet/minecraft/util/profiler/Profiler;swap(Ljava/lang/String;)V",
					args = "ldc=fluidTicks"
			)
	)
	private void swapTickTaskFluidTicks(BooleanSupplier isAheadOfTime, CallbackInfo ci) {
		swapTickTaskRSMM(TickTask.FLUID_TICKS);
	}
	
	@Inject(
			method = "tick",
			slice = @Slice(
					from = @At(
							value = "INVOKE_STRING",
							target = "Lnet/minecraft/util/profiler/Profiler;swap(Ljava/lang/String;)V",
							args = "ldc=fluidTicks"
					)
			),
			at = @At(
					value = "INVOKE",
					ordinal = 0,
					target = "Lnet/minecraft/util/profiler/Profiler;pop()V"
			)
	)
	private void endTickTaskFluidTicks(BooleanSupplier isAheadOfTime, CallbackInfo ci) {
		endTickTaskRSMM();
	}
	
	@Inject(
			method = "tick",
			at = @At(
					value = "INVOKE_STRING",
					target = "Lnet/minecraft/util/profiler/Profiler;swap(Ljava/lang/String;)V",
					args = "ldc=raid"
			)
	)
	private void swapTickTaskRaids(BooleanSupplier isAheadOfTime, CallbackInfo ci) {
		swapTickTaskRSMM(TickTask.RAIDS);
	}
	
	@Inject(
			method = "tick",
			at = @At(
					value = "INVOKE_STRING",
					target = "Lnet/minecraft/util/profiler/Profiler;swap(Ljava/lang/String;)V",
					args = "ldc=chunkSource"
			)
	)
	private void swapTickTaskChunkSource(BooleanSupplier isAheadOfTime, CallbackInfo ci) {
		swapTickTaskRSMM(TickTask.CHUNK_SOURCE);
	}
	
	@Inject(
			method = "tick",
			at = @At(
					value = "INVOKE_STRING",
					target = "Lnet/minecraft/util/profiler/Profiler;swap(Ljava/lang/String;)V",
					args = "ldc=blockEvents"
			)
	)
	private void swapTickTaskBlockEvents(BooleanSupplier isAheadOfTime, CallbackInfo ci) {
		swapTickTaskRSMM(TickTask.BLOCK_EVENTS);
	}
	
	@Inject(
			method = "tick",
			slice = @Slice(
					from = @At(
							value = "INVOKE_STRING",
							target = "Lnet/minecraft/util/profiler/Profiler;swap(Ljava/lang/String;)V",
							args = "ldc=blockEvents"
					)
			),
			at = @At(
					value = "INVOKE",
					ordinal = 0,
					target = "Lnet/minecraft/util/profiler/Profiler;pop()V"
			)
	)
	private void endTickTaskBlockEvents(BooleanSupplier isAheadOfTime, CallbackInfo ci) {
		endTickTaskRSMM();
	}
	
	@Inject(
			method = "tick",
			at = @At(
					value = "INVOKE_STRING",
					target = "Lnet/minecraft/util/profiler/Profiler;push(Ljava/lang/String;)V",
					args = "ldc=entities"
			)
	)
	private void startTickTaskEntities(BooleanSupplier isAheadOfTime, CallbackInfo ci) {
		startTickTaskRSMM(TickTask.ENTITIES);
	}
	
	@Inject(
			method = "tick",
			at = @At(
					value = "INVOKE",
					shift = Shift.BEFORE,
					target = "Lnet/minecraft/server/world/ServerWorld;tickBlockEntities()V"
			)
	)
	private void endTickTaskEntities(BooleanSupplier isAheadOfTime, CallbackInfo ci) {
		endTickTaskRSMM();
	}
	
	@Inject(
			method = "tick",
			at = @At(
					value = "INVOKE_STRING",
					target = "Lnet/minecraft/util/profiler/Profiler;push(Ljava/lang/String;)V",
					args = "ldc=entityManagement"
			)
	)
	private void startTickTaskEntityManagement(BooleanSupplier isAheadOfTime, CallbackInfo ci) {
		startTickTaskRSMM(TickTask.ENTITY_MANAGEMENT);
	}
	
	@Inject(
			method = "tick",
			slice = @Slice(
					from = @At(
							value = "INVOKE_STRING",
							target = "Lnet/minecraft/util/profiler/Profiler;push(Ljava/lang/String;)V",
							args = "ldc=entityManagement"
					)
			),
			at = @At(
					value = "INVOKE",
					ordinal = 0,
					target = "Lnet/minecraft/util/profiler/Profiler;pop()V"
			)
	)
	private void endTickTaskEntityManagement(BooleanSupplier isAheadOfTime, CallbackInfo ci) {
		endTickTaskRSMM();
	}
	
	@Inject(
			method = "tick",
			at = @At(
					value = "RETURN"
			)
	)
	private void endTickTaskTickWorld(BooleanSupplier isAheadOfTime, CallbackInfo ci) {
		endTickTaskRSMM();
	}
	
	@Inject(
			method = "tickTime",
			at = @At(
					value = "HEAD"
			)
	)
	private void beforeTickTime(CallbackInfo ci) {
		if (shouldTickTime) {
			swapTickTaskRSMM(TickTask.TICK_TIME);
		}
	}
	
	@Inject(
			method = "tickTime",
			at = @At(
					value = "RETURN"
			)
	)
	private void onTickTime(CallbackInfo ci) {
		if (shouldTickTime) {
			getMultimeterServer().onOverworldTickTime();
		}
	}
	
	@Inject(
			method = "tickSpawners",
			at = @At(
					value = "HEAD"
			)
	)
	private void startTickTaskCustomMobSpawning(boolean spawnMonsters, boolean spawnAnimals, CallbackInfo ci) {
		startTickTaskRSMM(TickTask.CUSTOM_MOB_SPAWNING);
	}
	
	@Inject(
			method = "tickSpawners",
			at = @At(
					value = "RETURN"
			)
	)
	private void endTickTaskCustomMobSpawning(boolean spawnMonsters, boolean spawnAnimals, CallbackInfo ci) {
		endTickTaskRSMM();
	}
	
	@Inject(
			method = "wakeSleepingPlayers",
			at = @At(
					value = "HEAD"
			)
	)
	private void startTickTaskWakeSleepingPlayers(CallbackInfo ci) {
		startTickTaskRSMM(TickTask.WAKE_SLEEPING_PLAYERS);
	}
	
	@Inject(
			method = "wakeSleepingPlayers",
			at = @At(
					value = "RETURN"
			)
	)
	private void endTickTaskWakeSleepingPlayers(CallbackInfo ci) {
		endTickTaskRSMM();
	}
	
	@Inject(
			method = "tickChunk",
			at = @At(
					value = "HEAD"
			)
	)
	private void startTickTaskTickChunk(WorldChunk chunk, int randomTickSpeed, CallbackInfo ci) {
		startTickTaskRSMM(false, TickTask.TICK_CHUNK);
	}
	
	@Inject(
			method = "tickChunk",
			at = @At(
					value = "INVOKE_STRING",
					target = "Lnet/minecraft/util/profiler/Profiler;push(Ljava/lang/String;)V",
					args = "ldc=thunder"
			)
	)
	private void startTickTaskThunder(WorldChunk chunk, int randomTickSpeed, CallbackInfo ci) {
		startTickTaskRSMM(false, TickTask.THUNDER);
	}
	
	@Inject(
			method = "tickChunk",
			at = @At(
					value = "INVOKE_STRING",
					target = "Lnet/minecraft/util/profiler/Profiler;swap(Ljava/lang/String;)V",
					args = "ldc=iceandsnow"
			)
	)
	private void swapTickTaskPrecipitation(WorldChunk chunk, int randomTickSpeed, CallbackInfo ci) {
		swapTickTaskRSMM(false, TickTask.PRECIPITATION);
	}
	
	@Inject(
			method = "tickChunk",
			at = @At(
					value = "INVOKE_STRING",
					target = "Lnet/minecraft/util/profiler/Profiler;swap(Ljava/lang/String;)V",
					args = "ldc=tickBlocks"
			)
	)
	private void swapTickTaskRandomTicks(WorldChunk chunk, int randomTickSpeed, CallbackInfo ci) {
		swapTickTaskRSMM(false, TickTask.RANDOM_TICKS);
	}
	
	@Inject(
			method = "tickChunk",
			at = @At(
					value = "RETURN"
			)
	)
	private void endTickTaskRandomTicksAndTickChunk(WorldChunk chunk, int randomTickSpeed, CallbackInfo ci) {
		endTickTaskRSMM(false);
		endTickTaskRSMM(false);
	}
	
	@Inject(
			method = "tickFluid",
			at = @At(
					value = "INVOKE",
					shift = Shift.BEFORE,
					target = "Lnet/minecraft/fluid/FluidState;onScheduledTick(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;)V"
			)
	)
	private void onTickFluid(BlockPos pos, Fluid fluid, CallbackInfo ci) {
		logCurrentScheduledTickRSMM();
	}
	
	@Inject(
			method = "tickBlock",
			at = @At(
					value = "INVOKE",
					shift = Shift.BEFORE,
					target = "Lnet/minecraft/block/BlockState;scheduledTick(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/math/random/Random;)V"
			)
	)
	private void onTickBlock(BlockPos pos, Block block, CallbackInfo ci) {
		logCurrentScheduledTickRSMM();
	}
	
	@Inject(
			method = "tickEntity",
			at = @At(
					value = "INVOKE",
					shift = Shift.BEFORE,
					target = "Lnet/minecraft/entity/Entity;tick()V"
			)
	)
	private void onTickEntity(Entity entity, CallbackInfo ci) {
		getMultimeter().logEntityTick((World)(Object)this, entity);
	}
	
	@Inject(
			method = "addSyncedBlockEvent",
			at = @At(
					value = "HEAD"
			)
	)
	private void onAddBlockEvent(BlockPos pos, Block block, int type, int data, CallbackInfo ci) {
		queueSize = syncedBlockEventQueue.size();
	}
	
	@Inject(
			method = "addSyncedBlockEvent",
			at = @At(
					value = "RETURN"
			)
	)
	private void postAddBlockEvent(BlockPos pos, Block block, int type, int data, CallbackInfo ci) {
		// The queue is a set; only one block event for the same block and position
		// can exist at any time. To check whether the block event was added, we
		// check the queue size before and after the new block event was offered.
		if (queueSize < syncedBlockEventQueue.size()) {
			getMultimeter().logBlockEvent((World)(Object)this, pos, type, currentDepth + 1, true);
		}
	}
	
	@Inject(
			method = "processSyncedBlockEvents",
			at = @At(
					value = "HEAD"
			)
	)
	private void onProcessBlockEvents(CallbackInfo ci) {
		currentDepth = 0;
		currentBatch = syncedBlockEventQueue.size();
	}
	
	@Inject(
			method = "processSyncedBlockEvents",
			at = @At(
					value = "INVOKE",
					target = "Lit/unimi/dsi/fastutil/objects/ObjectLinkedOpenHashSet;isEmpty()Z"
			)
	)
	private void onNextBlockEvent(CallbackInfo ci) {
		if (currentBatch == 0) {
			currentDepth++;
			currentBatch = syncedBlockEventQueue.size();
		}
		
		currentBatch--;
	}
	
	@Inject(
			method = "processSyncedBlockEvents",
			at = @At(
					value = "RETURN"
			)
	)
	private void postProcessBlockEvents(CallbackInfo ci) {
		currentDepth = -1;
		currentBatch = 0;
	}
	
	@Inject(
			method = "processBlockEvent",
			at = @At(
					value = "INVOKE",
					shift = Shift.BEFORE,
					target = "Lnet/minecraft/block/BlockState;onSyncedBlockEvent(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;II)Z"
			)
	)
	private void onProcessBlockEvent(BlockEvent blockEvent, CallbackInfoReturnable<Boolean> cir) {
		getMultimeter().logBlockEvent((World)(Object)this, blockEvent.pos(), blockEvent.type(), currentDepth, false);
	}
	
	@Override
	public MultimeterServer getMultimeterServer() {
		return ((IMinecraftServer)server).getMultimeterServer();
	}
	
	private void logCurrentScheduledTickRSMM() {
		if (scheduledTickRSMM != null) {
			getMultimeter().logScheduledTick((World)(Object)this, scheduledTickRSMM.pos(), scheduledTickRSMM.priority(), false);
			scheduledTickRSMM = null;
		}
	}
}
