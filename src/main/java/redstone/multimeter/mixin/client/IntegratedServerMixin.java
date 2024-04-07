package redstone.multimeter.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.server.integrated.IntegratedServer;

import redstone.multimeter.interfaces.mixin.IMinecraftServer;
import redstone.multimeter.util.DimensionUtils;

@Mixin(IntegratedServer.class)
public abstract class IntegratedServerMixin implements IMinecraftServer {

	@Shadow private boolean paused;

	@Inject(
		method = "init",
		at = @At(
			value = "HEAD"
		)
	)
	private void init(CallbackInfoReturnable<Boolean> cir) {
		DimensionUtils.setUp();
	}

	@Inject(
		method = "tick",
		at = @At(
			value = "HEAD"
		)
	)
	private void onTickStart(CallbackInfo ci) {
		getMultimeterServer().tickStart();
	}

	@Inject(
		method = "tick",
		at = @At(
			value = "TAIL"
		)
	)
	private void onTickEnd(CallbackInfo ci) {
		getMultimeterServer().tickEnd();
	}

	@Override
	public boolean rsmm$isPaused() {
		return paused;
	}
}
