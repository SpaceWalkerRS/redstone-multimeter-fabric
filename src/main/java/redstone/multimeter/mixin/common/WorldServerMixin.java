package redstone.multimeter.mixin.common;

import java.util.Iterator;
import java.util.Set;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.block.Block;
import net.minecraft.block.BlockEventData;
import net.minecraft.profiler.Profiler;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.DimensionType;
import net.minecraft.world.NextTickListEntry;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.storage.WorldInfo;

import redstone.multimeter.common.TickTask;
import redstone.multimeter.interfaces.mixin.IMinecraftServer;
import redstone.multimeter.interfaces.mixin.IWorldServer;
import redstone.multimeter.server.MultimeterServer;
import redstone.multimeter.util.DimensionUtils;

@Mixin(WorldServer.class)
public abstract class WorldServerMixin extends World implements IWorldServer {
	
	@Shadow @Final private MinecraftServer server;
	@Shadow @Final private Set<NextTickListEntry> pendingTickListEntriesHashSet;
	
	private int queueSize;
	private int currentDepth;
	
	protected WorldServerMixin(ISaveHandler saveHandlerIn, WorldInfo info, WorldProvider providerIn, Profiler profilerIn, boolean client) {
		super(saveHandlerIn, info, providerIn, profilerIn, client);
	}
	
	@Inject(
			method = "tick",
			at = @At(
					value = "HEAD"
			)
	)
	private void startTickTaskTickWorld(CallbackInfo ci) {
		startTickTaskRSMM(TickTask.TICK_WORLD, DimensionUtils.getId(provider.getDimensionType()).toString());
	}
	
	@Inject(
			method = "tick",
			at = @At(
					value = "INVOKE_STRING",
					target = "Lnet/minecraft/profiler/Profiler;startSection(Ljava/lang/String;)V",
					args = "ldc=mobSpawner"
			)
	)
	private void startTickTaskMobSpawning(CallbackInfo ci) {
		startTickTaskRSMM(TickTask.MOB_SPAWNING);
	}
	
	@Inject(
			method = "tick",
			at = @At(
					value = "INVOKE_STRING",
					target = "Lnet/minecraft/profiler/Profiler;endStartSection(Ljava/lang/String;)V",
					args = "ldc=chunkSource"
			)
	)
	private void swapTickTaskChunkSource(CallbackInfo ci) {
		swapTickTaskRSMM(TickTask.CHUNK_SOURCE);
	}
	
	@Inject(
			method = "tick",
			at = @At(
					value = "INVOKE",
					shift = Shift.BEFORE,
					target = "Lnet/minecraft/world/storage/WorldInfo;setWorldTotalTime(J)V"
			)
	)
	private void startTickTaskTickTime(CallbackInfo ci) {
		if (provider.getDimensionType() == DimensionType.OVERWORLD) {
			swapTickTaskRSMM(TickTask.TICK_TIME);
		}
	}
	
	@Inject(
			method = "tick",
			at = @At(
					value = "INVOKE",
					shift = Shift.AFTER,
					target = "Lnet/minecraft/world/storage/WorldInfo;setWorldTotalTime(J)V"
			)
	)
	private void afterTickTime(CallbackInfo ci) {
		if (provider.getDimensionType() == DimensionType.OVERWORLD) {
			getMultimeterServer().onOverworldTickTime();
		}
	}
	
	@Inject(
			method = "tick",
			at = @At(
					value = "INVOKE_STRING",
					target = "Lnet/minecraft/profiler/Profiler;endStartSection(Ljava/lang/String;)V",
					args = "ldc=tickPending"
			)
	)
	private void swapTickTaskScheduledTicks(CallbackInfo ci) {
		swapTickTaskRSMM(TickTask.SCHEDULED_TICKS);
	}
	
	@Inject(
			method = "tick",
			at = @At(
					value = "INVOKE_STRING",
					target = "Lnet/minecraft/profiler/Profiler;endStartSection(Ljava/lang/String;)V",
					args = "ldc=tickBlocks"
			)
	)
	private void swapTickTaskTickChunks(CallbackInfo ci) {
		swapTickTaskRSMM(TickTask.TICK_CHUNKS);
	}
	
	@Inject(
			method = "tick",
			at = @At(
					value = "INVOKE_STRING",
					target = "Lnet/minecraft/profiler/Profiler;endStartSection(Ljava/lang/String;)V",
					args = "ldc=chunkMap"
			)
	)
	private void swapTickTaskChunkMap(CallbackInfo ci) {
		swapTickTaskRSMM(TickTask.CHUNK_MAP);
	}
	
