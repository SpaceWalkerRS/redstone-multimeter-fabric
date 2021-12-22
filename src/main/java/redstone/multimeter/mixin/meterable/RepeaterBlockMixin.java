package redstone.multimeter.mixin.meterable;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.class_3599;
import net.minecraft.class_3772;
import net.minecraft.block.BlockState;
import net.minecraft.block.RedstoneComponentBlock;
import net.minecraft.block.RepeaterBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import redstone.multimeter.block.MeterableBlock;
import redstone.multimeter.block.PowerSource;

@Mixin(RepeaterBlock.class)
public abstract class RepeaterBlockMixin extends RedstoneComponentBlock implements MeterableBlock, PowerSource {
	
	protected RepeaterBlockMixin(class_3692 settings) {
		super(settings);
	}
	
	@Inject(
			method = "method_8722",
			at = @At(
					value = "RETURN"
			)
	)
	private void onIsLocked(class_3599 collisionView, BlockPos pos, BlockState state, CallbackInfoReturnable<Boolean> cir) {
		if (cir.getReturnValue() && collisionView instanceof World) {
			World world = (World)collisionView;
			
			if (!world.method_16390()) {
				logPowered(world, pos, method_8727(world, pos, state));
			}
		}
	}
	
	@Override
	public int getPowerLevel(World world, BlockPos pos, BlockState state) {
		return state.method_16934(class_3772.field_18783) ? MAX_POWER : MIN_POWER;
	}
}
