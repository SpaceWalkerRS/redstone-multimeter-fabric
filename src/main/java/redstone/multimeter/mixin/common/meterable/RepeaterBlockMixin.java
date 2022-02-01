package redstone.multimeter.mixin.common.meterable;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.block.AbstractRedstoneGateBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.RepeaterBlock;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;

import redstone.multimeter.block.MeterableBlock;
import redstone.multimeter.block.PowerSource;

@Mixin(RepeaterBlock.class)
public abstract class RepeaterBlockMixin extends AbstractRedstoneGateBlock implements MeterableBlock, PowerSource {
	
	protected RepeaterBlockMixin(Settings settings) {
		super(settings);
	}
	
	@Inject(
			method = "isLocked",
			at = @At(
					value = "RETURN"
			)
	)
	private void onIsLocked(WorldView world, BlockPos pos, BlockState state, CallbackInfoReturnable<Boolean> cir) {
		if (cir.getReturnValue() && world instanceof ServerWorld) {
			logPoweredRSMM((ServerWorld)world, pos, state);
		}
	}
	
	@Override
	public int getPowerLevelRSMM(World world, BlockPos pos, BlockState state) {
		return state.get(Properties.POWERED) ? MAX_POWER : MIN_POWER;
	}
}
