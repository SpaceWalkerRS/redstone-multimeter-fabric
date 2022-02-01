package redstone.multimeter.mixin.common;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.server.management.PlayerInteractionManager;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import redstone.multimeter.interfaces.mixin.IWorldServer;

@Mixin(PlayerInteractionManager.class)
public class PlayerInteractionManagerMixin {
	
	@Shadow private World world;
	
	@Inject(
			method = "processRightClickBlock",
			at = @At(
					value = "INVOKE",
					ordinal = 1,
					shift = Shift.BEFORE,
					target = "Lnet/minecraft/block/state/IBlockState;getBlock()Lnet/minecraft/block/Block;"
			)
	)
	private void onInteractBlock(EntityPlayer player, World world, ItemStack stack, EnumHand hand, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, CallbackInfoReturnable<EnumActionResult> cir) {
		((IWorldServer)world).getMultimeter().logInteractBlock(world, pos);
	}
}
