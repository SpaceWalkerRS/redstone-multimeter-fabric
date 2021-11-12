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

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.fluid.Fluid;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.BlockEvent;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.ScheduledTick;
import net.minecraft.world.World;
import net.minecraft.world.chunk.WorldChunk;

import redstone.multimeter.common.TickTask;
import redstone.multimeter.interfaces.mixin.IMinecraftServer;
import redstone.multimeter.interfaces.mixin.IServerWorld;
import redstone.multimeter.server.MultimeterServer;

@Mixin(ServerWorld.class)
public abstract class ServerWorldMixin implements IServerWorld {
	
	@Shadow @Final private MinecraftServer server;
	
	@Inject(
			method = "tick",
			at = @At(
					value = "INVOKE_STRING",
					target = "Lnet/minecraft/util/profiler/Profiler;push(Ljava/lang/String;)V",
					args = "ldc=world border"
			)
	)
	private void startTickPhaseWorldBorder(BooleanSupplier isAheadOfTime, CallbackInfo ci) {
		startTickTask(TickTask.WORLD_BORDER);
	}
	
	@Inject(
			method = "tick",
			at = @At(
					value = "INVOKE_STRING",
					target = "Lnet/minecraft/util/profiler/Profiler;swap(Ljava/lang/String;)V",
					args = "ldc=weather"
			)
	)
	private void swapTickPhaseWeather(BooleanSupplier isAheadOfTime, CallbackInfo ci) {
		swapTickTask(TickTask.WEATHER);
	}
	
	@Inject(
			method = "tick",
			at = @At(
					value = "INVOKE_STRING",
					target = "Lnet/minecraft/util/profiler/Profiler;swap(Ljava/lang/String;)V",
					args = "ldc=tickPending"
			)
	)
	private void swapTickPhaseScheduledTicks(BooleanSupplier isAheadOfTime, CallbackInfo ci) {
		swapTickTask(TickTask.SCHEDULED_TICKS);
	}
	
	@Inject(
			method = "tick",
			at = @At(
					value = "FIELD",
					shift = Shift.BEFORE,
					target = "Lnet/minecraft/server/world/ServerWorld;blockTickScheduler:Lnet/minecraft/server/world/ServerTickScheduler;"
			)
	)
	private void onTickPhaseScheduledBlockTicks(BooleanSupplier isAheadOfTime, CallbackInfo ci) {
		startTickTask(TickTask.BLOCK_TICKS);
	}
	
	@Inject(
			method = "tick",
			at = @At(
					value = "FIELD",
					shift = Shift.BEFORE,
					target = "Lnet/minecraft/server/world/ServerWorld;fluidTickScheduler:Lnet/minecraft/server/world/ServerTickScheduler;"
			)
	)
	private void swapTickPhaseScheduledFluidTicks(BooleanSupplier isAheadOfTime, CallbackInfo ci) {
		swapTickTask(TickTask.FLUID_TICKS);
	}
	
	@Inject(
			method = "tick",
			at = @At(
					value = "FIELD",
					shift = Shift.AFTER,
					target = "Lnet/minecraft/server/world/ServerWorld;fluidTickScheduler:Lnet/minecraft/server/world/ServerTickScheduler;"
			)
	)
	private void endTickPhaseScheduledFluidTicks(BooleanSupplier isAheadOfTime, CallbackInfo ci) {
		endTickTask();
	}
	
	@Inject(
			method = "tick",
			at = @At(
					value = "INVOKE_STRING",
					target = "Lnet/minecraft/util/profiler/Profiler;swap(Ljava/lang/String;)V",
					args = "ldc=raid"
			)
	)
	private void swapTickPhaseRaids(BooleanSupplier isAheadOfTime, CallbackInfo ci) {
		swapTickTask(TickTask.RAIDS);
	}
	
	@Inject(
			method = "tick",
			at = @At(
					value = "INVOKE_STRING",
					target = "Lnet/minecraft/util/profiler/Profiler;swap(Ljava/lang/String;)V",
					args = "ldc=chunkSource"
			)
	)
	private void swapTickPhaseChunkSource(BooleanSupplier isAheadOfTime, CallbackInfo ci) {
		swapTickTask(TickTask.CHUNK_SOURCE);
	}
	
	@Inject(
			method = "tick",
			at = @At(
					value = "INVOKE_STRING",
					target = "Lnet/minecraft/util/profiler/Profiler;swap(Ljava/lang/String;)V",
					args = "ldc=blockEvents"
			)
	)
	private void swapTickPhaseBlockEvents(BooleanSupplier isAheadOfTime, CallbackInfo ci) {
		swapTickTask(TickTask.BLOCK_EVENTS);
	}
	
	@Inject(
			method = "tick",
			at = @At(
					value = "INVOKE_STRING",
					target = "Lnet/minecraft/util/profiler/Profiler;push(Ljava/lang/String;)V",
					args = "ldc=entities"
			)
	)
	private void swapTickPhaseEntities(BooleanSupplier isAheadOfTime, CallbackInfo ci) {
		swapTickTask(TickTask.ENTITIES);
	}
	
