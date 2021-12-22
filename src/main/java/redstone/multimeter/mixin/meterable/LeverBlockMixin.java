package redstone.multimeter.mixin.meterable;

import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.block.LeverBlock;
import net.minecraft.world.World;

import redstone.multimeter.block.Meterable;
import redstone.multimeter.block.PowerSource;

@Mixin(LeverBlock.class)
public abstract class LeverBlockMixin implements Meterable, PowerSource {
	
	@Override
	public boolean isActive(World world, int x, int y, int z, int metadata) {
		return (metadata & 8) != 0;
	}
	
	@Override
	public int getPowerLevel(World world, int x, int y, int z, int metadata) {
		return (metadata & 8) == 0 ? MIN_POWER : MAX_POWER;
	}
}
