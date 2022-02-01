package redstone.multimeter.mixin.common.meterable;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.block.BlockRedstoneOre;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import redstone.multimeter.block.Meterable;

@Mixin(BlockRedstoneOre.class)
public class BlockRedstoneOreMixin implements Meterable {
	
	@Shadow @Final private boolean isOn;
	
	@Override
	public boolean isActiveRSMM(World world, BlockPos pos, IBlockState state) {
		return isOn;
	}
}
