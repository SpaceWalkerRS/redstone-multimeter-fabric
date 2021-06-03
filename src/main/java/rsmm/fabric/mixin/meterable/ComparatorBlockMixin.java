package rsmm.fabric.mixin.meterable;

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

import rsmm.fabric.block.MeterableBlock;
import rsmm.fabric.block.PowerSource;
import rsmm.fabric.common.event.EventType;
import rsmm.fabric.interfaces.mixin.IBlock;

@Mixin(ComparatorBlock.class)
public abstract class ComparatorBlockMixin implements IBlock, MeterableBlock, PowerSource {
	
	@Inject(
			method = "getPower",
			at = @At(
					value = "RETURN"
			)
	)
	private void onHasPowerInjectAtReturn(World world, BlockPos pos, BlockState state, CallbackInfoReturnable<Integer> cir) {
		logPowered(world, pos, cir.getReturnValue() > 0);
	}
	
	@Override
	public int getDefaultMeteredEvents() {
		return EventType.ACTIVE.flag() | EventType.POWER_CHANGE.flag();
	}
	
	@Override
	public boolean standardLogPowerChange() {
		return false;
	}
	
	@Override
	public int getPowerLevel(World world, BlockPos pos, BlockState state) {
		BlockEntity blockEntity = world.getBlockEntity(pos);
		
		if (blockEntity instanceof ComparatorBlockEntity) {
			return ((ComparatorBlockEntity)blockEntity).getOutputSignal();
		}
		
		return 0;
	}
}
