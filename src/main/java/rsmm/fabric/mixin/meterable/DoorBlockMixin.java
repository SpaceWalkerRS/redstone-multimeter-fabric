package rsmm.fabric.mixin.meterable;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.DoorBlock;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import rsmm.fabric.common.event.EventType;
import rsmm.fabric.interfaces.mixin.IBlock;
import rsmm.fabric.server.MeterableBlock;

@Mixin(DoorBlock.class)
public class DoorBlockMixin implements MeterableBlock, IBlock {
	
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
	private void onNeighborUpdateInjectBeforePowered0(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean notify, CallbackInfo ci, boolean powered) {
		logPowered(world, pos, powered);
	}
	
	@Override
	public boolean isActive(World world, BlockPos pos, BlockState state) {
		return state.get(Properties.OPEN);
	}
	
	@Override
	public int getDefaultMeteredEvents() {
		return EventType.ACTIVE.flag() | EventType.MOVED.flag();
	}
	
	@Override
	public boolean standardIsPowered() {
		return false;
	}
	
	@Override
	public boolean isPowered(World world, BlockPos pos, BlockState state) {
		if (world.isReceivingRedstonePower(pos)) {
			return true;
		}
		
		DoubleBlockHalf half = state.get(Properties.DOUBLE_BLOCK_HALF);
		Direction dir = (half == DoubleBlockHalf.LOWER) ? Direction.UP : Direction.DOWN;
		
		return world.isReceivingRedstonePower(pos.offset(dir));
	}
}
