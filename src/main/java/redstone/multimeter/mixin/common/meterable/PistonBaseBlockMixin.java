package redstone.multimeter.mixin.common.meterable;

import java.util.List;
import java.util.Set;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.block.PistonBaseBlock;
import net.minecraft.block.piston.PistonMoveStructureResolver;
import net.minecraft.block.state.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import redstone.multimeter.block.MeterableBlock;
import redstone.multimeter.interfaces.mixin.IServerWorld;
import redstone.multimeter.server.Multimeter;

@Mixin(PistonBaseBlock.class)
public class PistonBaseBlockMixin implements MeterableBlock {

	@Shadow private boolean shouldExtend(World world, BlockPos pos, Direction facing) { return false; }

	@Inject(
		method = "shouldExtend",
		at = @At(
			value = "RETURN"
		)
	)
	private void logPowered(World world, BlockPos pos, Direction facing, CallbackInfoReturnable<Boolean> cir) {
		rsmm$logPowered(world, pos, cir.getReturnValue());
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
	private void logMoved(World world, BlockPos pos, Direction facing, boolean extending, CallbackInfoReturnable<Boolean> cir, BlockPos headPos, PistonMoveStructureResolver structureResolver, List<BlockPos> toMove, List<BlockState> statesToMove, List<BlockPos> toDestroy, int removedIndex, BlockState[] removedStates, Direction moveDir, Set<BlockState> leftOverStates, int toMoveIndex, BlockPos posToMove, BlockState stateToMove) {
		if (!world.isClient()) {
			Multimeter multimeter = ((IServerWorld)world).getMultimeter();

			multimeter.logMoved(world, posToMove, moveDir);
			multimeter.moveMeters(world, posToMove, moveDir);
		}
	}

	@Override
	public boolean rsmm$logPoweredOnBlockUpdate() {
		return false;
	}

	@Override
	public boolean rsmm$isPowered(World world, BlockPos pos, BlockState state) {
		return shouldExtend(world, pos, state.get(PistonBaseBlock.FACING));
	}

	@Override
	public boolean rsmm$isActive(World world, BlockPos pos, BlockState state) {
		return state.get(PistonBaseBlock.EXTENDED);
	}
}
