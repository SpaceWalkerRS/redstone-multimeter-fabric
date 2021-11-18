package redstone.multimeter.mixin.server;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.SpawnHelper;
import net.minecraft.world.chunk.WorldChunk;

import redstone.multimeter.common.TickTask;
import redstone.multimeter.interfaces.mixin.IServerWorld;

@Mixin(SpawnHelper.class)
public class SpawnHelperMixin {
	
	@Inject(
			method = "spawn",
			at = @At(
					value = "HEAD"
			)
	)
	private static void startTickTaskMobSpawning(ServerWorld world, WorldChunk chunk, SpawnHelper.Info info, boolean spawnAnimals, boolean spawnMonsters, boolean rareSpawn, CallbackInfo ci) {
		((IServerWorld)world).startTickTask(TickTask.MOB_SPAWNING);
	}
	
	@Inject(
			method = "spawn",
			at = @At(
					value = "RETURN"
			)
	)
	private static void endTickTaskMobSpawning(ServerWorld world, WorldChunk chunk, SpawnHelper.Info info, boolean spawnAnimals, boolean spawnMonsters, boolean rareSpawn, CallbackInfo ci) {
		((IServerWorld)world).endTickTask();
	}
}
