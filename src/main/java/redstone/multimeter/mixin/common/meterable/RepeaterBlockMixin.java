package redstone.multimeter.mixin.common.meterable;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.block.Block;
import net.minecraft.block.RepeaterBlock;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.WorldView;

import redstone.multimeter.block.MeterableBlock;

@Mixin(RepeaterBlock.class)
public abstract class RepeaterBlockMixin implements MeterableBlock {

	@Inject(
		method = "isLocked",
		at = @At(
			value = "RETURN"
		)
	)
	private void logPowered(WorldView world, int x, int y, int z, int metadata, CallbackInfoReturnable<Boolean> cir) {
		if (cir.getReturnValue() && world instanceof ServerWorld) {
			rsmm$logPowered((ServerWorld)world, x, y, z, ((Block)(Object)this).id, metadata);
		}
	}
}
