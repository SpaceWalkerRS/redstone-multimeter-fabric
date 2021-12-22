package redstone.multimeter.mixin.meterable;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.block.RedstoneOreBlock;
import net.minecraft.world.World;

import redstone.multimeter.block.Meterable;

@Mixin(RedstoneOreBlock.class)
public class RedstoneOreBlockMixin implements Meterable {
	
	@Shadow @Final private boolean field_315;
	
	@Override
	public boolean isActive(World world, int x, int y, int z, int metadata) {
		return field_315;
	}
}
