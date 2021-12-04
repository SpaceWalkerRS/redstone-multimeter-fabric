package redstone.multimeter.mixin.meterable;

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
	public void onPowerChange(int newPower, CallbackInfo ci) {
		if (!world.isClient) {
			((IServerWorld)world).getMultimeter().logPowerChange(world, pos, outputSignal, newPower);
		}
	}
}