	@Inject(
			method = "tick",
			at = @At(
					value = "INVOKE",
					shift = Shift.BEFORE,
					target = "Lnet/minecraft/server/world/ServerWorld;tickBlockEntities()V"
			)
	)
	private void endTickPhaseEntities(BooleanSupplier isAheadOfTime, CallbackInfo ci) {
		endTickTask();
	}
	
	@Inject(
			method = "tickTime",
			at = @At(
					value = "INVOKE",
					shift = Shift.AFTER,
					target = "Lnet/minecraft/world/level/ServerWorldProperties;setTime(J)V"
			)
	)
	private void onTickTime(CallbackInfo ci) {
		getMultimeterServer().onOverworldTickTime();
	}
	
	@Inject(
			method = "tickSpawners",
			at = @At(
					value = "HEAD"
			)
	)
	private void startTickPhaseCustomMobSpawning(boolean spawnMonsters, boolean spawnAnimals, CallbackInfo ci) {
		startTickTask(TickTask.CUSTOM_MOB_SPAWNING);
	}
	
	@Inject(
			method = "tickSpawners",
			at = @At(
					value = "RETURN"
			)
	)
	private void endTickPhaseCustomMobSpawning(boolean spawnMonsters, boolean spawnAnimals, CallbackInfo ci) {
		endTickTask();
	}
	
	@Inject(
			method = "wakeSleepingPlayers",
			at = @At(
					value = "HEAD"
			)
	)
	private void startTickPhaseWakeSleepingPlayers(CallbackInfo ci) {
		startTickTask(TickTask.WAKE_SLEEPING_PLAYERS);
	}
	
	@Inject(
			method = "wakeSleepingPlayers",
			at = @At(
					value = "RETURN"
			)
	)
	private void endTickPhaseWakeSleepingPlayers(CallbackInfo ci) {
		endTickTask();
	}
	
	@Inject(
			method = "tickChunk",
			at = @At(
					value = "HEAD"
			)
	)
	private void startTickPhaseTickChunk(WorldChunk chunk, int randomTickSpeed, CallbackInfo ci) {
		startTickTask(TickTask.TICK_CHUNK);
	}
	
	@Inject(
			method = "tickChunk",
			at = @At(
					value = "INVOKE_STRING",
					target = "Lnet/minecraft/util/profiler/Profiler;push(Ljava/lang/String;)V",
					args = "ldc=thunder"
			)
	)
	private void startTickPhaseThunder(WorldChunk chunk, int randomTickSpeed, CallbackInfo ci) {
		startTickTask(TickTask.THUNDER);
	}
	
	@Inject(
			method = "tickChunk",
			at = @At(
					value = "INVOKE_STRING",
					target = "Lnet/minecraft/util/profiler/Profiler;swap(Ljava/lang/String;)V",
					args = "ldc=iceandsnow"
			)
	)
	private void swapTickPhasePrecipitation(WorldChunk chunk, int randomTickSpeed, CallbackInfo ci) {
		swapTickTask(TickTask.PRECIPITATION);
	}
	
	@Inject(
			method = "tickChunk",
			at = @At(
					value = "INVOKE_STRING",
					target = "Lnet/minecraft/util/profiler/Profiler;swap(Ljava/lang/String;)V",
					args = "ldc=tickBlocks"
			)
	)
	private void swapTickPhaseRandomTicks(WorldChunk chunk, int randomTickSpeed, CallbackInfo ci) {
		swapTickTask(TickTask.RANDOM_TICKS);
	}
	
	@Inject(
			method = "tickChunk",
			at = @At(
					value = "RETURN"
			)
	)
	private void endTickPhaseRandomTicksAndTickChunk(WorldChunk chunk, int randomTickSpeed, CallbackInfo ci) {
		endTickTask();
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
					target = "Lnet/minecraft/block/BlockState;scheduledTick(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/util/math/BlockPos;Ljava/util/Random;)V"
			)
	)
	private void onTickBlock(ScheduledTick<Block> scheduledTick, CallbackInfo ci) {
		getMultimeter().logScheduledTick((World)(Object)this, scheduledTick);
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
			method = "processBlockEvent",
			at = @At(
					value = "INVOKE",
					shift = Shift.BEFORE,
					target = "Lnet/minecraft/block/BlockState;onSyncedBlockEvent(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;II)Z"
			)
	)
	private void onProcessBlockEvent(BlockEvent blockEvent, CallbackInfoReturnable<Boolean> cir) {
		getMultimeter().logBlockEvent((World)(Object)this, blockEvent);
	}
	
	@Override
	public MultimeterServer getMultimeterServer() {
		return ((IMinecraftServer)server).getMultimeterServer();
	}
}
