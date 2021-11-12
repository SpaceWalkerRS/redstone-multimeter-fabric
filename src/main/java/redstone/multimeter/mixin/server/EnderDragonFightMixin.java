package redstone.multimeter.mixin.server;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.entity.boss.dragon.EnderDragonFight;
import net.minecraft.server.world.ServerWorld;

import redstone.multimeter.common.TickTask;
import redstone.multimeter.interfaces.mixin.IServerWorld;

@Mixin(EnderDragonFight.class)
public class EnderDragonFightMixin {
	
	@Shadow @Final ServerWorld world;
	
	@Inject(
			method = "tick",
			at = @At(
					value = "HEAD"
			)
	)
	private void startTickPhaseDragonFight(CallbackInfo ci) {
		((IServerWorld)world).startTickTask(TickTask.DRAGON_FIGHT);
	}
	
	@Inject(
			method = "tick",
			at = @At(
					value = "RETURN"
			)
	)
	private void endTickPhaseDragonFight(CallbackInfo ci) {
		((IServerWorld)world).endTickTask();
	}
}
