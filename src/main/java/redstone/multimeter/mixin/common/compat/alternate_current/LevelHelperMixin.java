package redstone.multimeter.mixin.common.compat.alternate_current;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
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
@Mixin(targets = "alternate.current.wire.LevelHelper")
public class LevelHelperMixin {
	
	@Inject(
			method = "setWireState",
			locals = LocalCapture.CAPTURE_FAILHARD,
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/server/world/ServerChunkManager;markForUpdate(Lnet/minecraft/util/math/BlockPos;)V"
			)
	)
	private static void onSetWireState(ServerWorld world, BlockPos pos, BlockState newState, boolean updateNeighborShapes, CallbackInfoReturnable<Boolean> cir, int y, int x, int z, Chunk chunk, ChunkSection section, BlockState oldState) {
		((IServerWorld)world).getMultimeter().onBlockChange(world, pos, oldState, newState);
	}
}
