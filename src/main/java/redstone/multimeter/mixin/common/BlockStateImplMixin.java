package redstone.multimeter.mixin.common;

import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.BlockStateImpl;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.WorldAccess;

import redstone.multimeter.interfaces.mixin.IServerWorld;

@Mixin(BlockStateImpl.class)
public abstract class BlockStateImplMixin implements BlockState {

	// not a fan of this but cannot inject in interfaces so this is the best alternative
	@Override
	public BlockState updateShape(Direction dir, BlockState neighborState, WorldAccess worldAccess, BlockPos pos, BlockPos neighborPos) {
		if (worldAccess instanceof ServerWorld) {
			ServerWorld world = (ServerWorld)worldAccess;
			((IServerWorld)world).getMultimeter().logShapeUpdate(world, pos, dir);
		}

		return getBlock().updateShape(this, dir, neighborState, worldAccess, pos, neighborPos);
	}
}
