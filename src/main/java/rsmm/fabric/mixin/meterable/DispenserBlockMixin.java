package rsmm.fabric.mixin.meterable;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.DispenserBlock;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import rsmm.fabric.common.event.EventType;
import rsmm.fabric.interfaces.mixin.IBlock;
import rsmm.fabric.server.MeterableBlock;

@Mixin(DispenserBlock.class)
public class DispenserBlockMixin implements IBlock, MeterableBlock {
	
	@Inject(
			method = "neighborUpdate",
			locals = LocalCapture.CAPTURE_FAILHARD,
			at = @At(
					value = "FIELD",
					ordinal = 0,
					target = "Lnet/minecraft/block/DispenserBlock;TRIGGERED:Lnet/minecraft/state/property/BooleanProperty;"
			)
	)
	private void onNeighborUpdateInjectAtTriggered(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean notify, CallbackInfo ci, boolean powered) {
		logPowered(world, pos, powered);
	}
	
	@Override
	public int getDefaultMeteredEvents() {
		return EventType.POWERED.flag();
	}
	
	@Override
	public boolean standardIsPowered() {
		return false;
	}
	
	@Override
	public boolean isPowered(World world, BlockPos pos, BlockState state) {
		return world.isReceivingRedstonePower(pos) || world.isReceivingRedstonePower(pos.up());
	}
	
	@Override
	public boolean isActive(World world, BlockPos pos, BlockState state) {
		return state.get(Properties.TRIGGERED);
	}
}
