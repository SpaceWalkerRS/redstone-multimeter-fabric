package redstone.multimeter.mixin.common.meterable;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;

import redstone.multimeter.block.PowerSource;
import redstone.multimeter.block.chest.TrappedChestHelper;
import redstone.multimeter.interfaces.mixin.IServerWorld;
import redstone.multimeter.server.Multimeter;

@Mixin(ChestBlockEntity.class)
public class ChestBlockEntityMixin extends BlockEntity {

	@Shadow private int viewerCount;

	@Shadow private int getChestType() { return 0; }

	@Inject(
		method = "onOpen",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/World;addBlockEvent(IIIIII)V"
		)
	)
	private void onOpen(CallbackInfo ci) {
		signalViewerCount(viewerCount - 1, viewerCount);
	}

	@Inject(
		method = "onClose",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/World;addBlockEvent(IIIIII)V"
		)
	)
	private void onClose(CallbackInfo ci) {
		signalViewerCount(viewerCount + 1, viewerCount);
	}

	private void signalViewerCount(int oldViewerCount, int newViewerCount) {
		if (!world.isMultiplayer && getChestType() == TrappedChestHelper.TYPE) {
			Multimeter multimeter = ((IServerWorld)world).getMultimeter();

			int oldPower = TrappedChestHelper.getPowerFromViewerCount(oldViewerCount);
			int newPower = TrappedChestHelper.getPowerFromViewerCount(newViewerCount);

			multimeter.logPowerChange(world, x, y, z, oldPower, newPower);
			multimeter.logActive(world, x, y, z, newPower > PowerSource.MIN_POWER);
		}
	}
}
