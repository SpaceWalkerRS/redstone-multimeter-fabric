package rsmm.fabric.mixin.meterable;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.block.BlockState;
import net.minecraft.block.HopperBlock;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import rsmm.fabric.common.event.EventType;
import rsmm.fabric.server.MeterableBlock;

@Mixin(HopperBlock.class)
public class HopperBlockMixin implements MeterableBlock {
	
	@Inject(
			method = "updateEnabled",
			locals = LocalCapture.CAPTURE_FAILHARD,
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/block/BlockState;get(Lnet/minecraft/state/property/Property;)Ljava/lang/Comparable;"
			)
	)
	private void onUpdateEnabledInjectAtGet(World world, BlockPos pos, BlockState state, CallbackInfo ci, boolean shouldBeEnabled) {
		logPowered(world, pos, !shouldBeEnabled);
	}
	
	@Override
	public boolean isActive(World world, BlockPos pos, BlockState state) {
		return state.get(Properties.ENABLED);
	}
	
	@Override
	public int getDefaultMeteredEvents() {
		return EventType.POWERED.flag();
	}
}
