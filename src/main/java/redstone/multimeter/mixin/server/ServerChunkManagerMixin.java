package redstone.multimeter.mixin.server;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.server.world.ServerChunkManager;
import net.minecraft.server.world.ServerWorld;

import redstone.multimeter.common.TickTask;
import redstone.multimeter.interfaces.mixin.IServerWorld;

@Mixin(ServerChunkManager.class)
public class ServerChunkManagerMixin {
	
	@Shadow @Final private ServerWorld world;
	
	@Inject(
			method = "tickChunks",
			at = @At(
					value = "INVOKE",
					shift = Shift.BEFORE,
					target = "Lnet/minecraft/world/gen/chunk/ChunkGenerator;spawnEntities(Lnet/minecraft/server/world/ServerWorld;ZZ)V"
			)
	)
	private void startTickTaskCustomMobSpawning(CallbackInfo ci) {
		((IServerWorld)world).startTickTask(TickTask.CUSTOM_MOB_SPAWNING);
	}
	
	@Inject(
			method = "tickChunks",
			at = @At(
					value = "INVOKE",
					shift = Shift.AFTER,
					target = "Lnet/minecraft/world/gen/chunk/ChunkGenerator;spawnEntities(Lnet/minecraft/server/world/ServerWorld;ZZ)V"
			)
	)
	private void endTickTaskCustomMobSpawning(CallbackInfo ci) {
		((IServerWorld)world).endTickTask();
	}
}
