package redstone.multimeter.mixin.meterable;

import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.block.WeightedPressurePlateBlock;
import net.minecraft.world.World;

import redstone.multimeter.block.Meterable;
import redstone.multimeter.block.PowerSource;

@Mixin(WeightedPressurePlateBlock.class)
public abstract class WeightedPressurePlateBlockMixin implements Meterable, PowerSource {
	
	@Override
	public boolean isActive(World world, int x, int y, int z, int metadata) {
		return metadata > MIN_POWER;
	}
	
	@Override
	public int getPowerLevel(World world, int x, int y, int z, int metadata) {
		return metadata;
	}
}
