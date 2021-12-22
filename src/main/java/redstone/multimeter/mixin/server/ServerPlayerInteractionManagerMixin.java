package redstone.multimeter.mixin.server;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerInteractionManager;
import net.minecraft.world.World;

import redstone.multimeter.interfaces.mixin.IServerWorld;

@Mixin(ServerPlayerInteractionManager.class)
public class ServerPlayerInteractionManagerMixin {
	
	@Inject(
			method = "method_2170",
			at = @At(
					value = "INVOKE",
					shift = Shift.BEFORE,
					target = "Lnet/minecraft/block/Block;method_421(Lnet/minecraft/world/World;IIILnet/minecraft/entity/player/PlayerEntity;IFFF)Z"
			)
	)
	private void onInteractBlock(PlayerEntity playerEntity, World world, ItemStack stack, int x, int y, int z, int dir, float dx, float dy, float dz, CallbackInfoReturnable<Boolean> cir) {
		((IServerWorld)world).getMultimeter().logInteractBlock(world, x, y, z);
	}
}
