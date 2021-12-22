package redstone.multimeter.mixin.meterable;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.block.PoweredRailBlock;
import net.minecraft.world.World;

import redstone.multimeter.block.MeterableBlock;

@Mixin(PoweredRailBlock.class)
public abstract class PoweredRailBlockMixin implements MeterableBlock {
	
	@Shadow protected abstract boolean method_4780(World world, int x, int y, int z, int metadata, boolean bl, int depth);
	
	@Override
	public boolean isPowered(World world, int x, int y, int z, int metadata) {
		return world.method_3739(x, y, z) || method_4780(world, x, y, z, metadata, true, 0) || method_4780(world, x, y, z, metadata, false, 0);
	}
	
	@Override
	public boolean isActive(World world, int x, int y, int z, int metadata) {
		return (metadata & 8) != 0;
	}
}
