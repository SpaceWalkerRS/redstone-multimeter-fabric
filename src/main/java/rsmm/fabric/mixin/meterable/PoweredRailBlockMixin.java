package rsmm.fabric.mixin.meterable;

import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.PoweredRailBlock;
import net.minecraft.block.enums.RailShape;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import rsmm.fabric.common.event.EventType;
import rsmm.fabric.interfaces.mixin.IBlock;
import rsmm.fabric.server.MeterableBlock;

@Mixin(PoweredRailBlock.class)
public abstract class PoweredRailBlockMixin implements MeterableBlock, IBlock {
	
	@Shadow protected abstract boolean isPoweredByOtherRails(World world, BlockPos pos, BlockState state, boolean boolean4, int distance);
	@Shadow protected abstract boolean isPoweredByOtherRails(World world, BlockPos pos, boolean bl, int distance, RailShape shape);
	
	@Inject(
			method = "neighborUpdate",
			locals = LocalCapture.CAPTURE_FAILHARD,
			at = @At(
					value = "JUMP",
					ordinal = 0,
					shift = Shift.BEFORE,
					opcode = Opcodes.IFNE
			)
	)
	private void onNeighborUpdateInjectBeforePowered0(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean notify, CallbackInfo ci, boolean active, boolean powered) {
		logPowered(world, pos, powered);
	}
	
	@Override
	public boolean isActive(World world, BlockPos pos, BlockState state) {
		return state.get(Properties.POWERED);
	}
	
	@Override
	public int getDefaultMeteredEvents() {
		return EventType.ACTIVE.flag() | EventType.MOVED.flag();
	}
	
	@Override
	public boolean standardIsPowered() {
		return false;
	}
	
	@Override
	public boolean isPowered(World world, BlockPos pos, BlockState state) {
		return world.isReceivingRedstonePower(pos) || isPoweredByOtherRails(world, pos, state, true, 0) || isPoweredByOtherRails(world, pos, state, false, 0);
	}
}
