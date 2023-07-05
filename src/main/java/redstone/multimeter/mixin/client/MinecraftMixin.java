package redstone.multimeter.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.world.ClientWorld;

import redstone.multimeter.client.MultimeterClient;
import redstone.multimeter.interfaces.mixin.IMinecraft;

@Mixin(Minecraft.class)
public class MinecraftMixin implements IMinecraft {

	private MultimeterClient multimeterClient;

	@Inject(
		method = "init",
		at = @At(
			value = "TAIL"
		)
	)
	private void init(CallbackInfo ci) {
		this.multimeterClient = new MultimeterClient((Minecraft)(Object)this);
	}

	@Inject(
		method = "reloadResources()V",
		at = @At(
			value = "HEAD"
		)
	)
	private void reloadResources(CallbackInfo ci) {
		if (multimeterClient != null) {
			multimeterClient.reloadResources();
		}
	}

	@Inject(
		method = "tick()V",
		at = @At(
			value = "INVOKE",
			shift = Shift.AFTER,
			target = "Lnet/minecraft/client/tutorial/Tutorial;tick()V"
		)
	)
	private void tickTutorial(CallbackInfo ci) {
		multimeterClient.getTutorial().tick();
	}

	@Inject(
		method = "handleKeyBindings",
		at = @At(
			value = "HEAD"
		)
	)
	private void handleKeybinds(CallbackInfo ci) {
		multimeterClient.getInputHandler().handleKeybinds();
	}

	@Inject(
		method = "setWorld(Lnet/minecraft/client/world/ClientWorld;Lnet/minecraft/client/gui/screen/Screen;)V",
		at = @At(
			value = "HEAD"
		)
	)
	private void setWorld(ClientWorld world, Screen screen, CallbackInfo ci) {
		if (world == null) {
			multimeterClient.onDisconnect();
		}
	}

	@Inject(
		method = "stop",
		at = @At(
			value = "HEAD"
		)
	)
	private void onClose(CallbackInfo ci) {
		multimeterClient.onShutdown();
	}

	@Override
	public MultimeterClient getMultimeterClient() {
		return multimeterClient;
	}
}
