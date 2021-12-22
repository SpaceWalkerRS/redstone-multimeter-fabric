package redstone.multimeter.mixin.meterable;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.block.BlockState;
import net.minecraft.block.RedstoneOreBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import redstone.multimeter.block.Meterable;

@Mixin(RedstoneOreBlock.class)
public class RedstoneOreBlockMixin implements Meterable {
	
	@Shadow @Final private boolean field_315;
	
	@Override
	public boolean isActive(World world, BlockPos pos, BlockState state) {
		return field_315;
	}
}
