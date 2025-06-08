package redstone.multimeter.mixin.common;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.server.MinecraftServer;

import redstone.multimeter.common.TickTask;
import redstone.multimeter.interfaces.mixin.IMinecraftServer;
import redstone.multimeter.server.MultimeterServer;
import redstone.multimeter.util.Dimensions;

@Mixin(MinecraftServer.class)
public class MinecraftServerMixin implements IMinecraftServer {

	private MultimeterServer multimeterServer;

	@Shadow
	private boolean isDedicated() { return false; }

	@Inject(
		method = "<init>(Ljava/io/File;Ljava/net/Proxy;)V",
		at = @At(
			value = "NEW",
			target = "Lnet/minecraft/server/command/handler/CommandManager;"
		)
	)
	private void init(CallbackInfo ci) {
		if (isDedicated()) {
			Dimensions.setUp();
		}

		this.multimeterServer = new MultimeterServer((MinecraftServer)(Object)this);
	}

	@Inject(
		method = "prepareWorlds",
		at = @At(
			value = "TAIL"
		)
	)
	private void worldLoaded(CallbackInfo ci) {
		multimeterServer.worldLoaded();
	}

	@Inject(
		method = "tick",
		at = @At(
			value = "HEAD"
		)
	)
	private void onTickStartAndStartTickTaskTick(CallbackInfo ci) {
		if (isDedicated()) {
			multimeterServer.tickStart();
		}

		rsmm$startTickTask(TickTask.TICK);
	}

	@Inject(
		method = "tick",
		at = @At(
			value = "INVOKE_STRING",
			target = "Lnet/minecraft/util/profiler/Profiler;push(Ljava/lang/String;)V",
			args = "ldc=save"
		)
	)
	private void startTickTaskAutosave(CallbackInfo ci) {
		rsmm$startTickTask(TickTask.AUTOSAVE);
	}

	@Inject(
		method = "tick",
		slice = @Slice(
			from = @At(
				value = "INVOKE_STRING",
				target = "Lnet/minecraft/util/profiler/Profiler;push(Ljava/lang/String;)V",
				args = "ldc=save"
			)
		),
		at = @At(
			value = "INVOKE",
			ordinal = 0,
			target = "Lnet/minecraft/util/profiler/Profiler;pop()V"
		)
	)
	private void endTickTaskAutosave(CallbackInfo ci) {
		rsmm$endTickTask();
	}

	@Inject(
		method = "tick",
		at = @At(
			value = "TAIL"
		)
	)
	private void endTickTaskTickAndOnTickEnd(CallbackInfo ci) {
		rsmm$endTickTask();

		if (isDedicated()) {
			multimeterServer.tickEnd();
		}
	}

	@Inject(
		method = "tickWorlds",
		at = @At(
			value = "INVOKE_STRING",
			target = "Lnet/minecraft/util/profiler/Profiler;push(Ljava/lang/String;)V",
			args = "ldc=levels"
		)
	)
	private void startTickTaskLevels(CallbackInfo ci) {
		rsmm$startTickTask(TickTask.LEVELS);
	}

	@Inject(
		method = "tickWorlds",
		at = @At(
			value = "INVOKE_STRING",
			target = "Lnet/minecraft/util/profiler/Profiler;swap(Ljava/lang/String;)V",
			args = "ldc=connection"
		)
	)
	private void swapTickTaskConnections(CallbackInfo ci) {
		rsmm$swapTickTask(TickTask.CONNECTIONS);
	}

	@Inject(
		method = "tickWorlds",
		at = @At(
			value = "INVOKE_STRING",
			target = "Lnet/minecraft/util/profiler/Profiler;swap(Ljava/lang/String;)V",
			args = "ldc=players"
			)
		)
	private void swapTickTaskPlayerPing(CallbackInfo ci) {
		rsmm$swapTickTask(TickTask.PLAYER_PING);
	}

	@Inject(
		method = "tickWorlds",
		at = @At(
			value = "INVOKE_STRING",
			target = "Lnet/minecraft/util/profiler/Profiler;swap(Ljava/lang/String;)V",
			args = "ldc=tickables"
		)
	)
	private void swapTickTaskServerGui(CallbackInfo ci) {
		rsmm$swapTickTask(TickTask.SERVER_GUI);
	}

	@Inject(
		method = "tickWorlds",
		at = @At(
			value = "TAIL"
		)
	)
	private void endTickTaskServerGui(CallbackInfo ci) {
		rsmm$endTickTask();
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
