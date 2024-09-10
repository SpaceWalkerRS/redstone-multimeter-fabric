package redstone.multimeter.mixin.common.meterable;

import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.block.PressurePlateBlock;
import net.minecraft.world.World;

import redstone.multimeter.block.Meterable;
import redstone.multimeter.block.PowerSource;

@Mixin(PressurePlateBlock.class)
public class PressurePlateBlockMixin implements Meterable, PowerSource {

	@Override
	public boolean rsmm$isActive(World world, int x, int y, int z, int metadata) {
		return metadata != 0;
	}

	@Override
	public int rsmm$getPowerLevel(World world, int x, int y, int z, int metadata) {
		return metadata != 0 ? MAX_POWER : MIN_POWER;
	}
}
