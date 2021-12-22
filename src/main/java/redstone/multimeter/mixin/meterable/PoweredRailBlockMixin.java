package redstone.multimeter.mixin.meterable;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.class_3772;
import net.minecraft.block.BlockState;
import net.minecraft.block.PoweredRailBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import redstone.multimeter.block.MeterableBlock;

@Mixin(PoweredRailBlock.class)
public abstract class PoweredRailBlockMixin implements MeterableBlock {
	
	@Shadow protected abstract boolean isPoweredByOtherRails(World world, BlockPos pos, BlockState state, boolean bl, int distance);
	
	@Override
	public boolean isPowered(World world, BlockPos pos, BlockState state) {
		return world.isReceivingRedstonePower(pos) || isPoweredByOtherRails(world, pos, state, true, 0) || isPoweredByOtherRails(world, pos, state, false, 0);
	}
	
	@Override
	public boolean isActive(World world, BlockPos pos, BlockState state) {
		return state.method_16934(class_3772.field_18783);
	}
}
