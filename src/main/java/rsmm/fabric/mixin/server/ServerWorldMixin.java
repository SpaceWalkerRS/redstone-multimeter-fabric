package rsmm.fabric.mixin.server;

import java.util.function.BooleanSupplier;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.fluid.Fluid;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.BlockAction;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.ScheduledTick;
import net.minecraft.world.World;

import rsmm.fabric.common.TickPhase;
import rsmm.fabric.interfaces.mixin.IMinecraftServer;
import rsmm.fabric.interfaces.mixin.IServerWorld;
import rsmm.fabric.server.Multimeter;
import rsmm.fabric.server.MultimeterServer;

@Mixin(ServerWorld.class)
public abstract class ServerWorldMixin implements IServerWorld {
	
	@Shadow public abstract MinecraftServer getServer();
	
	@Inject(
			method = "tick",
			at = @At(
					value = "INVOKE_STRING",
					target = "Lnet/minecraft/util/profiler/Profiler;swap(Ljava/lang/String;)V",
					args = "ldc=weather"
			)
	)
	private void onTickInjectAtStringWeather(BooleanSupplier shouldKeepTicking, CallbackInfo ci) {
		getMultimeterServer().getMultimeter().onTickPhase(TickPhase.TICK_WEATHER);
	}
	
	@Inject(
			method = "tick",
			at = @At(
					value = "INVOKE_STRING",
					target = "Lnet/minecraft/util/profiler/Profiler;swap(Ljava/lang/String;)V",
					args = "ldc=chunkSource"
			)
	)
	private void onTickInjectAtStringChunkSource(BooleanSupplier shouldKeepTicking, CallbackInfo ci) {
		getMultimeterServer().getMultimeter().onTickPhase(TickPhase.TICK_CHUNKS);
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
					value = "INVOKE_STRING",
					target = "Lnet/minecraft/util/profiler/Profiler;swap(Ljava/lang/String;)V",
					args = "ldc=raid"
			)
	)
	private void onTickInjectAtStringRaid(BooleanSupplier shouldKeepTicking, CallbackInfo ci) {
		getMultimeterServer().getMultimeter().onTickPhase(TickPhase.TICK_RAIDS);
	}
	
	@Inject(
			method = "tick",
			at = @At(
					value = "INVOKE_STRING",
					target = "Lnet/minecraft/util/profiler/Profiler;swap(Ljava/lang/String;)V",
					args = "ldc=blockEvents"
			)
	)
	private void onTickInjectAtStringBlockEvents(BooleanSupplier shouldKeepTicking, CallbackInfo ci) {
		getMultimeterServer().getMultimeter().onTickPhase(TickPhase.PROCESS_BLOCK_EVENTS);
	}
	
	@Inject(
			method = "tick",
			at = @At(
					value = "INVOKE_STRING",
					target = "Lnet/minecraft/util/profiler/Profiler;swap(Ljava/lang/String;)V",
					args = "ldc=entities"
			)
	)
	private void onTickInjectAtStringEntities(BooleanSupplier shouldKeepTicking, CallbackInfo ci) {
		getMultimeterServer().getMultimeter().onTickPhase(TickPhase.TICK_ENTITIES);
	}
	
	@Inject(
			method = "tick",
			at = @At(
					value = "INVOKE",
					shift = Shift.BEFORE,
					target = "Lnet/minecraft/server/world/ServerWorld;tickBlockEntities()V"
			)
	)
	private void onTickInjectBeforeTickBlockEntities(BooleanSupplier shouldKeepTicking, CallbackInfo ci) {
		getMultimeterServer().getMultimeter().onTickPhase(TickPhase.TICK_BLOCK_ENTITIES);
	}
	
	@Inject(
			method = "tickFluid",
			at = @At(
					value = "INVOKE",
					shift = Shift.BEFORE,
					target = "Lnet/minecraft/fluid/FluidState;onScheduledTick(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;)V"
			)
	)
	private void onTickFluidInjectBeforeOnScheduledTick(ScheduledTick<Fluid> scheduledTick, CallbackInfo ci) {
		MultimeterServer server = getMultimeterServer();
		Multimeter multimeter = server.getMultimeter();
		
		multimeter.logScheduledTick((World)(Object)this, scheduledTick);
	}
	
	@Inject(
			method = "tickBlock",
			at = @At(
					value = "INVOKE",
					shift = Shift.BEFORE,
					target = "Lnet/minecraft/block/BlockState;scheduledTick(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Ljava/util/Random;)V"
			)
	)
	private void onTickBlockInjectBeforeScheduledTick(ScheduledTick<Block> scheduledTick, CallbackInfo ci) {
		MultimeterServer server = getMultimeterServer();
		Multimeter multimeter = server.getMultimeter();
		
		multimeter.logScheduledTick((World)(Object)this, scheduledTick);
	}
	
	@Inject(
			method = "tickEntity",
			at = @At(
					value = "INVOKE",
					shift = Shift.BEFORE,
					target = "Lnet/minecraft/entity/Entity;tick()V"
			)
	)
	private void onTickEntityInjectBeforeTick(Entity entity, CallbackInfo ci) {
		MultimeterServer server = getMultimeterServer();
		Multimeter multimeter = server.getMultimeter();
		
		multimeter.logEntityTick((World)(Object)this, entity);
	}
	
	@Inject(
			method = "method_14174",
			at = @At(
					value = "INVOKE",
					shift = Shift.BEFORE,
					target = "Lnet/minecraft/block/BlockState;onBlockAction(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;II)Z"
			)
	)
	private void onMethod_14174InjectBeforeOnSyncedBlockEvent(BlockAction blockAction, CallbackInfoReturnable<Boolean> cir) {
		MultimeterServer server = getMultimeterServer();
		Multimeter multimeter = server.getMultimeter();
		
		multimeter.logBlockEvent((World)(Object)this, blockAction);
	}
	
	@Override
	public MultimeterServer getMultimeterServer() {
		return ((IMinecraftServer)getServer()).getMultimeterServer();
	}
}
