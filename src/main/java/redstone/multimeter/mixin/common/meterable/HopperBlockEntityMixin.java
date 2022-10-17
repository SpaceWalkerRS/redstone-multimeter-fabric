package redstone.multimeter.mixin.common.meterable;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.HopperBlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import redstone.multimeter.interfaces.mixin.IHopperBlockEntity;
import redstone.multimeter.interfaces.mixin.IServerWorld;

@Mixin(HopperBlockEntity.class)
public class HopperBlockEntityMixin extends BlockEntity implements IHopperBlockEntity {

	@Shadow private int transferCooldown;

	private HopperBlockEntityMixin(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	@Inject(
		method = "serverTick",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/World;getTime()J"
		)
	)
	private static void onTick(World world, BlockPos pos, BlockState state, HopperBlockEntity blockEntity, CallbackInfo ci) {
		((IHopperBlockEntity)blockEntity).logActiveRSMM();
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

	@Override
	public void logActiveRSMM() {
		if (!world.isClient()) {
			((IServerWorld)world).getMultimeter().logActive(world, pos, !isOnCooldown());
		}
	}
}
