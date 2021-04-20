package rsmm.fabric.mixin.meterable;

import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.block.BlockState;
import net.minecraft.block.RedstoneLampBlock;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import rsmm.fabric.common.event.EventType;
import rsmm.fabric.server.MeterableBlock;

@Mixin(RedstoneLampBlock.class)
public class RedstoneLampBlockMixin implements MeterableBlock {
	
	@Override
	public boolean isActive(World world, BlockPos pos, BlockState state) {
		return state.get(Properties.LIT);
	}
	
	@Override
	public int getDefaultMeteredEvents() {
		return EventType.POWERED.flag() | EventType.MOVED.flag();
	}
}
