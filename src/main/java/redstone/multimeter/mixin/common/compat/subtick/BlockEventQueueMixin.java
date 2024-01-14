package redstone.multimeter.mixin.common.compat.subtick;

import org.apache.commons.lang3.tuple.Triple;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;

import redstone.multimeter.interfaces.mixin.BlockEventListener;

import subtick.queues.BlockEventQueue;

@Pseudo
@Mixin(BlockEventQueue.class)
public class BlockEventQueueMixin {

	private BlockEventListener rsmm$listener;

	@Inject(
		method = "<init>",
		remap = false,
		at = @At(
			value = "TAIL"
		)
	)
	private void init(ServerLevel level, CallbackInfo ci) {
		this.rsmm$listener = (BlockEventListener)level;
	}

	@Inject(
		method = "start",
		remap = false,
		at = @At(
			value = "HEAD"
		)
	)
	private void start(CallbackInfo ci) {
		if (rsmm$listener != null) {
			rsmm$listener.rsmm$startBlockEvents();
		}
	}

	@Inject(
		method = "step",
		remap = false,
		at = @At(
			value = "INVOKE",
			target = "Lit/unimi/dsi/fastutil/objects/ObjectLinkedOpenHashSet;removeFirst()Ljava/lang/Object;"
		)
	)
	private void next(int count, BlockPos pos, int range, CallbackInfoReturnable<Triple<Integer, Integer, Boolean>> cir) {
		if (rsmm$listener != null) {
			rsmm$listener.rsmm$nextBlockEvent();
		}
	}

	@Inject(
		method = "end",
		remap = false,
		at = @At(
			value = "HEAD"
		)
	)
	private void end(CallbackInfo ci) {
		if (rsmm$listener != null) {
			rsmm$listener.rsmm$endBlockEvents();
		}
	}
}
