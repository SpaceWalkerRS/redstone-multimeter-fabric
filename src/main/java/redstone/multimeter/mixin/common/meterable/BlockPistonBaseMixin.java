package redstone.multimeter.mixin.common.meterable;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.block.BlockPistonBase;
import net.minecraft.block.state.BlockPistonStructureHelper;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import redstone.multimeter.block.MeterableBlock;
import redstone.multimeter.interfaces.mixin.IWorldServer;
import redstone.multimeter.server.Multimeter;

@Mixin(BlockPistonBase.class)
public abstract class BlockPistonBaseMixin implements MeterableBlock {
	
	@Shadow protected abstract boolean shouldBeExtended(World world, BlockPos pos, EnumFacing facing);
	
	@Inject(
			method = "shouldBeExtended",
			at = @At(
					value = "RETURN"
			)
	)
	private void onShouldBeExtended(World world, BlockPos pos, EnumFacing facing, CallbackInfoReturnable<Boolean> cir) {
		logPoweredRSMM(world, pos, cir.getReturnValue());
	}
	
	@Inject(
			method = "doMove",
			locals = LocalCapture.CAPTURE_FAILHARD,
			at = @At(
					value = "INVOKE",
					ordinal = 1,
					shift = Shift.BEFORE,
					target = "Lnet/minecraft/util/math/BlockPos;offset(Lnet/minecraft/util/EnumFacing;)Lnet/minecraft/util/math/BlockPos;"
			)
	)
	private void onBlockMoved(World world, BlockPos pistonPos, EnumFacing facing, boolean extend, CallbackInfoReturnable<Boolean> cir, BlockPistonStructureHelper pistonHandler, List<BlockPos> movedPositions, List<IBlockState> movedStates, List<BlockPos> brokenPositions, int removedIndex, IBlockState[] removedStates, EnumFacing moveDir, int movedIndex, BlockPos movedPos, IBlockState movedState) {
		if (!world.isRemote) {
			Multimeter multimeter = ((IWorldServer)world).getMultimeter();
			
			multimeter.logMoved(world, movedPos, moveDir);
			multimeter.moveMeters(world, movedPos, moveDir);
		}
	}
	
	@Override
	public boolean logPoweredOnBlockUpdateRSMM() {
		return false;
	}
	
	@Override
	public boolean isPoweredRSMM(World world, BlockPos pos, IBlockState state) {
		return shouldBeExtended(world, pos, state.getValue(BlockPistonBase.FACING));
	}
	
	@Override
	public boolean isActiveRSMM(World world, BlockPos pos, IBlockState state) {
		return state.getValue(BlockPistonBase.EXTENDED);
	}
}
