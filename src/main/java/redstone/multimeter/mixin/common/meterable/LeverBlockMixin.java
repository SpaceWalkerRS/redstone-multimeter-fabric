package redstone.multimeter.mixin.common.meterable;

import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.block.BlockState;
import net.minecraft.block.LeverBlock;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import redstone.multimeter.block.Meterable;
import redstone.multimeter.block.PowerSource;

@Mixin(LeverBlock.class)
public abstract class LeverBlockMixin implements Meterable, PowerSource {
	
	@Override
	public boolean isActiveRSMM(World world, BlockPos pos, BlockState state) {
		return state.get(Properties.POWERED);
	}
	
	@Override
	public int getPowerLevelRSMM(World world, BlockPos pos, BlockState state) {
		return state.get(Properties.POWERED) ? MAX_POWER : MIN_POWER;
	}
}
