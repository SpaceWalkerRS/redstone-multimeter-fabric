package redstone.multimeter.mixin.common.meterable;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ComparatorBlockEntity;

import redstone.multimeter.interfaces.mixin.IServerWorld;

@Mixin(ComparatorBlockEntity.class)
public class ComparatorBlockEntityMixin extends BlockEntity {

	@Shadow private int outputSignal;

	@Inject(
		method = "setOutputSignal",
		at = @At(
			value = "HEAD"
		)
	)
	public void logPowerChange(int newOutputSignal, CallbackInfo ci) {
		if (!world.isMultiplayer) {
			((IServerWorld)world).getMultimeter().logPowerChange(world, x, y, z, outputSignal, newOutputSignal);
		}
	}
}
