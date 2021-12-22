package redstone.multimeter.mixin.meterable;

import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.class_3731;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import redstone.multimeter.block.Meterable;
import redstone.multimeter.block.PowerSource;
import redstone.multimeter.block.chest.TrappedChestHelper;

@Mixin(class_3731.class)
public abstract class TrappedChestBlockMixin implements Meterable, PowerSource {
	
	@Override
	public boolean isActive(World world, BlockPos pos, BlockState state) {
		return TrappedChestHelper.getPower(world, pos, state) > MIN_POWER;
	}
	
	@Override
	public boolean logPowerChangeOnStateChange() {
		return false;
	}
	
	@Override
	public int getPowerLevel(World world, BlockPos pos, BlockState state) {
		return TrappedChestHelper.getPower(world, pos, state);
	}
}
