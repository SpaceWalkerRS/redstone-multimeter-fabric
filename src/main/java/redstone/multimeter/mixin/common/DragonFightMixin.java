package redstone.multimeter.mixin.common;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.dimension.end.DragonFight;

import redstone.multimeter.common.TickTask;
import redstone.multimeter.interfaces.mixin.IServerWorld;

@Mixin(DragonFight.class)
public class DragonFightMixin {

	@Shadow @Final private ServerWorld world;

	@Inject(
		method = "tick",
		at = @At(
			value = "HEAD"
		)
	)
	private void startTickTaskDragonFight(CallbackInfo ci) {
		((IServerWorld)world).rsmm$startTickTask(TickTask.DRAGON_FIGHT);
	}

	@Inject(
		method = "tick",
		at = @At(
			value = "RETURN"
		)
	)
	private void endTickTaskDragonFight(CallbackInfo ci) {
		((IServerWorld)world).rsmm$endTickTask();
	}
}
