package redstone.multimeter.mixin.meterable;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.block.BlockState;
import net.minecraft.block.ChestBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import redstone.multimeter.block.Meterable;
import redstone.multimeter.block.PowerSource;
import redstone.multimeter.block.chest.TrappedChestHelper;

@Mixin(ChestBlock.class)
public abstract class ChestBlockMixin implements Meterable, PowerSource {
	
	@Shadow @Final private ChestBlock.Type field_24266;
	
	@Override
	public boolean isActive(World world, BlockPos pos, BlockState state) {
		return isTrappedRSMM() && TrappedChestHelper.getPower(world, pos, state) > MIN_POWER;
	}
	
	@Override
	public boolean logPowerChangeOnStateChange() {
		return false;
	}
	
	@Override
	public int getPowerLevel(World world, BlockPos pos, BlockState state) {
		return isTrappedRSMM() ? TrappedChestHelper.getPower(world, pos, state) : MIN_POWER;
	}
	
	private boolean isTrappedRSMM() {
		return field_24266 == ChestBlock.Type.TRAP;
	}
}
