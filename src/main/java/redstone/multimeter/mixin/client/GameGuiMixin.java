package redstone.multimeter.mixin.client;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GameGui;

import redstone.multimeter.client.MultimeterClient;
import redstone.multimeter.client.gui.GuiRenderer;
import redstone.multimeter.client.gui.hud.MultimeterHud;
import redstone.multimeter.interfaces.mixin.IMinecraft;

@Mixin(GameGui.class)
public class GameGuiMixin {

	@Shadow @Final private Minecraft minecraft;

	@Inject(
		method = "render",
		at = @At(
			value = "INVOKE",
			shift = Shift.AFTER,
			target = "Lnet/minecraft/client/gui/GameGui;renderStatusEffects()V"
		)
	)
	private void renderHud(float tickDelta, CallbackInfo ci) {
		MultimeterClient client = ((IMinecraft)minecraft).getMultimeterClient();
		MultimeterHud hud = client.getHud();

		if (client.isHudActive() && !hud.isOnScreen()) {
			hud.render(new GuiRenderer());
		}
	}
}
