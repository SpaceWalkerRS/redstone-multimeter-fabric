package redstone.multimeter.mixin.meterable;

import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.class_3772;
import net.minecraft.block.BlockState;
import net.minecraft.block.EndPortalFrameBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import redstone.multimeter.block.Meterable;

@Mixin(EndPortalFrameBlock.class)
public abstract class EndPortalFrameBlockMixin implements Meterable {
	
	@Override
	public boolean isActive(World world, BlockPos pos, BlockState state) {
		return state.method_16934(class_3772.field_18770);
	}
}
