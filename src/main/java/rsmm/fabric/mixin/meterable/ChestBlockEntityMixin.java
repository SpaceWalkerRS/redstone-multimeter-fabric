package rsmm.fabric.mixin.meterable;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.block.TrappedChestBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.entity.player.PlayerEntity;

import rsmm.fabric.block.chest.TrappedChestHelper;
import rsmm.fabric.interfaces.mixin.IServerWorld;
import rsmm.fabric.server.Multimeter;
import rsmm.fabric.server.MultimeterServer;

@Mixin(ChestBlockEntity.class)
public class ChestBlockEntityMixin extends BlockEntity {
	
	@Shadow protected int viewerCount;
	
	public ChestBlockEntityMixin(BlockEntityType<?> type) {
		super(type);
	}
	
	@Inject(
			method = "onOpen",
			at = @At(
					value = "INVOKE",
					shift = Shift.BEFORE,
					target = "Lnet/minecraft/block/entity/ChestBlockEntity;onInvOpenOrClose()V"
			)
	)
	private void onOnOpenInjectBeforeOnInvOpenOrClose(PlayerEntity player, CallbackInfo ci) {
		invOpenOrClose(true);
	}
	
	@Inject(
			method = "onClose",
			at = @At(
					value = "INVOKE",
					shift = Shift.BEFORE,
					target = "Lnet/minecraft/block/entity/ChestBlockEntity;onInvOpenOrClose()V"
			)
	)
	private void onOnCloseInjectBeforeOnInvOpenOrClose(PlayerEntity player, CallbackInfo ci) {
		invOpenOrClose(false);
	}
	
	private void invOpenOrClose(boolean open) {
		if (!world.isClient() && getCachedState().getBlock() instanceof TrappedChestBlock) {
			MultimeterServer server = ((IServerWorld)world).getMultimeterServer();
			Multimeter multimeter = server.getMultimeter();
			
			int oldViewerCount = open ? viewerCount - 1 : viewerCount + 1;
			
			int oldPower = TrappedChestHelper.getPowerFromViewerCount(oldViewerCount);
			int newPower = TrappedChestHelper.getPowerFromViewerCount(viewerCount);
			
			multimeter.logPowerChange(world, pos, oldPower, newPower);
			multimeter.logActive(world, pos, newPower > 0);
		}
	}
}
