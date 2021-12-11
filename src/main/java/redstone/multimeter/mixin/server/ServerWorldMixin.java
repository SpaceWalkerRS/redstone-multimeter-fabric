package redstone.multimeter.mixin.server;

import java.util.Iterator;

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
import net.minecraft.block.BlockState;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.BlockAction;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.TickableEntry;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.world.World;
import net.minecraft.world.WorldSaveHandler;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.dimension.Dimension;
import net.minecraft.world.level.LevelProperties;

import redstone.multimeter.common.TickTask;
import redstone.multimeter.interfaces.mixin.IMinecraftServer;
import redstone.multimeter.interfaces.mixin.IServerWorld;
import redstone.multimeter.server.MultimeterServer;

@Mixin(ServerWorld.class)
public abstract class ServerWorldMixin extends World implements IServerWorld {
	
	@Shadow @Final private MinecraftServer server;
	
	protected ServerWorldMixin(WorldSaveHandler saveHandler, LevelProperties properties, Dimension dimension, Profiler profiler, boolean client) {
		super(saveHandler, properties, dimension, profiler, client);
	}
	
	@Inject(
			method = "tick()V",
			at = @At(
					value = "INVOKE_STRING",
					target = "Lnet/minecraft/util/profiler/Profiler;push(Ljava/lang/String;)V",
					args = "ldc=mobSpawner"
			)
	)
	private void startTickTaskMobSpawning(CallbackInfo ci) {
		startTickTask(TickTask.MOB_SPAWNING);
	}
	
	@Inject(
			method = "tick()V",
			at = @At(
					value = "INVOKE_STRING",
					target = "Lnet/minecraft/util/profiler/Profiler;swap(Ljava/lang/String;)V",
					args = "ldc=chunkSource"
			)
	)
	private void swapTickTaskChunkSource(CallbackInfo ci) {
		swapTickTask(TickTask.CHUNK_SOURCE);
	}
	
	@Inject(
			method = "tick()V",
			at = @At(
					value = "INVOKE",
					shift = Shift.AFTER,
					target = "Lnet/minecraft/world/level/LevelProperties;setTime(J)V"
			)
	)
	private void onTickTime(CallbackInfo ci) {
		if (dimension.getType() == 0) {
			getMultimeterServer().onOverworldTickTime();
		}
	}
	
	@Inject(
			method = "tick()V",
			at = @At(
					value = "INVOKE_STRING",
					target = "Lnet/minecraft/util/profiler/Profiler;swap(Ljava/lang/String;)V",
					args = "ldc=tickPending"
			)
	)
	private void swapTickTaskScheduledTicks(CallbackInfo ci) {
		swapTickTask(TickTask.SCHEDULED_TICKS);
	}
	
	@Inject(
			method = "tick()V",
			at = @At(
					value = "INVOKE_STRING",
					target = "Lnet/minecraft/util/profiler/Profiler;swap(Ljava/lang/String;)V",
					args = "ldc=tickBlocks"
			)
	)
	private void swapTickTaskTickChunks(CallbackInfo ci) {
		swapTickTask(TickTask.TICK_CHUNKS);
	}
	
	@Inject(
			method = "tick()V",
			at = @At(
					value = "INVOKE_STRING",
					target = "Lnet/minecraft/util/profiler/Profiler;swap(Ljava/lang/String;)V",
					args = "ldc=chunkMap"
			)
	)
	private void swapTickTaskChunkMap(CallbackInfo ci) {
		swapTickTask(TickTask.CHUNK_MAP);
	}
	
	@Inject(
			method = "tick()V",
			at = @At(
					value = "INVOKE_STRING",
					target = "Lnet/minecraft/util/profiler/Profiler;swap(Ljava/lang/String;)V",
					args = "ldc=village"
			)
	)
	private void swapTickTaskVillages(CallbackInfo ci) {
		swapTickTask(TickTask.VILLAGES);
	}
	
