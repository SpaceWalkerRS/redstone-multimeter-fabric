package redstone.multimeter.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;

import redstone.multimeter.client.MultimeterClient;
import redstone.multimeter.client.gui.GuiRenderer;
import redstone.multimeter.client.gui.hud.MultimeterHud;

@Mixin(Gui.class)
public class GuiMixin {

	@Inject(
		method = "render",
		locals = LocalCapture.CAPTURE_FAILHARD,
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/gui/Gui;renderSleepOverlay(Lnet/minecraft/client/gui/GuiGraphics;Lnet/minecraft/client/DeltaTracker;)V"
		)
	)
	private void renderHud(GuiGraphics graphics, DeltaTracker deltaTracker, CallbackInfo ci) {
		MultimeterClient client = MultimeterClient.INSTANCE;
		MultimeterHud hud = client.getHud();

		if (client.isHudActive() && !hud.isOnScreen()) {
			hud.render(new GuiRenderer(graphics));
		}
	}
}
