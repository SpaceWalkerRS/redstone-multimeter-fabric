package redstone.multimeter.mixin.meterable;

import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.block.FenceGateBlock;
import net.minecraft.world.World;

import redstone.multimeter.block.Meterable;

@Mixin(FenceGateBlock.class)
public class FenceGateBlockMixin implements Meterable {
	
	@Override
	public boolean isActive(World world, int x, int y, int z, int metadata) {
		return (metadata & 4) != 0;
	}
}
