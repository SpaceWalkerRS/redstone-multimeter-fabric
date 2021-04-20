package rsmm.fabric.mixin.meterable;

import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.block.AbstractPressurePlateBlock;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import rsmm.fabric.interfaces.mixin.IPressurePlate;
import rsmm.fabric.server.MeterableBlock;

@Mixin(AbstractPressurePlateBlock.class)
public class AbstractPressurePlateBlockMixin implements MeterableBlock {
	
	@Override
	public boolean isActive(World world, BlockPos pos, BlockState state) {
		return ((IPressurePlate)this).isActive(state);
	}
}
