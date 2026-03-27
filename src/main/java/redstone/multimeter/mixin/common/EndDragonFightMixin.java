package redstone.multimeter.mixin.common;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.dimension.end.EnderDragonFight;

import redstone.multimeter.common.TickTask;
import redstone.multimeter.interfaces.mixin.IServerLevel;

@Mixin(EnderDragonFight.class)
public class EndDragonFightMixin {

	@Shadow @Final private ServerLevel level;

	@Inject(
		method = "tick",
		at = @At(
			value = "HEAD"
		)
	)
	private void startTickTaskDragonFight(CallbackInfo ci) {
		((IServerLevel)level).rsmm$startTickTask(TickTask.DRAGON_FIGHT);
	}

	@Inject(
		method = "tick",
		at = @At(
			value = "RETURN"
		)
	)
	private void endTickTaskDragonFight(CallbackInfo ci) {
		((IServerLevel)level).rsmm$endTickTask();
	}
}
