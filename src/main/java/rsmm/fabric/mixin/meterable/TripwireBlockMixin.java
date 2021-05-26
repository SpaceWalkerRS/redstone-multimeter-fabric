package rsmm.fabric.mixin.meterable;

import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.block.BlockState;
import net.minecraft.block.TripwireBlock;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import rsmm.fabric.block.MeterableBlock;
import rsmm.fabric.common.event.EventType;
import rsmm.fabric.interfaces.mixin.IBlock;

@Mixin(TripwireBlock.class)
public class TripwireBlockMixin implements IBlock, MeterableBlock {
	
	@Override
	public int getDefaultMeteredEvents() {
		return EventType.ACTIVE.flag();
	}
	
	@Override
	public boolean isActive(World world, BlockPos pos, BlockState state) {
		return state.get(Properties.POWERED);
	}
}
