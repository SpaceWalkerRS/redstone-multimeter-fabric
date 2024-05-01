package redstone.multimeter.mixin.common;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour.BlockStateBase;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

import redstone.multimeter.interfaces.mixin.IBlock;
import redstone.multimeter.interfaces.mixin.IServerLevel;
import redstone.multimeter.server.Multimeter;
import redstone.multimeter.server.MultimeterServer;

@Mixin(BlockStateBase.class)
public class BlockStateBaseMixin {

	@Shadow private BlockState asState() { return null; }

	@Inject(
		method = "randomTick",
		at = @At(
			value = "HEAD"
		)
	)
	private void logRandomTick(ServerLevel level, BlockPos pos, RandomSource random, CallbackInfo ci) {
		((IServerLevel)level).getMultimeter().logRandomTick(level, pos);
	}

	@Inject(
		method = "useItemOn",
		at = @At(
			value = "HEAD"
		)
	)
	private void logInteractBlock(ItemStack item, Level level, Player player, InteractionHand hand, BlockHitResult hit, CallbackInfoReturnable<InteractionResult> cir) {
		if (!level.isClientSide()) {
			((IServerLevel)level).getMultimeter().logInteractBlock(level, hit.getBlockPos());
		}
	}

	@Inject(
		method = "useWithoutItem",
		at = @At(
			value = "HEAD"
		)
	)
	private void logInteractBlock(Level level, Player player, BlockHitResult hit, CallbackInfoReturnable<InteractionResult> cir) {
		if (!level.isClientSide()) {
			((IServerLevel)level).getMultimeter().logInteractBlock(level, hit.getBlockPos());
		}
	}

	@Inject(
		method = "handleNeighborChanged",
		at = @At(
			value = "HEAD"
		)
	)
	private void logBlockUpdate(Level level, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean movedByPiston, CallbackInfo ci) {
		if (!level.isClientSide()) {
			BlockState state = asState();

			MultimeterServer server = ((IServerLevel)level).getMultimeterServer();
			Multimeter multimeter = server.getMultimeter();

			multimeter.logBlockUpdate(level, pos);

			// 'powered' changes for most meterable blocks are handled in those classes
			// to reduce expensive calls to
			// Level.hasNeighborSignal and Level.getNeighborSignal
			if (((IBlock)state.getBlock()).rsmm$logPoweredOnBlockUpdate()) {
				multimeter.logPowered(level, pos, state);
			}
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
