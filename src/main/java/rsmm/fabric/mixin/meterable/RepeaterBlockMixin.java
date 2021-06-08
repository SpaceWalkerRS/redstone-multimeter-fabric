package rsmm.fabric.mixin.meterable;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.block.AbstractRedstoneGateBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.RepeaterBlock;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.CollisionView;
import net.minecraft.world.World;

import rsmm.fabric.block.MeterableBlock;
import rsmm.fabric.block.PowerSource;
import rsmm.fabric.common.event.EventType;
import rsmm.fabric.interfaces.mixin.IBlock;

@Mixin(RepeaterBlock.class)
public abstract class RepeaterBlockMixin extends AbstractRedstoneGateBlock implements IBlock, MeterableBlock, PowerSource {
	
	protected RepeaterBlockMixin(Settings settings) {
		super(settings);
	}
	
	@Inject(
			method = "isLocked",
			at = @At(
					value = "RETURN"
			)
	)
	private void onIsLockedInjectAtReturn(CollisionView world, BlockPos pos, BlockState state, CallbackInfoReturnable<Boolean> cir) {
		if (world instanceof World && !world.isClient() && cir.getReturnValue()) {
			logPowered((World)world, pos, hasPower((World)world, pos, state));
		}
	}
	
	@Override
	public int getDefaultMeteredEvents() {
		return EventType.ACTIVE.flag();
	}
	
	@Override
	public int getPowerLevel(World world, BlockPos pos, BlockState state) {
		return state.get(Properties.POWERED) ? MAX_POWER : 0;
	}
}
