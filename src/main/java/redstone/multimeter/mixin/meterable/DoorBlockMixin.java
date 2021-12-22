package redstone.multimeter.mixin.meterable;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.class_2181;
import net.minecraft.class_3772;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.DoorBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import redstone.multimeter.block.MeterableBlock;

@Mixin(DoorBlock.class)
public abstract class DoorBlockMixin implements MeterableBlock {
	
	@Inject(
			method = "method_8641",
			locals = LocalCapture.CAPTURE_FAILHARD,
			at = @At(
					value = "FIELD",
					ordinal = 0,
					target = "Lnet/minecraft/block/DoorBlock;field_18295:Lnet/minecraft/state/property/BooleanProperty;"
			)
	)
	private void onNeighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, CallbackInfo ci, boolean powered) {
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
		return state.method_16934(class_3772.field_18781);
	}
	
	private BlockPos getOtherHalf(BlockPos pos, BlockState state) {
		class_2181 half = state.method_16934(class_3772.field_18730);
		Direction dir = (half == class_2181.field_9375) ? Direction.UP : Direction.DOWN;
		
		return pos.offset(dir);
	}
}
