package redstone.multimeter.mixin.common.meterable;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityComparator;

import redstone.multimeter.interfaces.mixin.IWorldServer;

@Mixin(TileEntityComparator.class)
public class TileEntityComparatorMixin extends TileEntity {
	
	@Shadow private int outputSignal;
	
	@Inject(
			method = "setOutputSignal",
			at = @At(
					value = "HEAD"
			)
	)
	public void onPowerChange(int newPower, CallbackInfo ci) {
		if (!world.isRemote) {
			((IWorldServer)world).getMultimeter().logPowerChange(world, pos, outputSignal, newPower);
		}
	}
}
