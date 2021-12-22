package redstone.multimeter.mixin.meterable;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.block.ChestBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.entity.player.PlayerEntity;

import redstone.multimeter.block.PowerSource;
import redstone.multimeter.block.chest.TrappedChestHelper;
import redstone.multimeter.interfaces.mixin.IServerWorld;
import redstone.multimeter.server.Multimeter;
import redstone.multimeter.server.MultimeterServer;

@Mixin(ChestBlockEntity.class)
public class ChestBlockEntityMixin extends BlockEntity {
	
	@Shadow private int viewerCount;
	@Shadow private ChestBlock.class_2719 field_12843;
	
	@Inject(
			method = "onInvOpen",
			at = @At(
					value = "INVOKE",
					shift = Shift.BEFORE,
					target = "Lnet/minecraft/world/World;addBlockAction(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/Block;II)V"
			)
	)
	private void onOpenedByPlayer(PlayerEntity player, CallbackInfo ci) {
		invOpenOrClose(true);
	}
	
	@Inject(
			method = "onInvClose",
			at = @At(
					value = "INVOKE",
					shift = Shift.BEFORE,
					target = "Lnet/minecraft/world/World;addBlockAction(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/Block;II)V"
			)
	)
	private void onClosedByPlayer(PlayerEntity player, CallbackInfo ci) {
		invOpenOrClose(false);
	}
	
	private void invOpenOrClose(boolean open) {
		if (!world.isClient && isTrapped()) {
			MultimeterServer server = ((IServerWorld)world).getMultimeterServer();
			Multimeter multimeter = server.getMultimeter();
			
			int oldViewerCount = open ? viewerCount - 1 : viewerCount + 1;
			
			int oldPower = TrappedChestHelper.getPowerFromViewerCount(oldViewerCount);
			int newPower = TrappedChestHelper.getPowerFromViewerCount(viewerCount);
			
			multimeter.logPowerChange(world, pos, oldPower, newPower);
			multimeter.logActive(world, pos, newPower > PowerSource.MIN_POWER);
		}
	}
	
	private boolean isTrapped() {
		return field_12843 == ChestBlock.class_2719.field_12623;
	}
}
