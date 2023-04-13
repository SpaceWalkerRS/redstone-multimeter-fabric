package redstone.multimeter.mixin.common;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.function.BooleanSupplier;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.server.MinecraftServer;

import redstone.multimeter.common.TickTask;
import redstone.multimeter.interfaces.mixin.IMinecraftServer;
import redstone.multimeter.server.MultimeterServer;

@Mixin(MinecraftServer.class)
public class MinecraftServerMixin implements IMinecraftServer {

	private MultimeterServer multimeterServer;

	@Inject(
		method = "loadLevel",
		at = @At(
			value = "TAIL"
		)
	)
	private void postInit(CallbackInfo ci) {
		this.multimeterServer = new MultimeterServer((MinecraftServer)(Object)this);
	}

	@Inject(
		method = "waitUntilNextTick",
		at = @At(
			value = "HEAD"
		)
	)
	private void startTickTaskPackets(CallbackInfo ci) {
		rsmm$startTickTask(TickTask.PACKETS);
	}

	@Inject(
		method = "waitUntilNextTick",
		at = @At(
			value = "TAIL"
		)
	)
	private void endTickTaskPackets(CallbackInfo ci) {
		rsmm$endTickTask();
	}

	@Inject(
		method = "tickServer",
		at = @At(
			value = "HEAD"
		)
	)
	private void startTickTaskTick(BooleanSupplier hasTimeLeft, CallbackInfo ci) {
		rsmm$startTickTask(TickTask.TICK);
	}

	@Inject(
		method = "tickServer",
		at = @At(
			value = "INVOKE_STRING",
			target = "Lorg/slf4j/Logger;debug(Ljava/lang/String;)V",
			args = "ldc=Autosave started"
		)
	)
	private void startTickTaskAutosave(BooleanSupplier hasTimeLeft, CallbackInfo ci) {
		rsmm$startTickTask(TickTask.AUTOSAVE);
	}

	@Inject(
		method = "tickServer",
		at = @At(
			value = "INVOKE_STRING",
			target = "Lorg/slf4j/Logger;debug(Ljava/lang/String;)V",
			args = "ldc=Autosave finished"
		)
	)
	private void endTickTaskAutosave(BooleanSupplier hasTimeLeft, CallbackInfo ci) {
		rsmm$endTickTask();
	}

	@Inject(
		method = "tickServer",
		at = @At(
			value = "TAIL"
		)
	)
	private void endTickTaskTick(BooleanSupplier hasTimeLeft, CallbackInfo ci) {
		rsmm$endTickTask();
	}

	@Inject(
		method = "tickChildren",
		at = @At(
			value = "INVOKE_STRING",
			target = "Lnet/minecraft/util/profiling/ProfilerFiller;push(Ljava/lang/String;)V",
			args = "ldc=commandFunctions"
		)
	)
	private void startTickTaskCommandFunctions(BooleanSupplier hasTimeLeft, CallbackInfo ci) {
		rsmm$startTickTask(TickTask.COMMAND_FUNCTIONS);
	}

	@Inject(
		method = "tickChildren",
		at = @At(
			value = "INVOKE_STRING",
			target = "Lnet/minecraft/util/profiling/ProfilerFiller;popPush(Ljava/lang/String;)V",
			args = "ldc=levels"
		)
	)
	private void swapTickTaskLevels(BooleanSupplier hasTimeLeft, CallbackInfo ci) {
		rsmm$swapTickTask(TickTask.LEVELS);
	}

	@Inject(
		method = "tickChildren",
		at = @At(
			value = "INVOKE_STRING",
			target = "Lnet/minecraft/util/profiling/ProfilerFiller;popPush(Ljava/lang/String;)V",
			args = "ldc=connection"
		)
	)
	private void swapTickTaskConnections(BooleanSupplier hasTimeLeft, CallbackInfo ci) {
		rsmm$swapTickTask(TickTask.CONNECTIONS);
	}

	@Inject(
		method = "tickChildren",
		at = @At(
			value = "INVOKE_STRING",
			target = "Lnet/minecraft/util/profiling/ProfilerFiller;popPush(Ljava/lang/String;)V",
			args = "ldc=players"
			)
		)
	private void swapTickTaskPlayerPing(BooleanSupplier hasTimeLeft, CallbackInfo ci) {
		rsmm$swapTickTask(TickTask.PLAYER_PING);
	}

	@Inject(
		method = "tickChildren",
		at = @At(
			value = "INVOKE_STRING",
			target = "Lnet/minecraft/util/profiling/ProfilerFiller;popPush(Ljava/lang/String;)V",
			args = "ldc=server gui refresh"
		)
	)
	private void swapTickTaskServerGui(BooleanSupplier hasTimeLeft, CallbackInfo ci) {
		rsmm$swapTickTask(TickTask.SERVER_GUI);
	}

	@Inject(
		method = "tickChildren",
		at = @At(
			value = "TAIL"
		)
	)
	private void endTickTaskServerGui(BooleanSupplier hasTimeLeft, CallbackInfo ci) {
		rsmm$endTickTask();
	}

	@Inject(
		method = "reloadResources",
		at = @At(
			value = "HEAD"
		)
	)
	private void onReloadResources(Collection<String> datapacks, CallbackInfoReturnable<CompletableFuture<Void>> cir) {
		((MinecraftServer)(Object)this).execute(() -> multimeterServer.getMultimeter().reloadOptions());
	}

	@Inject(
		method = "startMetricsRecordingTick",
		at = @At(
			value = "HEAD"
		)
	)
	private void onTickStart(CallbackInfo ci) {
		multimeterServer.tickStart();
	}

	@Inject(
		method = "endMetricsRecordingTick",
		at = @At(
			value = "TAIL"
		)
	)
	private void onTickEnd(CallbackInfo ci) {
		multimeterServer.tickEnd();
	}

	@Override
	public MultimeterServer getMultimeterServer() {
		return multimeterServer;
	}

	@Override
	public boolean rsmm$isPaused() {
		return false;
	}
}
