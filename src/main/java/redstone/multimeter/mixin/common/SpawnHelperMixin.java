package redstone.multimeter.mixin.common;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.entity.EntityCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.SpawnHelper;
import net.minecraft.world.World;
import net.minecraft.world.chunk.WorldChunk;

import redstone.multimeter.common.TickTask;
import redstone.multimeter.interfaces.mixin.TickTaskExecutor;

@Mixin(SpawnHelper.class)
public class SpawnHelperMixin {
	
	@Inject(
			method = "spawnEntitiesInChunk",
			at = @At(
					value = "HEAD"
			)
	)
	private static void startTickTaskMobSpawning(EntityCategory category, World world, WorldChunk chunk, BlockPos spawnPos, CallbackInfo ci) {
		((TickTaskExecutor)world).startTickTaskRSMM(TickTask.MOB_SPAWNING);
	}
	
	@Inject(
			method = "spawnEntitiesInChunk",
			at = @At(
					value = "RETURN"
			)
	)
	private static void endTickTaskMobSpawning(EntityCategory category, World world, WorldChunk chunk, BlockPos spawnPos, CallbackInfo ci) {
		((TickTaskExecutor)world).endTickTaskRSMM();
	}
}
