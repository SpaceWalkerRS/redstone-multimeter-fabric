package redstone.multimeter.mixin.common.meterable;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.block.PoweredRailBlock;
import net.minecraft.world.World;

import redstone.multimeter.block.MeterableBlock;

@Mixin(PoweredRailBlock.class)
public class PoweredRailBlockMixin implements MeterableBlock {

	@Shadow private boolean isPoweredByConnectedRails(World world, int x, int y, int z, int metadata, boolean forward, int depth) { return false; }

	@Override
	public boolean rsmm$isPowered(World world, int x, int y, int z, int metadata) {
		return world.hasNeighborSignal(x, y, z) || isPoweredByConnectedRails(world, x, y, z, metadata, true, 0) || isPoweredByConnectedRails(world, x, y, z, metadata, false, 0);
	}

	@Override
	public boolean rsmm$isActive(World world, int x, int y, int z, int metadata) {
		return (metadata & 0b1000) != 0;
	}
}
