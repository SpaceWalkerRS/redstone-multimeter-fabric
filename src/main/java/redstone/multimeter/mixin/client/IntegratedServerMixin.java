package redstone.multimeter.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.server.integrated.IntegratedServer;

import redstone.multimeter.common.TickTask;
import redstone.multimeter.interfaces.mixin.IMinecraftServer;
import redstone.multimeter.server.MultimeterServer;

@Mixin(IntegratedServer.class)
public abstract class IntegratedServerMixin implements IMinecraftServer {
	
	@Shadow private boolean isGamePaused;
	
	@Inject(
			method = "tick",
			at = @At(
					value = "HEAD"
			)
	)
	private void onTickStart(CallbackInfo ci) {
		boolean paused = false;
		
		Minecraft client = Minecraft.getMinecraft();
		NetHandlerPlayClient connection = client.getConnection();
		
		if (connection != null) {
			paused = client.isGamePaused();
		}
		
		// When the server is paused, the super tick method is not called
		if (paused) {
			MultimeterServer server = getMultimeterServer();
			
			server.tickStart();
			server.startTickTask(true, TickTask.PACKETS);
		}
	}
	
	@Inject(
			method = "tick",
			at = @At(
					value = "RETURN"
			)
	)
	private void onTickEnd(CallbackInfo ci) {
		// When the server is paused, the super tick method is not called
		if (isGamePaused) {
			MultimeterServer server = getMultimeterServer();
			
			server.tickEnd();
			server.endTickTask(true);
		}
	}
	
	@Override
	public boolean isPausedRSMM() {
		return isGamePaused;
	}
}
