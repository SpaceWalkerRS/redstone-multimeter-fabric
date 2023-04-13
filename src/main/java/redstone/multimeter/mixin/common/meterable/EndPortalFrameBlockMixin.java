package redstone.multimeter.mixin.common.meterable;

import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EndPortalFrameBlock;
import net.minecraft.world.level.block.state.BlockState;

import redstone.multimeter.block.Meterable;

@Mixin(EndPortalFrameBlock.class)
public class EndPortalFrameBlockMixin implements Meterable {

	@Override
	public boolean rsmm$isActive(Level level, BlockPos pos, BlockState state) {
		return state.getValue(EndPortalFrameBlock.HAS_EYE);
	}
}
