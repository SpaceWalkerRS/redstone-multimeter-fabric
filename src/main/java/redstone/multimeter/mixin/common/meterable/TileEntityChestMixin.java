package redstone.multimeter.mixin.common.meterable;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.block.BlockChest;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;

import redstone.multimeter.block.PowerSource;
import redstone.multimeter.block.chest.TrappedChestHelper;
import redstone.multimeter.interfaces.mixin.IWorldServer;
import redstone.multimeter.server.Multimeter;
import redstone.multimeter.server.MultimeterServer;

@Mixin(TileEntityChest.class)
public class TileEntityChestMixin extends TileEntity {
	
	@Shadow private int numPlayersUsing;
	@Shadow private BlockChest.Type cachedChestType;
	
	@Inject(
			method = "openInventory",
			at = @At(
					value = "INVOKE",
					shift = Shift.BEFORE,
					target = "Lnet/minecraft/world/World;addBlockEvent(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/Block;II)V"
			)
	)
	private void onOpenedByPlayer(EntityPlayer player, CallbackInfo ci) {
		invOpenOrCloseRSMM(true);
	}
	
	@Inject(
			method = "closeInventory",
			at = @At(
					value = "INVOKE",
					shift = Shift.BEFORE,
					target = "Lnet/minecraft/world/World;addBlockEvent(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/Block;II)V"
			)
	)
	private void onClosedByPlayer(EntityPlayer player, CallbackInfo ci) {
		invOpenOrCloseRSMM(false);
	}
	
	private void invOpenOrCloseRSMM(boolean open) {
		if (!world.isRemote && isTrappedRSMM()) {
			MultimeterServer server = ((IWorldServer)world).getMultimeterServer();
			Multimeter multimeter = server.getMultimeter();
			
			int oldViewerCount = open ? numPlayersUsing - 1 : numPlayersUsing + 1;
			
			int oldPower = TrappedChestHelper.getPowerFromViewerCount(oldViewerCount);
			int newPower = TrappedChestHelper.getPowerFromViewerCount(numPlayersUsing);
			
			multimeter.logPowerChange(world, pos, oldPower, newPower);
			multimeter.logActive(world, pos, newPower > PowerSource.MIN_POWER);
		}
	}
	
	private boolean isTrappedRSMM() {
		return cachedChestType == BlockChest.Type.TRAP;
	}
}
