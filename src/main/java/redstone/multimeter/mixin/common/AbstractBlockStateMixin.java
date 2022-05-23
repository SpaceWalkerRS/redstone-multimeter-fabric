package redstone.multimeter.mixin.common;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.block.AbstractBlock.AbstractBlockState;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

import redstone.multimeter.interfaces.mixin.IBlock;
import redstone.multimeter.interfaces.mixin.IServerWorld;
import redstone.multimeter.server.Multimeter;
import redstone.multimeter.server.MultimeterServer;

@Mixin(AbstractBlockState.class)
public abstract class AbstractBlockStateMixin {
	
	@Shadow protected abstract BlockState asBlockState();

	@Inject(
			method = "randomTick",
			at = @At(
					value = "HEAD"
			)
	)
	private void onRandomTick(ServerWorld world, BlockPos pos, Random random, CallbackInfo ci) {
		((IServerWorld)world).getMultimeter().logRandomTick(world, pos);
	}
	
	@Inject(
			method = "onUse",
			at = @At(
					value = "HEAD"
			)
	)
	private void onInteractBlock(World world, PlayerEntity player, Hand hand, BlockHitResult hit, CallbackInfoReturnable<ActionResult> cir) {
		if (!world.isClient()) {
			((IServerWorld)world).getMultimeter().logInteractBlock(world, hit.getBlockPos());
		}
	}

	@Inject(
			method = "neighborUpdate",
			at = @At(
					value = "HEAD"
			)
	)
	private void onBlockUpdate(World world, BlockPos pos, Block fromBlock, BlockPos fromPos, boolean notify, CallbackInfo ci) {
		if (!world.isClient()) {
			BlockState state = asBlockState();

			MultimeterServer server = ((IServerWorld)world).getMultimeterServer();
			Multimeter multimeter = server.getMultimeter();
			
			multimeter.logBlockUpdate(world, pos);
			
			// 'powered' changes for most meterable blocks are handled in those classes
			// to reduce expensive calls to 
			// World.isReceivingRedstonePower and World.getReceivedRedstonePower
			if (((IBlock)state.getBlock()).logPoweredOnBlockUpdateRSMM()) {
				multimeter.logPowered(world, pos, state);
			}
		}
	}
	
	@Inject(
			method = "getStateForNeighborUpdate",
			at = @At(
					value = "HEAD"
			)
	)
	private void onShapeUpdate(Direction direction, BlockState neighborState, WorldAccess worldAccess, BlockPos pos, BlockPos neighborPos, CallbackInfoReturnable<BlockState> ci) {
		if (worldAccess instanceof ServerWorld) {
			ServerWorld world = (ServerWorld)worldAccess;
			((IServerWorld)world).getMultimeter().logShapeUpdate(world, pos, direction);
		}
	}
}
