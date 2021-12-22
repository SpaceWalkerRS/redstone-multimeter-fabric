package redstone.multimeter.mixin.meterable;

import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.block.PressurePlateBlock;
import net.minecraft.world.World;

import redstone.multimeter.block.Meterable;
import redstone.multimeter.block.PowerSource;

@Mixin(PressurePlateBlock.class)
public abstract class PressurePlateBlockMixin implements Meterable, PowerSource {
	
	@Override
	public boolean isActive(World world, int x, int y, int z, int metadata) {
		return metadata == 1;
	}
	
	@Override
	public int getPowerLevel(World world, int x, int y, int z, int metadata) {
		return metadata == 1 ? MAX_POWER : MIN_POWER;
	}
}
