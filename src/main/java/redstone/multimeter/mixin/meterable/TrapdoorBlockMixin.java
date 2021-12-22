package redstone.multimeter.mixin.meterable;

import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.block.TrapdoorBlock;
import net.minecraft.world.World;

import redstone.multimeter.block.Meterable;

@Mixin(TrapdoorBlock.class)
public class TrapdoorBlockMixin implements Meterable {
	
	@Override
	public boolean isActive(World world, int x, int y, int z, int meta) {
		return TrapdoorBlock.method_493(meta);
	}
}
