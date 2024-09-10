package redstone.multimeter.mixin.common.meterable;

import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.block.EndPortalFrameBlock;
import net.minecraft.world.World;

import redstone.multimeter.block.Meterable;

@Mixin(EndPortalFrameBlock.class)
public class EndPortalFrameBlockMixin implements Meterable {

	@Override
	public boolean rsmm$isActive(World world, int x, int y, int z, int metadata) {
		return (metadata & 0b100) != 0;
	}
}
