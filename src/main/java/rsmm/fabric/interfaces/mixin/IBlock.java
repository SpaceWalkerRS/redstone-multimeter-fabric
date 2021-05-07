package rsmm.fabric.interfaces.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import rsmm.fabric.common.event.EventType;
import rsmm.fabric.server.Meterable;

public interface IBlock {
	
	default boolean isMeterable() {
		return this instanceof Meterable;
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
