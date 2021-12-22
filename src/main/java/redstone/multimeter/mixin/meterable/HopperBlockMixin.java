package redstone.multimeter.mixin.meterable;

import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.block.HopperBlock;
import net.minecraft.world.World;

import redstone.multimeter.block.Meterable;

@Mixin(HopperBlock.class)
public abstract class HopperBlockMixin implements Meterable {
	
	@Override
	public boolean isActive(World world, int x, int y, int z, int metadata) {
		return HopperBlock.method_4776(metadata);
	}
}
