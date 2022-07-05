package redstone.multimeter.mixin.common;

import java.util.Set;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.server.world.ServerTickScheduler;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ScheduledTick;
import net.minecraft.world.TickPriority;

import redstone.multimeter.interfaces.mixin.IServerWorld;

@Mixin(ServerTickScheduler.class)
public class ServerTickSchedulerMixin {
	
	@Shadow @Final ServerWorld world;
	@Shadow @Final Set<ScheduledTick<?>> scheduledTickActions;
	
	private int queueSize;
	
	@Inject(
			method = "schedule",
			at = @At(
					value = "HEAD"
			)
	)
	private void onSchedule(BlockPos pos, Object object, int delay, TickPriority priority, CallbackInfo ci) {
		queueSize = scheduledTickActions.size();
	}
	
	@Inject(
			method = "schedule",
			at = @At(
					value = "RETURN"
			)
	)
	private void postSchedule(BlockPos pos, Object object, int delay, TickPriority priority, CallbackInfo ci) {
		if (queueSize < scheduledTickActions.size()) {
			((IServerWorld)world).getMultimeter().logScheduledTick(world, pos, priority, true);
		}
	}
}
