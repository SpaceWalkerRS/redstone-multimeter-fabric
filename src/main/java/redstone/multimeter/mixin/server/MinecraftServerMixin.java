package redstone.multimeter.mixin.server;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.server.MinecraftServer;

import redstone.multimeter.common.TickTask;
import redstone.multimeter.interfaces.mixin.IMinecraftServer;
import redstone.multimeter.server.MultimeterServer;

@Mixin(MinecraftServer.class)
public class MinecraftServerMixin implements IMinecraftServer {
	
	private MultimeterServer multimeterServer;
	
	@Inject(
			method = "<init>",
			at = @At(
					value = "INVOKE",
					shift = Shift.BEFORE,
					target = "Lnet/minecraft/server/MinecraftServer;method_33269()Lnet/minecraft/server/command/ServerCommandManager;"
			)
	)
	private void onInit(CallbackInfo ci) {
		this.multimeterServer = new MultimeterServer((MinecraftServer)(Object)this);
	}
	
	@Inject(
			method = "tick",
			at = @At(
					value = "HEAD"
			)
	)
	private void onTickStart(CallbackInfo ci) {
		multimeterServer.tickStart();
	}
	
	@Inject(
			method = "tickWorlds",
			at = @At(
					value = "INVOKE_STRING",
					target = "Lnet/minecraft/util/profiler/Profiler;push(Ljava/lang/String;)V",
					args = "ldc=jobs"
			)
	)
	private void startTickTaskPackets(CallbackInfo ci) {
		multimeterServer.startTickTask(TickTask.PACKETS);
	}
	
	@Inject(
			method = "tickWorlds",
			at = @At(
					value = "INVOKE_STRING",
					target = "Lnet/minecraft/util/profiler/Profiler;swap(Ljava/lang/String;)V",
					args = "ldc=levels"
			)
	)
	private void endTickTaskPackets(CallbackInfo ci) {
		multimeterServer.endTickTask();
	}
	
	@Inject(
			method = "tickWorlds",
			at = @At(
					value = "INVOKE_STRING",
					target = "Lnet/minecraft/util/profiler/Profiler;swap(Ljava/lang/String;)V",
					args = "ldc=commandFunctions"
			)
	)
	private void startTickTaskCommandFunctions(CallbackInfo ci) {
		multimeterServer.startTickTask(TickTask.COMMAND_FUNCTIONS);
	}
	
	@Inject(
			method = "tick",
			at = @At(
					value = "RETURN"
			)
	)
	private void endTickTaskPacketsAndEndTick(CallbackInfo ci) {
		multimeterServer.endTickTask();
		multimeterServer.tickEnd();
	}
	
	@Inject(
			method = "reloadResources",
			at = @At(
					value = "HEAD"
			)
	)
	private void onReload(CallbackInfo ci) {
		multimeterServer.getMultimeter().reloadOptions();
	}
	
	@Override
	public MultimeterServer getMultimeterServer() {
		return multimeterServer;
	}
	
	@Override
	public boolean isPaused() {
		return false;
	}
}
