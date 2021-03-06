package redstone.multimeter.mixin.common.meterable;

import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.block.BlockState;
import net.minecraft.block.TripwireBlock;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import redstone.multimeter.block.Meterable;

@Mixin(TripwireBlock.class)
public abstract class TripwireBlockMixin implements Meterable {
	
	@Override
	public boolean isActiveRSMM(World world, BlockPos pos, BlockState state) {
		return state.get(Properties.POWERED);
	}
}
