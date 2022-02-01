package redstone.multimeter.mixin.common.meterable;

import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.block.BlockTripWireHook;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import redstone.multimeter.block.Meterable;
import redstone.multimeter.block.PowerSource;

@Mixin(BlockTripWireHook.class)
public class BlockTripWireHookMixin implements Meterable, PowerSource {
	
	@Override
	public boolean isActiveRSMM(World world, BlockPos pos, IBlockState state) {
		return state.getValue(BlockTripWireHook.POWERED);
	}
	
	@Override
	public int getPowerLevelRSMM(World world, BlockPos pos, IBlockState state) {
		return state.getValue(BlockTripWireHook.POWERED) ? MAX_POWER : MIN_POWER;
	}
}
