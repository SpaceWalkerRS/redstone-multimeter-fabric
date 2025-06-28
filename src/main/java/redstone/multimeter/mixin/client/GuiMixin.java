package redstone.multimeter.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.LayeredDraw;

import redstone.multimeter.client.MultimeterClient;
import redstone.multimeter.client.gui.GuiRenderer;
import redstone.multimeter.client.gui.hud.MultimeterHud;
import redstone.multimeter.interfaces.mixin.IMinecraft;

@Mixin(Gui.class)
public class GuiMixin {

	@Inject(
		method = "<init>",
		locals = LocalCapture.CAPTURE_FAILHARD,
		at = @At(
			value = "NEW",
			ordinal = 2,
			target = "Lnet/minecraft/client/gui/LayeredDraw;"
		)
	)
	private void renderHud(Minecraft minecraft, CallbackInfo ci, LayeredDraw layers) {
		layers.add((graphics, partialTick) -> {
			MultimeterClient client = ((IMinecraft)minecraft).getMultimeterClient();
			MultimeterHud hud = client.getHud();

			if (client.isHudActive() && !hud.isOnScreen()) {
				hud.render(new GuiRenderer(graphics));
			}
		});
	}
}