	@Inject(
			method = "tick",
			at = @At(
					value = "INVOKE_STRING",
					target = "Lnet/minecraft/profiler/Profiler;endStartSection(Ljava/lang/String;)V",
					args = "ldc=village"
			)
	)
	private void swapTickTaskVillages(CallbackInfo ci) {
		swapTickTaskRSMM(TickTask.VILLAGES);
	}
	
	@Inject(
			method = "tick",
			at = @At(
					value = "INVOKE_STRING",
					target = "Lnet/minecraft/profiler/Profiler;endStartSection(Ljava/lang/String;)V",
					args = "ldc=portalForcer"
			)
	)
	private void swapTickTaskPortals(CallbackInfo ci) {
		swapTickTaskRSMM(TickTask.PORTALS);
	}
	
	@Inject(
			method = "tick",
			at = @At(
					value = "INVOKE",
					shift = Shift.BEFORE,
					target = "Lnet/minecraft/world/WorldServer;sendQueuedBlockEvents()V"
			)
	)
	private void swapTickTaskBlockEvents(CallbackInfo ci) {
		swapTickTaskRSMM(TickTask.BLOCK_EVENTS);
	}
	
	@Inject(
			method = "tick",
			at = @At(
					value = "RETURN"
			)
	)
	private void endTickTaskBlockEventsAndTickWorld(CallbackInfo ci) {
		endTickTaskRSMM();
		endTickTaskRSMM();
	}
	
	@Inject(
			method = "wakeAllPlayers",
			at = @At(
					value = "HEAD"
			)
	)
	private void startTickTaskWakePlayers(CallbackInfo ci) {
		startTickTaskRSMM(TickTask.WAKE_SLEEPING_PLAYERS);
	}
	
	@Inject(
			method = "wakeAllPlayers",
			at = @At(
					value = "RETURN"
			)
	)
	private void endTickTaskWakePlayers(CallbackInfo ci) {
		endTickTaskRSMM();
	}
	
	@Inject(
			method = "updateBlocks",
			at = @At(
					value = "INVOKE_STRING",
					target = "Lnet/minecraft/profiler/Profiler;startSection(Ljava/lang/String;)V",
					args = "ldc=pollingChunks"
			)
	)
	private void startDummyTask(CallbackInfo ci) {
		startTickTaskRSMM(false, TickTask.TICK_CHUNK);
	}
	
	@Inject(
			method = "updateBlocks",
			at = @At(
					value = "INVOKE",
					target = "Ljava/util/Iterator;hasNext()Z"
			)
	)
	private void endTickTaskRandomTicks(CallbackInfo ci) {
		endTickTaskRSMM(false);
	}
	
	@Inject(
			method = "updateBlocks",
			at = @At(
					value = "INVOKE_STRING",
					target = "Lnet/minecraft/profiler/Profiler;endStartSection(Ljava/lang/String;)V",
					args = "ldc=tickChunk"
			)
	)
	private void startTickTaskTickChunk(CallbackInfo ci) {
		startTickTaskRSMM(false, TickTask.THUNDER);
	}
	
	@Inject(
			method = "updateBlocks",
			at = @At(
					value = "INVOKE_STRING",
					target = "Lnet/minecraft/profiler/Profiler;endStartSection(Ljava/lang/String;)V",
					args = "ldc=thunder"
			)
	)
	private void swapTickTaskThunder(CallbackInfo ci) {
		swapTickTaskRSMM(false, TickTask.THUNDER);
	}
	
	@Inject(
			method = "updateBlocks",
			at = @At(
					value = "INVOKE_STRING",
					target = "Lnet/minecraft/profiler/Profiler;endStartSection(Ljava/lang/String;)V",
					args = "ldc=iceandsnow"
			)
	)
	private void swapTickTaskPrecipitation(CallbackInfo ci) {
		swapTickTaskRSMM(false, TickTask.PRECIPITATION);
	}
	
	@Inject(
			method = "updateBlocks",
			at = @At(
					value = "INVOKE_STRING",
					target = "Lnet/minecraft/profiler/Profiler;endStartSection(Ljava/lang/String;)V",
					args = "ldc=tickBlocks"
			)
	)
	private void swapTickTaskRandomTicks(CallbackInfo ci) {
		swapTickTaskRSMM(false, TickTask.RANDOM_TICKS);
	}
	
