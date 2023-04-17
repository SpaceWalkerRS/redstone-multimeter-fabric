package redstone.multimeter.mixin.common.meterable;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.HopperBlockEntity;

import redstone.multimeter.interfaces.mixin.IHopperBlockEntity;
import redstone.multimeter.interfaces.mixin.IServerLevel;

@Mixin(HopperBlockEntity.class)
public class HopperBlockEntityMixin extends BlockEntity implements IHopperBlockEntity {

	@Shadow private int cooldownTime;

	private HopperBlockEntityMixin(BlockEntityType<?> type) {
		super(type);
	}

	@Inject(
		method = "tick",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/level/Level;getGameTime()J"
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
		return cooldownTime > 0;
	}

	private void rsmm$logActive() {
		if (!level.isClientSide()) {
			((IServerLevel)level).getMultimeter().logActive(level, worldPosition, !rsmm$isOnCooldown());
		}
	}
}
