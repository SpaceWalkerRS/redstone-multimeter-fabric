package redstone.multimeter.mixin.common.compat.alternate_current;

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
@Mixin(targets = "alternate.current.wire.LevelAccess")
public class LevelAccessMixin {
	
	@Shadow @Final private ServerWorld level;
	
	@Inject(
			method = "setWireState",
			locals = LocalCapture.CAPTURE_FAILHARD,
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/server/world/ServerChunkManager;markForUpdate(Lnet/minecraft/util/math/BlockPos;)V"
			)
	)
	private void onSetWireState(BlockPos pos, BlockState newState, CallbackInfoReturnable<Boolean> cir, int y, int x, int z, int index, Chunk chunk, ChunkSection section, BlockState oldState) {
		((IServerWorld)level).getMultimeter().onBlockChange(level, pos, oldState, newState);
	}
}
