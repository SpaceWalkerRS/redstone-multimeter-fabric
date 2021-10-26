package redstone.multimeter.mixin.server;

import java.util.Random;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.block.AbstractBlock.AbstractBlockState;
import net.minecraft.block.BlockState;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import redstone.multimeter.interfaces.mixin.IServerWorld;

@Mixin(AbstractBlockState.class)
public class AbstractBlockStateMixin {
	
	@Inject(
			method = "randomTick",
			at = @At(
					value = "HEAD"
			)
	)
	private void onRandomTickInjectAtHead(ServerWorld world, BlockPos pos, Random random, CallbackInfo ci) {
		((IServerWorld)world).getMultimeter().logRandomTick(world, pos);
	}
	
	@Inject(
			method = "getStateForNeighborUpdate",
			at = @At(
					value = "HEAD"
			)
	)
	private void onGetStateForNeighborUpdateInjectAtHead(Direction direction, BlockState neighborState, WorldAccess worldAccess, BlockPos pos, BlockPos neighborPos, CallbackInfoReturnable<BlockState> ci) {
		if (worldAccess instanceof World) {
			World world = (World)worldAccess;
			
			if (!world.isClient()) {
				((IServerWorld)world).getMultimeter().logShapeUpdate(world, pos, direction);
			}
		}
	}
}
