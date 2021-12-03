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

import net.minecraft.block.BlockState;
import net.minecraft.block.PistonBlock;
import net.minecraft.block.piston.PistonHandler;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import redstone.multimeter.block.MeterableBlock;
import redstone.multimeter.interfaces.mixin.IServerWorld;
import redstone.multimeter.server.Multimeter;

@Mixin(PistonBlock.class)
public abstract class PistonBlockMixin implements MeterableBlock {
	
	@Shadow protected abstract boolean shouldExtend(World world, BlockPos pos, Direction facing);
	
	@Inject(
			method = "shouldExtend",
			at = @At(
					value = "RETURN"
			)
	)
	private void onShouldExtend(World world, BlockPos pos, Direction facing, CallbackInfoReturnable<Boolean> cir) {
		logPowered(world, pos, cir.getReturnValue());
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
	private void onBlockMoved(World world, BlockPos pistonPos, Direction facing, boolean extend, CallbackInfoReturnable<Boolean> cir, BlockPos headPos, PistonHandler pistonHandler, Map<BlockPos, BlockState> movedPosToState, List<BlockPos> movedPositions, List<BlockState> movedStates, List<BlockPos> brokenPositions, BlockState[] removedStates, Direction moveDir, int removedIndex, int brokenIndex, BlockPos movedPos, BlockState movedState) {
		if (!world.isClient()) {
			Multimeter multimeter = ((IServerWorld)world).getMultimeter();
			
			multimeter.logMoved(world, movedPos, moveDir);
			multimeter.moveMeters(world, movedPos, moveDir);
		}
	}
	
	@Override
	public boolean logPoweredOnBlockUpdate() {
		return false;
	}
	
	@Override
	public boolean isPowered(World world, BlockPos pos, BlockState state) {
		return shouldExtend(world, pos, state.get(Properties.FACING));
	}
	
	@Override
	public boolean isActive(World world, BlockPos pos, BlockState state) {
		return state.get(Properties.EXTENDED);
	}
}
