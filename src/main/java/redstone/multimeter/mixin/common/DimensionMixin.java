package redstone.multimeter.mixin.common;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.world.dimension.Dimension;

import redstone.multimeter.util.Dimensions;

@Mixin(Dimension.class)
public class DimensionMixin {

	@Inject(
		method = "fromId",
		at = @At(
			value = "RETURN"
		)
	)
	private static void fromId(int id, CallbackInfoReturnable<Dimension> cir) {
		Dimensions.register(cir.getReturnValue(), id);
	}
}
