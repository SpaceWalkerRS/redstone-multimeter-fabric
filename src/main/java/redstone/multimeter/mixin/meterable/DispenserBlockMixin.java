package redstone.multimeter.mixin.meterable;

import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.block.DispenserBlock;
import net.minecraft.world.World;

import redstone.multimeter.block.Meterable;

@Mixin(DispenserBlock.class)
public abstract class DispenserBlockMixin implements Meterable {
	
	@Override
	public boolean isPowered(World world, int x, int y, int z, int metadata) {
		return world.method_3739(x, y, z) || world.method_3739(x, y + 1, z);
	}
	
	@Override
	public boolean isActive(World world, int x, int y, int z, int metadata) {
		return (metadata & 8) != 0;
	}
}
