package redstone.multimeter.mixin.meterable;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.DoorBlock;
import net.minecraft.block.DoorBlock.HalfType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import redstone.multimeter.block.MeterableBlock;

@Mixin(DoorBlock.class)
public abstract class DoorBlockMixin implements MeterableBlock {
	
	@Inject(
			method = "neighborUpdate",
			locals = LocalCapture.CAPTURE_FAILHARD,
			at = @At(
					value = "FIELD",
					ordinal = 0,
					shift = Shift.BEFORE,
					target = "Lnet/minecraft/block/DoorBlock;POWERED:Lnet/minecraft/state/property/BooleanProperty;"
			)
	)
	private void onNeighborUpdate(World world, BlockPos pos, BlockState state, Block block, CallbackInfo ci, boolean powered) {
		logPowered(world, pos, powered);
		logPowered(world, getOtherHalf(pos, state), powered);
	}
	
	@Override
	public boolean logPoweredOnBlockUpdate() {
		return false;
	}
	
	@Override
	public boolean isPowered(World world, BlockPos pos, BlockState state) {
		return world.isReceivingRedstonePower(pos) || world.isReceivingRedstonePower(getOtherHalf(pos, state));
	}
	
	@Override
	public boolean isActive(World world, BlockPos pos, BlockState state) {
		return state.get(DoorBlock.OPEN);
	}
	
	private BlockPos getOtherHalf(BlockPos pos, BlockState state) {
		HalfType half = state.get(DoorBlock.HALF);
		Direction dir = (half == HalfType.LOWER) ? Direction.UP : Direction.DOWN;
		
		return pos.offset(dir);
	}
}
