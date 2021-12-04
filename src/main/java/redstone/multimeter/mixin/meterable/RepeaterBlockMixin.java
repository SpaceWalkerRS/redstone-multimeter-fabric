package redstone.multimeter.mixin.meterable;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.block.AbstractRedstoneGateBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.RepeaterBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

import redstone.multimeter.block.MeterableBlock;
import redstone.multimeter.block.PowerSource;

@Mixin(RepeaterBlock.class)
public abstract class RepeaterBlockMixin extends AbstractRedstoneGateBlock implements MeterableBlock, PowerSource {
	
	protected RepeaterBlockMixin(boolean active) {
		super(active);
	}
	
	@Inject(
			method = "isLocked",
			at = @At(
					value = "RETURN"
			)
	)
	private void onIsLocked(BlockView blockView, BlockPos pos, BlockState state, CallbackInfoReturnable<Boolean> cir) {
		if (cir.getReturnValue() && blockView instanceof World) {
			World world = (World)blockView;
			
			if (!world.isClient) {
				logPowered(world, pos, hasPower(world, pos, state));
			}
		}
	}
	
	@Override
	public int getPowerLevel(World world, BlockPos pos, BlockState state) {
		return field_24310 ? MAX_POWER : MIN_POWER;
	}
}
