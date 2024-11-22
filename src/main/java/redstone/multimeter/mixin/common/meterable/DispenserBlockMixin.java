package redstone.multimeter.mixin.common.meterable;

import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.block.DispenserBlock;
import net.minecraft.world.World;

import redstone.multimeter.block.MeterableBlock;

@Mixin(DispenserBlock.class)
public class DispenserBlockMixin implements MeterableBlock {

	@Override
	public boolean rsmm$isPowered(World world, int x, int y, int z, int metadata) {
		return world.hasNeighborSignal(x, y, z) || world.hasNeighborSignal(x, y + 1, z);
	}

	@Override
	public boolean rsmm$isActive(World world, int x, int y, int z, int metadata) {
		return (metadata & 0b1000) != 0;
	}
}
