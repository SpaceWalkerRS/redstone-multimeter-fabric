package redstone.multimeter.mixin.meterable;

import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.block.BlockState;
import net.minecraft.block.PressurePlateBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import redstone.multimeter.block.Meterable;
import redstone.multimeter.block.PowerSource;

@Mixin(PressurePlateBlock.class)
public abstract class PressurePlateBlockMixin implements Meterable, PowerSource {
	
	@Override
	public boolean isActive(World world, BlockPos pos, BlockState state) {
		return state.get(PressurePlateBlock.POWERED);
	}
	
	@Override
	public int getPowerLevel(World world, BlockPos pos, BlockState state) {
		return state.get(PressurePlateBlock.POWERED) ? MAX_POWER : MIN_POWER;
	}
}