	@Inject(
			method = "tick()V",
			at = @At(
					value = "INVOKE_STRING",
					target = "Lnet/minecraft/util/profiler/Profiler;swap(Ljava/lang/String;)V",
					args = "ldc=portalForcer"
			)
	)
	private void swapTickTaskPortals(CallbackInfo ci) {
		swapTickTask(TickTask.PORTALS);
	}
	
	@Inject(
			method = "tick()V",
			at = @At(
					value = "INVOKE",
					shift = Shift.BEFORE,
					target = "Lnet/minecraft/server/world/ServerWorld;method_6046()V"
			)
	)
	private void swapTickTaskBlockEvents(CallbackInfo ci) {
		swapTickTask(TickTask.BLOCK_EVENTS);
	}
	
	@Inject(
			method = "tick()V",
			at = @At(
					value = "RETURN"
			)
	)
	private void endTickTaskBlockEvents(CallbackInfo ci) {
		endTickTask();
	}
	
	@Inject(
			method = "method_6048",
			at = @At(
					value = "HEAD"
			)
	)
	private void startTickTaskWakeSleepingPlayers(CallbackInfo ci) {
		startTickTask(TickTask.WAKE_SLEEPING_PLAYERS);
	}
	
	@Inject(
			method = "method_6048",
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
					value = "INVOKE",
					ordinal = 1,
					target = "Ljava/util/Set;iterator()Ljava/util/Iterator;"
			)
	)
	private void startDummyTask(CallbackInfo ci) {
		startTickTask(TickTask.UNKNOWN);
	}
	
	@Inject(
			method = "tickBlocks",
			at = @At(
					value = "INVOKE",
					ordinal = 1,
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
			method = "tickBlocks",
			locals = LocalCapture.CAPTURE_FAILHARD,
			at = @At(
					value = "INVOKE",
					shift = Shift.BEFORE,
					target = "Lnet/minecraft/block/Block;onUpdateTick(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;Ljava/util/Random;)V"
			)
	)
	private void onRandomTick(CallbackInfo ci, int attemps, int succeeded, Iterator<Chunk> it, ChunkPos chunkPos, int chunkX, int chunkZ, Chunk chunk, int o, ChunkSection[] sections, int p, int q, ChunkSection section, int r, int s, int dx, int dz, int dy, BlockState state, Block block) {
		int x = chunkX + dx;
		int y = section.method_1432() + dy;
		int z = chunkZ + dz;
		BlockPos pos = new BlockPos(x, y, z);
		
		getMultimeter().logRandomTick((World)(Object)this, pos);
	}
	
	@Inject(
			method = "method_340(Z)Z",
			locals = LocalCapture.CAPTURE_FAILHARD,
			at = @At(
					value = "INVOKE",
					shift = Shift.BEFORE,
					target = "Lnet/minecraft/block/Block;scheduledTick(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;Ljava/util/Random;)V"
			)
	)
	private void onScheduledTick(boolean bl, CallbackInfoReturnable<Boolean> cir, Iterator<TickableEntry> it, TickableEntry scheduledTick) {
		getMultimeter().logScheduledTick((World)(Object)this, scheduledTick);
	}
	
	@Inject(
			method = "method_6034",
			at = @At(
					value = "INVOKE",
					shift = Shift.BEFORE,
					target = "Lnet/minecraft/block/Block;onEvent(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;II)Z"
			)
	)
	private void onProcessBlockEvent(BlockAction blockEvent, CallbackInfoReturnable<Boolean> cir) {
		getMultimeter().logBlockEvent((World)(Object)this, blockEvent);
	}
	
	@Inject(
			method = "tickWeather",
			at = @At(
					value = "HEAD"
			)
	)
	private void startTickTaskWeather(CallbackInfo ci) {
		startTickTask(TickTask.WEATHER);
	}
	
	@Inject(
			method = "tickWeather",
			at = @At(
					value = "RETURN"
			)
	)
	private void endTickTaskWeather(CallbackInfo ci) {
		endTickTask();
	}
	
	@Override
	public MultimeterServer getMultimeterServer() {
		return ((IMinecraftServer)server).getMultimeterServer();
	}
}
