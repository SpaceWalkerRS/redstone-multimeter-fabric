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
			value = "HEAD"
		)
	)
	private void onTickStart(BooleanSupplier hasTimeLeft, CallbackInfo ci) {
		getMultimeterServer().tickStart();
	}

	@Inject(
		method = "tick",
		at = @At(
			value = "TAIL"
		)
	)
	private void onTickEnd(BooleanSupplier hasTimeLeft, CallbackInfo ci) {
		getMultimeterServer().tickEnd();
	}

	@Override
	public boolean rsmm$isPaused() {
		return paused;
	}
}
