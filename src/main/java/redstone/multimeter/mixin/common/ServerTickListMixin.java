package redstone.multimeter.mixin.common;

import java.util.Set;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.server.world.ServerWorld;
import net.minecraft.server.world.tick.ScheduledTick;
import net.minecraft.server.world.tick.ServerTickList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.tick.TickPriority;

import redstone.multimeter.interfaces.mixin.IServerWorld;

@Mixin(ServerTickList.class)
public class ServerTickListMixin {

	@Shadow @Final private ServerWorld world;
	@Shadow @Final private Set<ScheduledTick<?>> ticks;

	private int rsmm$size;

	@Inject(
		method = "scheduleTick",
		at = @At(
			value = "HEAD"
		)
	)
	private void onSchedule(BlockPos pos, Object type, int delay, TickPriority priority, CallbackInfo ci) {
		rsmm$size = ticks.size();
	}

	@Inject(
		method = "scheduleTick",
		at = @At(
			value = "TAIL"
		)
	)
	private void logSchedule(BlockPos pos, Object type, int delay, TickPriority priority, CallbackInfo ci) {
		if (rsmm$size < ticks.size()) {
			((IServerWorld)world).getMultimeter().logScheduledTick(world, pos, priority, true);
		}
	}
}
