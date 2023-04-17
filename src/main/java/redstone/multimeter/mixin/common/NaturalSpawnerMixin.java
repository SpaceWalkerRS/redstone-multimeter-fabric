package redstone.multimeter.mixin.common;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.NaturalSpawner;
import net.minecraft.world.level.NaturalSpawner.SpawnState;
import net.minecraft.world.level.chunk.LevelChunk;

import redstone.multimeter.common.TickTask;
import redstone.multimeter.interfaces.mixin.IServerLevel;

@Mixin(NaturalSpawner.class)
public class NaturalSpawnerMixin {

	@Inject(
		method = "spawnForChunk",
		at = @At(
			value = "HEAD"
		)
	)
	private static void startTickTaskMobSpawning(ServerLevel level, LevelChunk chunk, SpawnState state, boolean spawnFriendlies, boolean spawnEnemies, boolean spawnPersistents, CallbackInfo ci) {
		((IServerLevel)level).rsmm$startTickTask(TickTask.MOB_SPAWNING);
	}

	@Inject(
		method = "spawnForChunk",
		at = @At(
			value = "TAIL"
		)
	)
	private static void endTickTaskMobSpawning(ServerLevel level, LevelChunk chunk, SpawnState state, boolean spawnFriendlies, boolean spawnEnemies, boolean spawnPersistents, CallbackInfo ci) {
		((IServerLevel)level).rsmm$endTickTask();
	}
}
