package redstone.multimeter.mixin.server;

import java.util.Random;

import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

import redstone.multimeter.interfaces.mixin.IServerWorld;

@Mixin(net.minecraft.block.BlockState.class)
public abstract class BlockStateMixin implements BlockState {
	
	@Override
	public void method_73282(World world, BlockPos pos, Random random) {
		((IServerWorld)world).getMultimeter().logRandomTick(world, pos);
		getBlock().onRandomTick(this, world, pos, random);
	}
	
	@Override
	public boolean method_73266(World world, BlockPos pos, PlayerEntity player, Hand hand, Direction dir, float dx, float dy, float dz) {
		if (!world.isClient()) {
			((IServerWorld)world).getMultimeter().logInteractBlock(world, pos);
		}
		
		return getBlock().activate(this, world, pos, player, hand, dir, dx, dy, dz);
	}
	
	@Override
	public BlockState getStateForNeighborUpdate(Direction direction, BlockState neighborState, IWorld iworld, BlockPos pos, BlockPos neighborPos) {
		if (iworld instanceof World) {
			World world = (World)iworld;
			
			if (!world.isClient()) {
				((IServerWorld)world).getMultimeter().logShapeUpdate(world, pos, direction);
			}
		}
		
		return getBlock().getStateForNeighborUpdate(this, direction, neighborState, iworld, pos, neighborPos);
	}
}
