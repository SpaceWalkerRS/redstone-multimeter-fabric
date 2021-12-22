package redstone.multimeter.mixin.meterable;

import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.class_3772;
import net.minecraft.block.BlockState;
import net.minecraft.block.TripwireBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import redstone.multimeter.block.Meterable;

@Mixin(TripwireBlock.class)
public abstract class TripwireBlockMixin implements Meterable {
	
	@Override
	public boolean isActive(World world, BlockPos pos, BlockState state) {
		return state.method_16934(class_3772.field_18783);
	}
}
