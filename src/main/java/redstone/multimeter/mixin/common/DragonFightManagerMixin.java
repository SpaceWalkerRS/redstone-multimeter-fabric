package redstone.multimeter.mixin.common;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.world.WorldServer;
import net.minecraft.world.end.DragonFightManager;

import redstone.multimeter.common.TickTask;
import redstone.multimeter.interfaces.mixin.IWorldServer;

@Mixin(DragonFightManager.class)
public class DragonFightManagerMixin {
	
	@Shadow @Final WorldServer world;
	
	@Inject(
			method = "tick",
			at = @At(
					value = "HEAD"
			)
	)
	private void startTickTaskDragonFight(CallbackInfo ci) {
		((IWorldServer)world).startTickTaskRSMM(TickTask.DRAGON_FIGHT);
	}
	
	@Inject(
			method = "tick",
			at = @At(
					value = "RETURN"
			)
	)
	private void endTickTaskDragonFight(CallbackInfo ci) {
		((IWorldServer)world).endTickTaskRSMM();
	}
}
