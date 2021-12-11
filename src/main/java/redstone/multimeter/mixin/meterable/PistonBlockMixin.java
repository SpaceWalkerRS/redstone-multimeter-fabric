package redstone.multimeter.mixin.meterable;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.PistonBlock;
import net.minecraft.block.piston.PistonHandler;
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
	private void onBlockMoved(World world, BlockPos pistonPos, Direction facing, boolean extend, CallbackInfoReturnable<Boolean> cir, PistonHandler pistonHandler, List<BlockPos> movedPositions, List<BlockPos> brokenPositions, int removedIndex, Block[] removedBlocks, Direction moveDir, int brokenIndex, BlockPos movedPos, BlockState movedState) {
		if (!world.isClient) {
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
		return shouldExtend(world, pos, state.get(PistonBlock.DIRECTION));
	}
	
	@Override
	public boolean isActive(World world, BlockPos pos, BlockState state) {
		return state.get(PistonBlock.EXTENDED);
	}
}
