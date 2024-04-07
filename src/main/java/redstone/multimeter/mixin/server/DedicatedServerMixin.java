package redstone.multimeter.mixin.server;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.server.dedicated.DedicatedServer;

import redstone.multimeter.util.DimensionUtils;

@Mixin(DedicatedServer.class)
public class DedicatedServerMixin {

	@Inject(
		method = "init",
		at = @At(
			value = "HEAD"
		)
	)
	private void init(CallbackInfoReturnable<Boolean> cir) {
		DimensionUtils.setUp();
	}
}
