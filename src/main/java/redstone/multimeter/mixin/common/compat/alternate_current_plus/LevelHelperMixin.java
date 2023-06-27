package redstone.multimeter.mixin.common.compat.alternate_current_plus;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunkSection;

import redstone.multimeter.interfaces.mixin.IServerLevel;

@Pseudo
@Mixin(targets = "alternate.current.plus.wire.LevelHelper")
public class LevelHelperMixin {

	@Inject(
		method = "setWireState",
		locals = LocalCapture.CAPTURE_FAILHARD,
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/server/level/ServerChunkCache;blockChanged(Lnet/minecraft/core/BlockPos;)V"
		)
	)
	private static void onSetWireState(ServerLevel level, BlockPos pos, BlockState state, boolean updateNeighborShapes, CallbackInfoReturnable<Boolean> cir, int y, int x, int z, int index, ChunkAccess chunk, LevelChunkSection section, BlockState oldState) {
		((IServerLevel)level).getMultimeter().onBlockChange(level, pos, oldState, state);
	}
}
