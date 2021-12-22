package redstone.multimeter.mixin.client;

import java.io.File;
import java.net.Proxy;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.MinecraftClient;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.integrated.IntegratedServer;

import redstone.multimeter.interfaces.mixin.IMinecraftServer;

@Mixin(IntegratedServer.class)
public abstract class IntegratedServerMixin extends MinecraftServer implements IMinecraftServer {
	
	@Shadow @Final private MinecraftClient client;
	@Shadow private boolean paused;
	
	public IntegratedServerMixin(File file, Proxy proxy) {
		super(file, proxy);
	}
	
	@Inject(
			method = "setupWorld()V",
			at = @At(
					value = "HEAD"
			)
	)
	private void onTickStart(CallbackInfo ci) {
		// When the server is paused, the tick method is not called
		if (client.getNetworkHandler() != null && client.isPaused()) {
			getMultimeterServer().tickStart();
		}
	}
	
	@Inject(
			method = "setupWorld()V",
			at = @At(
					value = "RETURN"
			)
	)
	private void onTickEnd(CallbackInfo ci) {
		// When the server is paused, the tick method is not called
		if (paused) {
			getMultimeterServer().tickEnd();
		}
	}
	
	@Override
	public boolean isPaused() {
		return paused;
	}
}
