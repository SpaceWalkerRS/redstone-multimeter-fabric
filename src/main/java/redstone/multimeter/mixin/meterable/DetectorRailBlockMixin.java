package redstone.multimeter.mixin.meterable;

import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.block.DetectorRailBlock;
import net.minecraft.world.World;

import redstone.multimeter.block.Meterable;
import redstone.multimeter.block.PowerSource;

@Mixin(DetectorRailBlock.class)
public abstract class DetectorRailBlockMixin implements Meterable, PowerSource {
	
	@Override
	public boolean isActive(World world, int x, int y, int z, int metadata) {
		return (metadata & 8) != 0;
	}
	
	@Override
	public int getPowerLevel(World world, int x, int y, int z, int metadata) {
		return (metadata & 8) == 0 ? MIN_POWER : MAX_POWER;
	}
}
