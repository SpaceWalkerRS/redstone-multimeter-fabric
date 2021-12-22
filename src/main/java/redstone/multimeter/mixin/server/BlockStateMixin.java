package redstone.multimeter.mixin.server;

import java.util.Random;

import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.class_2961;
import net.minecraft.class_3598;
import net.minecraft.class_3756;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import redstone.multimeter.interfaces.mixin.IServerWorld;

@Mixin(class_3756.class)
public abstract class BlockStateMixin implements BlockState {
	
	@Override
	public void method_16887(World world, BlockPos pos, Random random) {
		((IServerWorld)world).getMultimeter().logRandomTick(world, pos);
		getBlock().method_16582(this, world, pos, random);
	}
	
	@Override
	public boolean method_16871(World world, BlockPos pos, PlayerEntity player, class_2961 hand, Direction dir, float dx, float dy, float dz) {
		if (!world.method_16390()) {
			((IServerWorld)world).getMultimeter().logInteractBlock(world, pos);
		}
		
		return getBlock().method_421(this, world, pos, player, hand, dir, dx, dy, dz);
	}
	
	@Override
	public BlockState method_16883(Direction direction, BlockState neighborState, class_3598 iworld, BlockPos pos, BlockPos neighborPos) {
		if (iworld instanceof World) {
			World world = (World)iworld;
			
			if (!world.method_16390()) {
				((IServerWorld)world).getMultimeter().logShapeUpdate(world, pos, direction);
			}
		}
		
		return getBlock().method_16575(this, direction, neighborState, iworld, pos, neighborPos);
	}
}
