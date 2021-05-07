package rsmm.fabric.mixin.server;

import java.util.function.BooleanSupplier;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.server.world.ServerChunkManager;
import net.minecraft.server.world.ServerWorld;

import rsmm.fabric.common.TickPhase;
import rsmm.fabric.interfaces.mixin.IServerWorld;

@Mixin(ServerChunkManager.class)
public class ServerChunkManagerMixin {
	
	@Shadow @Final private ServerWorld world;
	
	@Inject(
			method = "tick(Ljava/util/function/BooleanSupplier;)V",
			at = @At(
					value = "HEAD"
			)
	)
	private void onTickInjectAtHead(BooleanSupplier shouldKeepTicking, CallbackInfo ci) {
		((IServerWorld)world).getMultimeterServer().getMultimeter().onTickPhase(TickPhase.TICK_CHUNKS);
	}
}
