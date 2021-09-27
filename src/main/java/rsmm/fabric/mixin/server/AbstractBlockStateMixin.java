package rsmm.fabric.mixin.server;

import java.util.Random;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.block.BlockState;
import net.minecraft.block.AbstractBlock.AbstractBlockState;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.WorldAccess;

import rsmm.fabric.interfaces.mixin.IServerWorld;
import rsmm.fabric.server.Multimeter;
import rsmm.fabric.server.MultimeterServer;

@Mixin(AbstractBlockState.class)
public class AbstractBlockStateMixin {
	
	@Inject(
			method = "randomTick",
			at = @At(
					value = "HEAD"
			)
	)
	private void onRandomTickInjectAtHead(ServerWorld world, BlockPos pos, Random random, CallbackInfo ci) {
		MultimeterServer server = ((IServerWorld)world).getMultimeterServer();
		Multimeter multimeter = server.getMultimeter();
		
		multimeter.logRandomTick(world, pos);
	}
	
	@Inject(
			method = "getStateForNeighborUpdate",
			at = @At(
					value = "HEAD"
			)
	)
	private void onGetStateForNeighborUpdateInjectAtHead(Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos, CallbackInfoReturnable<BlockState> ci) {
		if (world instanceof ServerWorld) {
			MultimeterServer server = ((IServerWorld)world).getMultimeterServer();
			Multimeter multimeter = server.getMultimeter();
			
			multimeter.logShapeUpdate((ServerWorld)world, pos, direction);
		}
	}
}
