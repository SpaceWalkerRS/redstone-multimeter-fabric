package redstone.multimeter.mixin.common.compat.lithium;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;

import me.jellysquid.mods.lithium.common.world.scheduler.LithiumServerTickScheduler;
import me.jellysquid.mods.lithium.common.world.scheduler.TickEntry;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.TickPriority;

import redstone.multimeter.interfaces.mixin.IServerLevel;

@Pseudo
@Mixin(LithiumServerTickScheduler.class)
public class LithiumServerTickSchedulerMixin {

	@Shadow @Final private ServerLevel world;
	@Shadow @Final private ObjectOpenHashSet<TickEntry<?>> scheduledTicks;

	private int rsmm$size;

	@Inject(
		method = "scheduleTick",
		at = @At(
			value = "HEAD"
		)
	)
	private void onSchedule(BlockPos pos, Object type, int delay, TickPriority priority, CallbackInfo ci) {
		rsmm$size = scheduledTicks.size();
	}

	@Inject(
		method = "scheduleTick",
		at = @At(
			value = "TAIL"
		)
	)
	private void logSchedule(BlockPos pos, Object type, int delay, TickPriority priority, CallbackInfo ci) {
		if (rsmm$size < scheduledTicks.size()) {
			((IServerLevel)world).getMultimeter().logScheduledTick(world, pos, priority, true);
		}
	}
}
