package redstone.multimeter.mixin.common.meterable;

import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.block.BlockTripWire;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import redstone.multimeter.block.Meterable;

@Mixin(BlockTripWire.class)
public class BlockTripWireMixin implements Meterable {
	
	@Override
	public boolean isActiveRSMM(World world, BlockPos pos, IBlockState state) {
		return state.getValue(BlockTripWire.POWERED);
	}
}
