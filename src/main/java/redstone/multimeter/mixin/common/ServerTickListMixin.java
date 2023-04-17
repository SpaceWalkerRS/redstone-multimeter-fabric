package redstone.multimeter.mixin.common;

import java.util.Set;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ServerTickList;
import net.minecraft.world.level.TickNextTickData;
import net.minecraft.world.level.TickPriority;

import redstone.multimeter.interfaces.mixin.IServerLevel;

@Mixin(ServerTickList.class)
public class ServerTickListMixin {

	@Shadow @Final private ServerLevel level;
	@Shadow @Final private Set<TickNextTickData<?>> tickNextTickSet;

	private int rsmm$size;

	@Inject(
		method = "scheduleTick",
		at = @At(
			value = "HEAD"
		)
	)
	private void onSchedule(BlockPos pos, Object type, int delay, TickPriority priority, CallbackInfo ci) {
		rsmm$size = tickNextTickSet.size();
	}

	@Inject(
		method = "scheduleTick",
		at = @At(
			value = "TAIL"
		)
	)
	private void logSchedule(BlockPos pos, Object type, int delay, TickPriority priority, CallbackInfo ci) {
		if (rsmm$size < tickNextTickSet.size()) {
			((IServerLevel)level).getMultimeter().logScheduledTick(level, pos, priority, true);
		}
	}
}
