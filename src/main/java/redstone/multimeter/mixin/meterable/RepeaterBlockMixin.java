package redstone.multimeter.mixin.meterable;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.block.BlockState;
import net.minecraft.block.RedstoneComponentBlock;
import net.minecraft.block.RepeaterBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;

import redstone.multimeter.block.MeterableBlock;
import redstone.multimeter.block.PowerSource;

@Mixin(RepeaterBlock.class)
public abstract class RepeaterBlockMixin extends RedstoneComponentBlock implements MeterableBlock, PowerSource {
	
	protected RepeaterBlockMixin(boolean active) {
		super(active);
	}
	
	@Inject(
			method = "method_789",
			at = @At(
					value = "RETURN"
			)
	)
	private void onIsLocked(WorldView worldView, BlockPos pos, BlockState state, CallbackInfoReturnable<Boolean> cir) {
		if (cir.getReturnValue() && worldView instanceof World) {
			World world = (World)worldView;
			
			if (!world.isClient) {
				logPowered(world, pos, method_796(world, pos, state));
			}
		}
	}
	
	@Override
	public int getPowerLevel(World world, BlockPos pos, BlockState state) {
		return field_851 ? MAX_POWER : MIN_POWER;
	}
}
