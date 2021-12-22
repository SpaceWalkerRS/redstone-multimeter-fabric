package redstone.multimeter.mixin.meterable;

import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.block.EndPortalFrameBlock;
import net.minecraft.world.World;

import redstone.multimeter.block.Meterable;

@Mixin(EndPortalFrameBlock.class)
public abstract class EndPortalFrameBlockMixin implements Meterable {
	
	@Override
	public boolean isActive(World world, int x, int y, int z, int metadata) {
		return (metadata & 4) != 0;
	}
}
