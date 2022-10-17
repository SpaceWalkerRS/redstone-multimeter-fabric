package redstone.multimeter.mixin.common.meterable;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.HopperBlockEntity;

import redstone.multimeter.interfaces.mixin.IHopperBlockEntity;
import redstone.multimeter.interfaces.mixin.IServerWorld;

@Mixin(HopperBlockEntity.class)
public class HopperBlockEntityMixin extends BlockEntity implements IHopperBlockEntity {

	@Shadow private int transferCooldown;

	private HopperBlockEntityMixin(BlockEntityType<?> type) {
		super(type);
	}

	@Inject(
		method = "tick",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/World;getTime()J"
		)
	)
	private void onTick(CallbackInfo ci) {
		logActiveRSMM();
	}

	@Inject(
		method = "setCooldown",
		at = @At(
			value = "TAIL"
		)
	)
	private void onSetCooldown(CallbackInfo ci) {
		logActiveRSMM();
	}

	@Override
	public boolean isOnCooldown() {
		return transferCooldown > 0;
	}

	private void logActiveRSMM() {
		if (!world.isClient()) {
			((IServerWorld)world).getMultimeter().logActive(world, pos, !isOnCooldown());
		}
	}
}
