package redstone.multimeter.mixin.client;

import java.util.function.BooleanSupplier;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.server.integrated.IntegratedServer;

import redstone.multimeter.interfaces.mixin.IMinecraftServer;

@Mixin(IntegratedServer.class)
public abstract class IntegratedServerMixin implements IMinecraftServer {
	
	@Shadow private boolean paused;
	
	@Inject(
			method = "tick",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/server/integrated/IntegratedServer;getProfiler()Lnet/minecraft/util/profiler/DisableableProfiler;"
			)
	)
	private void onTickStart(BooleanSupplier isAheadOfTime, CallbackInfo ci) {
		// When the server is paused, the tick method is not called
		if (paused) {
			getMultimeterServer().tickStart();
		}
	}
	
	@Override
	public boolean isPaused() {
		return paused;
	}
}
