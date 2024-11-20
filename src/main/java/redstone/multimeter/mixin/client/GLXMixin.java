package redstone.multimeter.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.blaze3d.platform.GLX;

import redstone.multimeter.util.GL;

@Mixin(GLX.class)
public class GLXMixin {

	@Inject(
		method = "init",
		at = @At(
			value = "TAIL"
		)
	)
	private static void init(CallbackInfo ci) {
		GL.init();
	}
}
