package redstone.multimeter.mixin.common.meterable;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.HopperBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import redstone.multimeter.interfaces.mixin.IHopperBlockEntity;
import redstone.multimeter.interfaces.mixin.IServerLevel;

@Mixin(HopperBlockEntity.class)
public class HopperBlockEntityMixin extends BlockEntity implements IHopperBlockEntity {

	@Shadow private int cooldownTime;

	private HopperBlockEntityMixin(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	@Inject(
		method = "pushItemsTick",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/level/Level;getGameTime()J"
		)
	)
	private static void logActive(Level world, BlockPos pos, BlockState state, HopperBlockEntity blockEntity, CallbackInfo ci) {
		((IHopperBlockEntity)blockEntity).rsmm$logActive();
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

	@Override
	public void rsmm$logActive() {
		if (!level.isClientSide()) {
			((IServerLevel)level).getMultimeter().logActive(level, worldPosition, !rsmm$isOnCooldown());
		}
	}
}
