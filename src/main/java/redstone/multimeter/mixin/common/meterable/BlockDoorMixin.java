package redstone.multimeter.mixin.common.meterable;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.BlockDoor.EnumDoorHalf;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import redstone.multimeter.block.MeterableBlock;

@Mixin(BlockDoor.class)
public class BlockDoorMixin implements MeterableBlock {
	
	@Inject(
			method = "neighborChanged",
			locals = LocalCapture.CAPTURE_FAILHARD,
			at = @At(
					value = "FIELD",
					ordinal = 0,
					shift = Shift.BEFORE,
					target = "Lnet/minecraft/block/BlockDoor;POWERED:Lnet/minecraft/block/properties/PropertyBool;"
			)
	)
	private void onNeighborUpdate(IBlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, CallbackInfo ci, boolean powered, BlockPos otherHalf) {
		logPoweredRSMM(world, pos, powered);
		logPoweredRSMM(world, otherHalf, powered);
	}
	
	@Override
	public boolean logPoweredOnBlockUpdateRSMM() {
		return false;
	}
	
	@Override
	public boolean isPoweredRSMM(World world, BlockPos pos, IBlockState state) {
		return world.isBlockPowered(pos) || world.isBlockPowered(getOtherHalfRSMM(pos, state));
	}
	
	@Override
	public boolean isActiveRSMM(World world, BlockPos pos, IBlockState state) {
		return state.getValue(BlockDoor.OPEN);
	}
	
	private BlockPos getOtherHalfRSMM(BlockPos pos, IBlockState state) {
		EnumDoorHalf half = state.getValue(BlockDoor.HALF);
		EnumFacing dir = (half == EnumDoorHalf.LOWER) ? EnumFacing.UP : EnumFacing.DOWN;
		
		return pos.offset(dir);
	}
}
