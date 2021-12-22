package redstone.multimeter.mixin.meterable;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;

import redstone.multimeter.block.PowerSource;
import redstone.multimeter.block.chest.TrappedChestHelper;
import redstone.multimeter.interfaces.mixin.IServerWorld;
import redstone.multimeter.server.Multimeter;
import redstone.multimeter.server.MultimeterServer;

@Mixin(ChestBlockEntity.class)
public abstract class ChestBlockEntityMixin extends BlockEntity {
	
	@Shadow private int viewerCount;
	
	@Shadow public abstract int method_4806();
	
	@Inject(
			method = "method_2390",
			at = @At(
					value = "INVOKE",
					shift = Shift.BEFORE,
					target = "Lnet/minecraft/world/World;method_3654(IIILnet/minecraft/block/Block;II)V"
			)
	)
	private void onOpenedByPlayer(CallbackInfo ci) {
		invOpenOrCloseRSMM(true);
	}
	
	@Inject(
			method = "method_2387",
			at = @At(
					value = "INVOKE",
					shift = Shift.BEFORE,
					target = "Lnet/minecraft/world/World;method_3654(IIILnet/minecraft/block/Block;II)V"
			)
	)
	private void onClosedByPlayer(CallbackInfo ci) {
		invOpenOrCloseRSMM(false);
	}
	
	private void invOpenOrCloseRSMM(boolean open) {
		if (!world.isClient && isTrappedRSMM()) {
			MultimeterServer server = ((IServerWorld)world).getMultimeterServer();
			Multimeter multimeter = server.getMultimeter();
			
			int oldViewerCount = open ? viewerCount - 1 : viewerCount + 1;
			
			int oldPower = TrappedChestHelper.getPowerFromViewerCount(oldViewerCount);
			int newPower = TrappedChestHelper.getPowerFromViewerCount(viewerCount);
			
			multimeter.logPowerChange(world, field_566, field_567, field_568, oldPower, newPower);
			multimeter.logActive(world, field_566, field_567, field_568, newPower > PowerSource.MIN_POWER);
		}
	}
	
	private boolean isTrappedRSMM() {
		return method_4806() == 1;
	}
}
