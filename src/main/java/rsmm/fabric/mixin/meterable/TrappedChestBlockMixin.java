package rsmm.fabric.mixin.meterable;

import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.block.BlockState;
import net.minecraft.block.TrappedChestBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import rsmm.fabric.block.Meterable;
import rsmm.fabric.block.PowerSource;
import rsmm.fabric.block.chest.TrappedChestHelper;
import rsmm.fabric.interfaces.mixin.IBlock;

@Mixin(TrappedChestBlock.class)
public abstract class TrappedChestBlockMixin implements IBlock, Meterable, PowerSource {
	
	@Override
	public boolean isActive(World world, BlockPos pos, BlockState state) {
		return TrappedChestHelper.getPower(world, pos, state) > 0;
	}
	
	@Override
	public boolean standardLogPowerChange() {
		return false;
	}
	
	@Override
	public int getPowerLevel(World world, BlockPos pos, BlockState state) {
		return TrappedChestHelper.getPower(world, pos, state);
	}
}
