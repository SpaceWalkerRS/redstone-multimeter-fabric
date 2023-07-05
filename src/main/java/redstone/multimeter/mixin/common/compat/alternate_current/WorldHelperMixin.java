package redstone.multimeter.mixin.common.compat.alternate_current;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.block.state.BlockState;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.chunk.WorldChunkSection;

import redstone.multimeter.interfaces.mixin.IServerWorld;

@Pseudo
@Mixin(targets = "alternate.current.wire.WorldHelper")
public class WorldHelperMixin {

	@Inject(
		method = "setWireState",
		locals = LocalCapture.CAPTURE_FAILHARD,
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/server/ChunkMap;onBlockChanged(Lnet/minecraft/util/math/BlockPos;)V"
		)
	)
	private static void onSetWireState(ServerWorld world, BlockPos pos, BlockState state, boolean updateNeighborShapes, CallbackInfoReturnable<Boolean> cir, int y, int x, int z, int index, WorldChunk chunk, WorldChunkSection section, BlockState oldState) {
		((IServerWorld)world).getMultimeter().onBlockChange(world, pos, oldState, state);
	}
}
