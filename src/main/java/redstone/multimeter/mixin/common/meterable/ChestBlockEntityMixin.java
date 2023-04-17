package redstone.multimeter.mixin.common.meterable;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.ChestBlockEntity;

import redstone.multimeter.interfaces.mixin.IChestBlockEntity;

@Mixin(ChestBlockEntity.class)
public class ChestBlockEntityMixin implements IChestBlockEntity {

	@Shadow private int openCount;

	@Inject(
		method = "startOpen",
		at = @At(
			value = "INVOKE",
			shift = Shift.BEFORE,
			target = "Lnet/minecraft/world/level/block/entity/ChestBlockEntity;signalOpenCount()V"
		)
	)
	private void startOpen(Player player, CallbackInfo ci) {
		signalOpenerCount(openCount - 1, openCount);
	}

	@Inject(
		method = "stopOpen",
		at = @At(
			value = "INVOKE",
			shift = Shift.BEFORE,
			target = "Lnet/minecraft/world/level/block/entity/ChestBlockEntity;signalOpenCount()V"
		)
	)
	private void stopOpen(Player player, CallbackInfo ci) {
		signalOpenerCount(openCount + 1, openCount);
	}

	@Override
	public void signalOpenerCount(int oldOpenerCount, int newOpenerCount) {
	}
}
