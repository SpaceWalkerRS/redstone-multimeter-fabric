package rsmm.fabric.mixin.server;

import java.util.function.BooleanSupplier;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import rsmm.fabric.common.TickPhase;
import rsmm.fabric.interfaces.mixin.IMinecraftServer;
import rsmm.fabric.interfaces.mixin.IServerWorld;
import rsmm.fabric.server.MultimeterServer;

@Mixin(ServerWorld.class)
public abstract class ServerWorldMixin implements IServerWorld {
	
	@Shadow public abstract MinecraftServer getServer();
	
	@Inject(
			method = "tick",
			at = @At(
					value = "INVOKE",
					shift = Shift.BEFORE,
					target = "Lnet/minecraft/world/dimension/DimensionType;hasSkyLight()Z"
			)
	)
	private void onTickInjectBeforeHasSkyLight(BooleanSupplier shouldKeepTicking, CallbackInfo ci) {
		getMultimeterServer().getMultimeter().onTickPhase(TickPhase.TICK_WEATHER);
	}
	
	@Inject(
			method = "tick",
			at = @At(
					value = "FIELD",
					shift = Shift.BEFORE,
					target = "Lnet/minecraft/server/world/ServerWorld;blockTickScheduler:Lnet/minecraft/server/world/ServerTickScheduler;"
			)
	)
	private void onTickInjectBeforeBlockTickScheduler(BooleanSupplier shouldKeepTicking, CallbackInfo ci) {
		getMultimeterServer().getMultimeter().onTickPhase(TickPhase.TICK_BLOCKS);
	}
	
	@Inject(
			method = "tick",
			at = @At(
					value = "FIELD",
					shift = Shift.BEFORE,
					target = "Lnet/minecraft/server/world/ServerWorld;fluidTickScheduler:Lnet/minecraft/server/world/ServerTickScheduler;"
			)
	)
	private void onTickInjectBeforeFluidTickScheduler(BooleanSupplier shouldKeepTicking, CallbackInfo ci) {
		getMultimeterServer().getMultimeter().onTickPhase(TickPhase.TICK_FLUIDS);
	}
	
	@Inject(
			method = "tick",
			at = @At(
					value = "FIELD",
					shift = Shift.BEFORE,
					target = "Lnet/minecraft/server/world/ServerWorld;idleTimeout:I"
			)
	)
	private void onTickInjectBeforeIdleTimeout(BooleanSupplier shouldKeepTicking, CallbackInfo ci) {
		getMultimeterServer().getMultimeter().onTickPhase(TickPhase.TICK_ENTITIES);
	}
	
	@Inject(
			method = "processSyncedBlockEvents",
			at = @At(
					value = "HEAD"
			)
	)
	private void onProcessSyncedBlockEventsInjectAtHead(CallbackInfo ci) {
		getMultimeterServer().getMultimeter().onTickPhase(TickPhase.PROCESS_BLOCK_EVENTS);
	}
	
	@Override
	public MultimeterServer getMultimeterServer() {
		return ((IMinecraftServer)getServer()).getMultimeterServer();
	}
}
