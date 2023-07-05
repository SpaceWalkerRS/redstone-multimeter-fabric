package redstone.multimeter.mixin.common;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.block.state.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.WorldChunk;

import redstone.multimeter.interfaces.mixin.IServerWorld;

@Mixin(WorldChunk.class)
public class WorldChunkMixin {

	@Shadow @Final private World world;

	@Inject(
		method = "setBlockState",
		locals = LocalCapture.CAPTURE_FAILHARD,
		at = @At(
			value = "INVOKE",
			shift = Shift.BEFORE,
			target = "Lnet/minecraft/block/state/BlockState;onRemoved(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/state/BlockState;Z)V"
		)
	)
	private void logBlockChange(BlockPos pos, BlockState state, boolean movedByPiston, CallbackInfoReturnable<BlockState> cir, int sectionX, int y, int sectionZ, int height, BlockState prevState) {
		if (!world.isClient()) {
			((IServerWorld)world).getMultimeter().onBlockChange(world, pos, prevState, state);
		}
	}
}
