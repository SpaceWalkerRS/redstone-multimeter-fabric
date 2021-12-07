package redstone.multimeter.mixin.common.meterable;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.block.BlockState;
import net.minecraft.block.PoweredRailBlock;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import redstone.multimeter.block.MeterableBlock;

@Mixin(PoweredRailBlock.class)
public abstract class PoweredRailBlockMixin implements MeterableBlock {
	
	@Shadow protected abstract boolean isPoweredByOtherRails(World world, BlockPos pos, BlockState state, boolean boolean4, int distance);
	
	@Override
	public boolean isPoweredRSMM(World world, BlockPos pos, BlockState state) {
		return world.isReceivingRedstonePower(pos) || isPoweredByOtherRails(world, pos, state, true, 0) || isPoweredByOtherRails(world, pos, state, false, 0);
	}
	
	@Override
	public boolean isActiveRSMM(World world, BlockPos pos, BlockState state) {
		return state.get(Properties.POWERED);
	}
}
