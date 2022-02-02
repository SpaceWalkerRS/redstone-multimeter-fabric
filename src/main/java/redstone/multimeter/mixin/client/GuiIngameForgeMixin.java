package redstone.multimeter.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiIngame;
import net.minecraftforge.client.GuiIngameForge;

import redstone.multimeter.client.MultimeterClient;
import redstone.multimeter.client.gui.hud.MultimeterHud;
import redstone.multimeter.interfaces.mixin.IMinecraft;

@Mixin(GuiIngameForge.class)
public class GuiIngameForgeMixin extends GuiIngame {
	
	protected GuiIngameForgeMixin(Minecraft client) {
		super(client);
	}
	
	@Inject(
			method = "renderGameOverlay",
			at = @At(
					value = "INVOKE",
					shift = Shift.AFTER,
					target = "Lnet/minecraftforge/client/GuiIngameForge;renderPotionIcons(Lnet/minecraft/client/gui/ScaledResolution;)V"
			)
	)
	private void renderHUD(float tickDelta, CallbackInfo ci) {
		MultimeterClient multimeterClient = ((IMinecraft)mc).getMultimeterClient();
		MultimeterHud hud = multimeterClient.getHUD();
		
		if (multimeterClient.isHudActive() && !hud.isOnScreen()) {
			hud.render();
		}
	}
}
