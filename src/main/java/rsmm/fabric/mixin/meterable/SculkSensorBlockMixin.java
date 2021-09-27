package rsmm.fabric.mixin.meterable;

import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.block.BlockState;
import net.minecraft.block.SculkSensorBlock;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import rsmm.fabric.block.Meterable;
import rsmm.fabric.block.PowerSource;
import rsmm.fabric.interfaces.mixin.IBlock;

@Mixin(SculkSensorBlock.class)
public abstract class SculkSensorBlockMixin implements IBlock, Meterable, PowerSource {
	
	@Override
	public int getPowerLevel(World world, BlockPos pos, BlockState state) {
		return state.get(Properties.POWER);
	}
	
	@Override
	public boolean isActive(World world, BlockPos pos, BlockState state) {
		return state.get(Properties.POWER) > 0;
	}
}
