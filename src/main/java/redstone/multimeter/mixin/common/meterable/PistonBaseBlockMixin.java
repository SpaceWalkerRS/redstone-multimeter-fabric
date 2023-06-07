package redstone.multimeter.mixin.common.meterable;

import java.util.List;
import java.util.Map;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.SignalGetter;
import net.minecraft.world.level.block.piston.PistonBaseBlock;
import net.minecraft.world.level.block.piston.PistonStructureResolver;
import net.minecraft.world.level.block.state.BlockState;

import redstone.multimeter.block.MeterableBlock;
import redstone.multimeter.interfaces.mixin.IServerLevel;
import redstone.multimeter.server.Multimeter;

@Mixin(PistonBaseBlock.class)
public class PistonBaseBlockMixin implements MeterableBlock {

	@Shadow private boolean getNeighborSignal(SignalGetter level, BlockPos pos, Direction facing) { return false; }

	@Inject(
		method = "getNeighborSignal",
		at = @At(
			value = "RETURN"
		)
	)
	private void logPowered(SignalGetter level, BlockPos pos, Direction facing, CallbackInfoReturnable<Boolean> cir) {
		if (level instanceof Level) {
			rsmm$logPowered((Level)level, pos, cir.getReturnValue());
		}
	}

	@Inject(
		method = "moveBlocks",
		locals = LocalCapture.CAPTURE_FAILHARD,
		at = @At(
			value = "INVOKE",
			ordinal = 1,
			shift = Shift.BEFORE,
			target = "Lnet/minecraft/core/BlockPos;relative(Lnet/minecraft/core/Direction;)Lnet/minecraft/core/BlockPos;"
		)
	)
	private void logMoved(Level level, BlockPos pos, Direction facing, boolean extending, CallbackInfoReturnable<Boolean> cir, BlockPos headPos, PistonStructureResolver structureResolver, Map<BlockPos, BlockState> leftOverStates, List<BlockPos> toMove, List<BlockState> statesToMove, List<BlockPos> toDestroy, BlockState[] removedStates, Direction moveDir, int removedIndex, int toMoveIndex, BlockPos posToMove, BlockState stateToMove) {
		if (!level.isClientSide()) {
			Multimeter multimeter = ((IServerLevel)level).getMultimeter();

			multimeter.logMoved(level, posToMove, moveDir);
			multimeter.moveMeters(level, posToMove, moveDir);
		}
	}

	@Override
	public boolean rsmm$logPoweredOnBlockUpdate() {
		return false;
	}

	@Override
	public boolean rsmm$isPowered(Level level, BlockPos pos, BlockState state) {
		return getNeighborSignal(level, pos, state.getValue(PistonBaseBlock.FACING));
	}

	@Override
	public boolean rsmm$isActive(Level level, BlockPos pos, BlockState state) {
		return state.getValue(PistonBaseBlock.EXTENDED);
	}
}
