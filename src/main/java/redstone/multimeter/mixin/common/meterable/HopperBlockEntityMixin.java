package redstone.multimeter.mixin.common.meterable;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.HopperBlockEntity;

import redstone.multimeter.interfaces.mixin.IHopperBlockEntity;
import redstone.multimeter.interfaces.mixin.IServerWorld;

@Mixin(HopperBlockEntity.class)
public class HopperBlockEntityMixin extends BlockEntity implements IHopperBlockEntity {

	@Shadow private int transferCooldown;

	@Inject(
		method = "tick",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/block/entity/HopperBlockEntity;isOnCooldown()Z"
		)
	)
	private void logActive(CallbackInfo ci) {
		rsmm$logActive();
	}

	@Inject(
		method = "setCooldown",
		at = @At(
			value = "TAIL"
		)
	)
	private void onSetTransferCooldown(CallbackInfo ci) {
		rsmm$logActive();
	}

	@Override
	public boolean rsmm$isOnCooldown() {
		return transferCooldown > 0;
	}

	private void rsmm$logActive() {
		if (!world.isMultiplayer) {
			((IServerWorld)world).getMultimeter().logActive(world, x, y, z, !rsmm$isOnCooldown());
		}
	}
}
