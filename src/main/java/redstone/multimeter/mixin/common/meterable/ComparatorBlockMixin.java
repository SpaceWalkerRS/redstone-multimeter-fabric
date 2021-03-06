package redstone.multimeter.mixin.common.meterable;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.block.BlockState;
import net.minecraft.block.ComparatorBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ComparatorBlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import redstone.multimeter.block.MeterableBlock;
import redstone.multimeter.block.PowerSource;

@Mixin(ComparatorBlock.class)
public abstract class ComparatorBlockMixin implements MeterableBlock, PowerSource {
	
	@Inject(
			method = "getPower",
			at = @At(
					value = "RETURN"
			)
	)
	private void onPowerCheck(World world, BlockPos pos, BlockState state, CallbackInfoReturnable<Integer> cir) {
		logPoweredRSMM(world, pos, cir.getReturnValue() > MIN_POWER);
	}
	
	@Override
	public boolean logPowerChangeOnStateChangeRSMM() {
		return false;
	}
	
	@Override
	public int getPowerLevelRSMM(World world, BlockPos pos, BlockState state) {
		BlockEntity blockEntity = world.getBlockEntity(pos);
		
		if (blockEntity instanceof ComparatorBlockEntity) {
			return ((ComparatorBlockEntity)blockEntity).getOutputSignal();
		}
		
		return MIN_POWER;
	}
}
