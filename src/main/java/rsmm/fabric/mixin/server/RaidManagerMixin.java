package rsmm.fabric.mixin.server;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.server.world.ServerWorld;
import net.minecraft.village.raid.RaidManager;
import rsmm.fabric.common.TickPhase;
import rsmm.fabric.interfaces.mixin.IServerWorld;

@Mixin(RaidManager.class)
public class RaidManagerMixin {
	
	@Shadow @Final private ServerWorld world;
	
	@Inject(
			method = "tick",
			at = @At(
					value = "HEAD"
			)
	)
	private void onTickInjectAtHead(CallbackInfo ci) {
		((IServerWorld)world).getMultimeterServer().getMultimeter().onTickPhase(TickPhase.TICK_RAIDS);
	}
}
