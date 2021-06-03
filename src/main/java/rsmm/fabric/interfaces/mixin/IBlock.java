package rsmm.fabric.interfaces.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import rsmm.fabric.block.Meterable;
import rsmm.fabric.block.PowerSource;
import rsmm.fabric.common.event.EventType;

public interface IBlock {
	
	default boolean isMeterable() {
		return this instanceof Meterable;
	}
	
	default boolean isPowerSource() {
		return this instanceof PowerSource;
	}
	
	default int getDefaultMeteredEvents() {
		return EventType.POWERED.flag() | EventType.MOVED.flag();
	}
	
	default boolean standardIsPowered() {
		return true;
	}
	
	default boolean isPowered(World world, BlockPos pos, BlockState state) {
		return world.isReceivingRedstonePower(pos);
	}
}
