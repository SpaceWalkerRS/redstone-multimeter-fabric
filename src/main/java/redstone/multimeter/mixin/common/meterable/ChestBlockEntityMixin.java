package redstone.multimeter.mixin.common.meterable;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.block.ChestBlock.Type;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.entity.living.player.PlayerEntity;

import redstone.multimeter.block.PowerSource;
import redstone.multimeter.block.chest.TrappedChestHelper;
import redstone.multimeter.interfaces.mixin.IServerWorld;
import redstone.multimeter.server.Multimeter;

@Mixin(ChestBlockEntity.class)
public class ChestBlockEntityMixin extends BlockEntity {

	@Shadow private int viewerCount;

	@Shadow private Type getType() { return null; }

	@Inject(
		method = "onOpen",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/World;addBlockEvent(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/Block;II)V"
		)
	)
	private void onOpen(PlayerEntity player, CallbackInfo ci) {
		signalViewerCount(viewerCount - 1, viewerCount);
	}

	@Inject(
		method = "onClose",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/World;addBlockEvent(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/Block;II)V"
		)
	)
	private void onClose(PlayerEntity player, CallbackInfo ci) {
		signalViewerCount(viewerCount + 1, viewerCount);
	}

	private void signalViewerCount(int oldViewerCount, int newViewerCount) {
		if (!world.isClient && getType() == Type.TRAP) {
			Multimeter multimeter = ((IServerWorld)world).getMultimeter();

			int oldPower = TrappedChestHelper.getPowerFromViewerCount(oldViewerCount);
			int newPower = TrappedChestHelper.getPowerFromViewerCount(newViewerCount);

			multimeter.logPowerChange(world, pos, oldPower, newPower);
			multimeter.logActive(world, pos, newPower > PowerSource.MIN_POWER);
		}
	}
}
