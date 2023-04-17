package redstone.multimeter.mixin.common;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

import redstone.multimeter.interfaces.mixin.IServerLevel;

@Mixin(targets = "net.minecraft.world.level.chunk.LevelChunk$BoundTickingBlockEntity")
public class BoundTickingBlockEntityMixin {

	@Shadow @Final private BlockEntity blockEntity;

	@Inject(
		method = "tick()V",
		at = @At(
			value = "INVOKE",
			shift = Shift.BEFORE,
			target = "Lnet/minecraft/world/level/block/entity/BlockEntityTicker;tick(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/block/entity/BlockEntity;)V"
		)
	)
	private void logBlockEntityTick(CallbackInfo ci) {
		Level level = blockEntity.getLevel();

		if (!level.isClientSide()) {
			((IServerLevel)level).getMultimeter().logBlockEntityTick(level, blockEntity);
		}
	}
}
