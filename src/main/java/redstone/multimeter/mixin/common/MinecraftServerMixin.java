package redstone.multimeter.mixin.common;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.server.MinecraftServer;

import redstone.multimeter.common.TickTask;
import redstone.multimeter.interfaces.mixin.IMinecraftServer;
import redstone.multimeter.server.MultimeterServer;

@Mixin(MinecraftServer.class)
public class MinecraftServerMixin implements IMinecraftServer {
	
	private MultimeterServer multimeterServer;
	
	@Inject(
			method = "tick",
			at = @At(
					value = "HEAD"
			)
	)
	private void startTickTaskTick(CallbackInfo ci) {
		multimeterServer.tickStart();
		startTickTaskRSMM(TickTask.TICK);
	}
	
	@Inject(
			method = "tick",
			at = @At(
					value = "INVOKE_STRING",
					target = "Lnet/minecraft/profiler/Profiler;startSection(Ljava/lang/String;)V",
					args = "ldc=save"
			)
	)
	private void startTickTaskAutosave(CallbackInfo ci) {
		startTickTaskRSMM(TickTask.AUTOSAVE);
	}
	
	@Inject(
			method = "tick",
			slice = @Slice(
					from = @At(
							value = "INVOKE_STRING",
							target = "Lnet/minecraft/profiler/Profiler;startSection(Ljava/lang/String;)V",
							args = "ldc=save"
					)
			),
			at = @At(
					value = "INVOKE",
					ordinal = 0,
					target = "Lnet/minecraft/profiler/Profiler;endSection()V"
			)
	)
	private void endTickTaskAutosave(CallbackInfo ci) {
		endTickTaskRSMM();
	}
	
	@Inject(
			method = "tick",
			at = @At(
					value = "RETURN"
			)
	)
	private void endTickTaskTick(CallbackInfo ci) {
		endTickTaskRSMM();
		multimeterServer.tickEnd();
	}
	
	@Inject(
			method = "updateTimeLightAndEntities",
			at = @At(
					value = "INVOKE_STRING",
					target = "Lnet/minecraft/profiler/Profiler;startSection(Ljava/lang/String;)V",
					args = "ldc=jobs"
			)
	)
	private void startTickTaskPackets(CallbackInfo ci) {
		startTickTaskRSMM(TickTask.PACKETS);
	}
	
	@Inject(
			method = "updateTimeLightAndEntities",
			at = @At(
					value = "INVOKE_STRING",
					target = "Lnet/minecraft/profiler/Profiler;endStartSection(Ljava/lang/String;)V",
					args = "ldc=levels"
			)
	)
	private void swapTickTaskLevels(CallbackInfo ci) {
		swapTickTaskRSMM(TickTask.LEVELS);
	}
	
	@Inject(
			method = "updateTimeLightAndEntities",
			at = @At(
					value = "INVOKE_STRING",
					target = "Lnet/minecraft/profiler/Profiler;endStartSection(Ljava/lang/String;)V",
					args = "ldc=connection"
			)
	)
	private void swapTickTaskConnections(CallbackInfo ci) {
		swapTickTaskRSMM(TickTask.CONNECTIONS);
	}
	
	@Inject(
			method = "updateTimeLightAndEntities",
			at = @At(
					value = "INVOKE_STRING",
					target = "Lnet/minecraft/profiler/Profiler;endStartSection(Ljava/lang/String;)V",
					args = "ldc=players"
			)
	)
	private void swapTickTaskPlayerPing(CallbackInfo ci) {
		swapTickTaskRSMM(TickTask.PLAYER_PING);
	}
	
	@Inject(
			method = "updateTimeLightAndEntities",
			at = @At(
					value = "INVOKE_STRING",
					target = "Lnet/minecraft/profiler/Profiler;endStartSection(Ljava/lang/String;)V",
					args = "ldc=commandFunctions"
			)
	)
	private void swapTickTaskCommandFunctions(CallbackInfo ci) {
		swapTickTaskRSMM(TickTask.COMMAND_FUNCTIONS);
	}
	
	@Inject(
			method = "updateTimeLightAndEntities",
			at = @At(
					value = "INVOKE_STRING",
					target = "Lnet/minecraft/profiler/Profiler;endStartSection(Ljava/lang/String;)V",
					args = "ldc=tickables"
			)
	)
	private void swapTickTaskServerGui(CallbackInfo ci) {
		swapTickTaskRSMM(TickTask.SERVER_GUI);
	}
	
	@Inject(
			method = "updateTimeLightAndEntities",
			at = @At(
					value = "RETURN"
			)
	)
	private void endTickTaskServerGui(CallbackInfo ci) {
		endTickTaskRSMM();
	}
	
	@Inject(
			method = "reload",
			at = @At(
					value = "INVOKE",
					shift = Shift.BEFORE,
					target = "Lnet/minecraft/server/management/PlayerList;saveAllPlayerData()V"
			)
	)
	private void onReloadResources(CallbackInfo ci) {
		multimeterServer.getMultimeter().reloadOptions();
	}
	
	@Override
	public MultimeterServer getMultimeterServer() {
		if (multimeterServer == null) {
			multimeterServer = new MultimeterServer((MinecraftServer)(Object)this);
		}
		
		return multimeterServer;
	}
	
	@Override
	public boolean isPausedRSMM() {
		return false;
	}
}
