package redstone.multimeter.mixin.common;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.ServerPlayerInteractionManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import redstone.multimeter.interfaces.mixin.IServerWorld;

@Mixin(ServerPlayerInteractionManager.class)
public class ServerPlayerInteractionManagerMixin {

	@Inject(
		method = "useBlock",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/block/Block;use(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/state/BlockState;Lnet/minecraft/entity/living/player/PlayerEntity;Lnet/minecraft/util/math/Direction;FFF)Z"
		)
	)
	private void logInteractBlock(PlayerEntity player, World world, ItemStack stack, BlockPos pos, Direction face, float dx, float dy, float dz, CallbackInfoReturnable<Boolean> cir) {
		if (!world.isClient) {
			((IServerWorld)world).getMultimeter().logInteractBlock(world, pos);
		}
	}
}
