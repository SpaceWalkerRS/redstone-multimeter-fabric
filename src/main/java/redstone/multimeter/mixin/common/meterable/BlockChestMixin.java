package redstone.multimeter.mixin.common.meterable;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.block.BlockChest;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import redstone.multimeter.block.Meterable;
import redstone.multimeter.block.PowerSource;
import redstone.multimeter.block.chest.TrappedChestHelper;

@Mixin(BlockChest.class)
public class BlockChestMixin implements Meterable, PowerSource {
	
	@Shadow @Final private BlockChest.Type chestType;
	
	@Override
	public boolean isMeterableRSMM() {
		return isTrappedRSMM();
	}
	
	@Override
	public boolean isPowerSourceRSMM() {
		return isTrappedRSMM();
	}
	
	@Override
	public boolean isActiveRSMM(World world, BlockPos pos, IBlockState state) {
		return isTrappedRSMM() && TrappedChestHelper.getPower(world, pos, state) > MIN_POWER;
	}
	
	@Override
	public boolean logPowerChangeOnStateChangeRSMM() {
		return false;
	}
	
	@Override
	public int getPowerLevelRSMM(World world, BlockPos pos, IBlockState state) {
		return isTrappedRSMM() ? TrappedChestHelper.getPower(world, pos, state) : MIN_POWER;
	}
	
	private boolean isTrappedRSMM() {
		return chestType == BlockChest.Type.TRAP;
	}
}
