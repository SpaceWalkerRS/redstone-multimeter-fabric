package rsmm.fabric.mixin.meterable;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.block.AbstractRedstoneGateBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.RepeaterBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;

import rsmm.fabric.server.MeterableBlock;

@Mixin(RepeaterBlock.class)
public abstract class RepeaterBlockMixin extends AbstractRedstoneGateBlock implements MeterableBlock {
	
	protected RepeaterBlockMixin(Settings settings) {
		super(settings);
	}
	
	@Inject(
			method = "isLocked",
			at = @At(
					value = "RETURN"
			)
	)
	private void onIsLockedInjectAtReturn(WorldView world, BlockPos pos, BlockState state, CallbackInfoReturnable<Boolean> cir) {
		if (world instanceof World && !world.isClient() && cir.getReturnValue()) {
			logPowered((World)world, pos, hasPower((World)world, pos, state));
		}
	}
}
