package redstone.multimeter.mixin.common;

import java.util.Random;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

import redstone.multimeter.interfaces.mixin.IServerLevel;

@Mixin(BlockState.class)
public class BlockStateMixin {

	@Inject(
		method = "randomTick",
		at = @At(
			value = "HEAD"
		)
	)
	private void logRandomTick(Level level, BlockPos pos, Random random, CallbackInfo ci) {
		if (!level.isClientSide()) {
			((IServerLevel)level).getMultimeter().logRandomTick(level, pos);
		}
	}

	@Inject(
		method = "use",
		at = @At(
			value = "HEAD"
		)
	)
	private void logInteractBlock(Level level, Player player, InteractionHand hand, BlockHitResult hit, CallbackInfoReturnable<InteractionResult> cir) {
		if (!level.isClientSide()) {
			((IServerLevel)level).getMultimeter().logInteractBlock(level, hit.getBlockPos());
		}
	}

	@Inject(
		method = "updateShape",
		at = @At(
			value = "HEAD"
		)
	)
	private void logShapeUpdate(Direction dir, BlockState neighborState, LevelAccessor levelAccess, BlockPos pos, BlockPos neighborPos, CallbackInfoReturnable<BlockState> cir) {
		if (levelAccess instanceof ServerLevel) {
			ServerLevel level = (ServerLevel)levelAccess;
			((IServerLevel)level).getMultimeter().logShapeUpdate(level, pos, dir);
		}
	}
}
