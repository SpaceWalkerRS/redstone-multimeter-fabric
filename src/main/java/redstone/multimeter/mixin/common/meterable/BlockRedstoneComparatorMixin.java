package redstone.multimeter.mixin.common.meterable;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.block.BlockRedstoneComparator;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityComparator;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import redstone.multimeter.block.MeterableBlock;
import redstone.multimeter.block.PowerSource;

@Mixin(BlockRedstoneComparator.class)
public class BlockRedstoneComparatorMixin implements MeterableBlock, PowerSource {
	
	@Inject(
			method = "calculateInputStrength",
			at = @At(
					value = "RETURN"
			)
	)
	private void onPowerCheck(World world, BlockPos pos, IBlockState state, CallbackInfoReturnable<Integer> cir) {
		logPoweredRSMM(world, pos, cir.getReturnValue() > MIN_POWER);
	}
	
	@Override
	public boolean isActiveRSMM(World world, BlockPos pos, IBlockState state) {
		return getPowerLevelRSMM(world, pos, state) > MIN_POWER;
	}
	
	@Override
	public boolean logPowerChangeOnStateChangeRSMM() {
		return false;
	}
	
	@Override
	public int getPowerLevelRSMM(World world, BlockPos pos, IBlockState state) {
		TileEntity blockEntity = world.getTileEntity(pos);
		
		if (blockEntity instanceof TileEntityComparator) {
			return ((TileEntityComparator)blockEntity).getOutputSignal();
		}
		
		return MIN_POWER;
	}
}
