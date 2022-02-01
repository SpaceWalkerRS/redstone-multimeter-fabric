package redstone.multimeter.mixin.common.meterable;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.block.Block;
import net.minecraft.block.BlockNote;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityNote;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import redstone.multimeter.block.MeterableBlock;
import redstone.multimeter.interfaces.mixin.IWorldServer;

@Mixin(BlockNote.class)
public abstract class BlockNoteMixin implements MeterableBlock {
	
	@Inject(
			method = "neighborChanged",
			locals = LocalCapture.CAPTURE_FAILHARD,
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/World;getTileEntity(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/tileentity/TileEntity;"
			)
	)
	private void onNeighborUpdate(IBlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, CallbackInfo ci, boolean powered) {
		logPoweredRSMM(world, pos, powered);
	}
	
	@Inject(
			method = "neighborChanged",
			locals = LocalCapture.CAPTURE_FAILHARD,
			at = @At(
					value = "FIELD",
					ordinal = 0,
					target = "Lnet/minecraft/tileentity/TileEntityNote;previousRedstoneState:Z"
			)
	)
	private void onNoteBlockPowered(IBlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, CallbackInfo ci, boolean active) {
		if (!world.isRemote) {
			((IWorldServer)world).getMultimeter().logActive(world, pos, active);
		}
	}
	
	@Override
	public boolean logPoweredOnBlockUpdateRSMM() {
		return false;
	}
	
	@Override
	public boolean isActiveRSMM(World world, BlockPos pos, IBlockState state) {
		TileEntity blockEntity = world.getTileEntity(pos);
		
		if (blockEntity instanceof TileEntityNote) {
			return ((TileEntityNote)blockEntity).previousRedstoneState;
		}
		
		return false;
	}
}
