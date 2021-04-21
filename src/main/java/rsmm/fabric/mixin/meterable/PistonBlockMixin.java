package rsmm.fabric.mixin.meterable;

import java.util.List;
import java.util.Set;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.block.BlockState;
import net.minecraft.block.PistonBlock;
import net.minecraft.block.piston.PistonHandler;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import rsmm.fabric.common.event.EventType;
import rsmm.fabric.interfaces.mixin.IBlock;
import rsmm.fabric.interfaces.mixin.IServerWorld;
import rsmm.fabric.server.MeterableBlock;
import rsmm.fabric.server.Multimeter;
import rsmm.fabric.server.MultimeterServer;

@Mixin(PistonBlock.class)
public abstract class PistonBlockMixin implements MeterableBlock, IBlock {
	
	@Shadow protected abstract boolean shouldExtend(World world, BlockPos pos, Direction facing);
	
	@Inject(
			method = "shouldExtend",
			at = @At(
					value = "RETURN"
			)
	)
	private void onShouldExtendInjectAtReturn(World world, BlockPos pos, Direction facing, CallbackInfoReturnable<Boolean> cir) {
		onBlockUpdate(world, pos, cir.getReturnValue());
	}
	
	@Inject(
			method = "move",
			locals = LocalCapture.CAPTURE_FAILHARD,
			at = @At(
					value = "INVOKE",
					ordinal = 1,
					shift = Shift.BEFORE,
					target = "Lnet/minecraft/util/math/BlockPos;offset(Lnet/minecraft/util/math/Direction;)Lnet/minecraft/util/math/BlockPos;"
			)
	)
	private void onMoveInjectBeforeOffset1(World world, BlockPos pistonPos, Direction facing, boolean extend, CallbackInfoReturnable<Boolean> cir, BlockPos headPos, PistonHandler pistonHandler, List<BlockPos> movedPositions, List<BlockState> movedStates, List<BlockPos> brokenPositions, int affectedIndex, BlockState[] removedStates, Direction moveDir, Set<BlockPos> removedPositions, int index, BlockPos movedPos, BlockState movedState) {
		if (!world.isClient()) {
			MultimeterServer server = ((IServerWorld)world).getMultimeterServer();
			Multimeter multimeter = server.getMultimeter();
			
			multimeter.blockMoved(world, movedPos, moveDir);
		}
	}
	
	@Override
	public boolean isActive(World world, BlockPos pos, BlockState state) {
		return state.get(Properties.EXTENDED);
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
		return shouldExtend(world, pos, state.get(Properties.FACING));
	}
}
