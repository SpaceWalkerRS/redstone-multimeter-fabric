package redstone.multimeter.mixin.common;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;

import redstone.multimeter.interfaces.mixin.IServerLevel;

@Mixin(LevelChunk.class)
public class LevelChunkMixin {

	@Shadow @Final private Level level;

	@Inject(
		method = "setBlockState",
		locals = LocalCapture.CAPTURE_FAILHARD,
		at = @At(
			value = "INVOKE",
			ordinal = 0,
			target = "Lnet/minecraft/world/level/block/state/BlockState;is(Lnet/minecraft/world/level/block/Block;)Z"
		)
	)
	private void logBlockChange(BlockPos pos, BlockState state, int flags, CallbackInfoReturnable<BlockState> cir, int y, LevelChunkSection section, boolean hadOnlyAir, int sectionX, int sectionY, int sectionZ, BlockState prevState) {
		if (!level.isClientSide()) {
			((IServerLevel)level).getMultimeter().onBlockChange(level, pos, prevState, state);
		}
	}
}
