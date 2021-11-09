package redstone.multimeter.mixin.server;

import java.util.Random;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.block.BlockState;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

import redstone.multimeter.interfaces.mixin.IServerWorld;

@Mixin(BlockState.class)
public class BlockStateMixin {
	
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
			method = "getStateForNeighborUpdate",
			at = @At(
					value = "HEAD"
			)
	)
	private void onShapeUpdate(Direction direction, BlockState neighborState, IWorld iworld, BlockPos pos, BlockPos neighborPos, CallbackInfoReturnable<BlockState> ci) {
		if (iworld instanceof World) {
			World world = (World)iworld;
			
			if (!world.isClient()) {
				((IServerWorld)world).getMultimeter().logShapeUpdate(world, pos, direction);
			}
		}
	}
}
