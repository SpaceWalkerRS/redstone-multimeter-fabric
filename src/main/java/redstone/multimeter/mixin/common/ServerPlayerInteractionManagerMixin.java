package redstone.multimeter.mixin.common;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.ServerPlayerInteractionManager;
import net.minecraft.world.World;

import redstone.multimeter.interfaces.mixin.IServerWorld;

@Mixin(ServerPlayerInteractionManager.class)
public class ServerPlayerInteractionManagerMixin {

	@Inject(
		method = "useBlock",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/block/Block;use(Lnet/minecraft/world/World;IIILnet/minecraft/entity/living/player/PlayerEntity;IFFF)Z"
		)
	)
	private void logInteractBlock(PlayerEntity player, World world, ItemStack stack, int x, int y, int z, int face, float dx, float dy, float dz, CallbackInfoReturnable<Boolean> cir) {
		if (!world.isMultiplayer) {
			((IServerWorld)world).getMultimeter().logInteractBlock(world, x, y, z);
		}
	}
}
