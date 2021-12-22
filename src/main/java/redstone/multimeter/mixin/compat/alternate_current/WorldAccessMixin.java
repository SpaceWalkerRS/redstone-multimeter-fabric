package redstone.multimeter.mixin.compat.alternate_current;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.block.BlockState;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkSection;

import redstone.multimeter.interfaces.mixin.IServerWorld;

@Pseudo
@Mixin(targets = "alternate.current.redstone.WorldAccess")
public class WorldAccessMixin {
	
	@Shadow @Final private ServerWorld world;
	
	@Inject(
			method = "setWireState",
			locals = LocalCapture.CAPTURE_FAILHARD,
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/server/PlayerWorldManager;method_10748(Lnet/minecraft/util/math/BlockPos;)V"
			)
	)
	private void onSetWireState(BlockPos pos, BlockState newState, CallbackInfoReturnable<Boolean> cir, int y, int x, int z, Chunk chunk, ChunkSection section, BlockState oldState) {
		((IServerWorld)world).getMultimeter().onBlockChange(world, pos, oldState, newState);
	}
}