	@Inject(
			method = "updateBlocks",
			locals = LocalCapture.CAPTURE_FAILHARD,
			at = @At(
					value = "INVOKE",
					shift = Shift.BEFORE,
					target = "Lnet/minecraft/block/Block;randomTick(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/state/IBlockState;Ljava/util/Random;)V"
			)
	)
	private void onRandomTick(CallbackInfo ci, int randomTickSpeed, boolean isRaining, boolean isThundering, Iterator<Chunk> it, Chunk chunk, int chunkX, int chunkZ, ExtendedBlockStorage[] sections, int sectionIndex, int attempt, ExtendedBlockStorage section, int nextRandomTickSeed, int randomLocation, int sectionX, int sectionZ, int sectionY) {
		int x = chunkX + sectionX;
		int z = chunkZ + sectionZ;
		int y = section.getYLocation() + sectionY;
		BlockPos pos = new BlockPos(x, y, z);
		
		getMultimeter().logRandomTick((World)(Object)this, pos);
	}
	
	@Inject(
			method = "updateBlockTick",
			at = @At(
					value = "HEAD"
			)
	)
	private void onSchedule(BlockPos pos, Block block, int delay, int priority, CallbackInfo ci) {
		queueSize = pendingTickListEntriesHashSet.size();
	}

	@Inject(
			method = "updateBlockTick",
			at = @At(
					value = "RETURN"
			)
	)
	private void postSchedule(BlockPos pos, Block block, int delay, int priority, CallbackInfo ci) {
		if (queueSize < pendingTickListEntriesHashSet.size()) {
			getMultimeter().logScheduledTick((World)(Object)this, pos, priority, true);
		}
	}
	
	@Inject(
			method = "updateEntities",
			at = @At(
					value = "HEAD"
			)
	)
	private void startTickTaskEntities(CallbackInfo ci) {
		startTickTaskRSMM(TickTask.ENTITIES);
	}
	
	@Inject(
			method = "updateEntities",
			at = @At(
					value = "RETURN"
			)
	)
	private void endTickTaskEntities(CallbackInfo ci) {
		endTickTaskRSMM();
	}
	
	@Inject(
			method = "tickPlayers",
			at = @At(
					value = "HEAD"
			)
	)
	private void startTickTaskPlayers(CallbackInfo ci) {
		startTickTaskRSMM(TickTask.PLAYERS);
	}
	
	@Inject(
			method = "tickPlayers",
			at = @At(
					value = "RETURN"
			)
	)
	private void endTickTaskPlayers(CallbackInfo ci) {
		endTickTaskRSMM();
	}
	
	@Inject(
			method = "tickUpdates",
			locals = LocalCapture.CAPTURE_FAILHARD,
			at = @At(
					value = "INVOKE",
					shift = Shift.BEFORE,
					target = "Lnet/minecraft/block/Block;updateTick(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/state/IBlockState;Ljava/util/Random;)V"
			)
	)
	private void onScheduledTick(boolean runAllPending, CallbackInfoReturnable<Boolean> ci, int total, Iterator<NextTickListEntry> it, NextTickListEntry scheduledTick) {
		getMultimeter().logScheduledTick((World)(Object)this, scheduledTick.position, scheduledTick.priority, false);
	}
	
	@Inject(
			method = "addBlockEvent",
			at = @At(
					value = "TAIL"
			)
	)
	private void postAddBlockEvent(BlockPos pos, Block block, int type, int data, CallbackInfo ci) {
		getMultimeter().logBlockEvent((World)(Object)this, pos, type, currentDepth + 1, true);
	}
	
	@Inject(
			method = "sendQueuedBlockEvents",
			at = @At(
					value = "CONSTANT",
					args = "intValue=1"
			)
	)
	private void onNextBlockEvent(CallbackInfo ci) {
		currentDepth++;
	}
	
	@Inject(
			method = "sendQueuedBlockEvents",
			at = @At(
					value = "RETURN"
			)
	)
	private void onProcessBlockEvents(CallbackInfo ci) {
		currentDepth = -1;
	}
	
	@Inject(
			method = "fireBlockEvent",
			at = @At(
					value = "INVOKE",
					shift = Shift.BEFORE,
					target = "Lnet/minecraft/block/state/IBlockState;onBlockEventReceived(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;II)Z"
			)
	)
	private void onProcessBlockEvent(BlockEventData blockEvent, CallbackInfoReturnable<Boolean> cir) {
		getMultimeter().logBlockEvent((World)(Object)this, blockEvent.getPosition(), blockEvent.getEventID(), currentDepth, false);
	}
	
	@Override
	public MultimeterServer getMultimeterServer() {
		return ((IMinecraftServer)server).getMultimeterServer();
	}
}
