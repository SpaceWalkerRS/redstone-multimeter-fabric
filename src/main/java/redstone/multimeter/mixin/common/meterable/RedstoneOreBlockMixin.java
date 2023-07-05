package redstone.multimeter.mixin.common.meterable;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.block.RedstoneOreBlock;
import net.minecraft.block.state.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import redstone.multimeter.block.Meterable;

@Mixin(RedstoneOreBlock.class)
public class RedstoneOreBlockMixin implements Meterable {

	@Shadow @Final private boolean lit;

	@Override
	public boolean rsmm$isActive(World world, BlockPos pos, BlockState state) {
		return lit;
	}
}
