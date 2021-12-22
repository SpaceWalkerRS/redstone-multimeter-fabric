package redstone.multimeter.mixin.server;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.class_2752;
import net.minecraft.server.world.ServerWorld;

import redstone.multimeter.common.TickTask;
import redstone.multimeter.interfaces.mixin.IServerWorld;

@Mixin(class_2752.class)
public class EnderDragonFightMixin {
	
	@Shadow @Final ServerWorld field_12937;
	
	@Inject(
			method = "method_11805",
			at = @At(
					value = "HEAD"
			)
	)
	private void startTickTaskDragonFight(CallbackInfo ci) {
		((IServerWorld)field_12937).startTickTask(TickTask.DRAGON_FIGHT);
	}
	
	@Inject(
			method = "method_11805",
			at = @At(
					value = "RETURN"
			)
	)
	private void endTickTaskDragonFight(CallbackInfo ci) {
		((IServerWorld)field_12937).endTickTask();
	}
}
