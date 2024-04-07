package redstone.multimeter.mixin.common.meterable;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.block.Block;
import net.minecraft.block.NoteBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.NoteBlockBlockEntity;
import net.minecraft.block.state.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import redstone.multimeter.block.MeterableBlock;
import redstone.multimeter.interfaces.mixin.IServerWorld;

@Mixin(NoteBlock.class)
public class NoteBlockMixin implements MeterableBlock {

	@Inject(
		method = "neighborChanged",
		locals = LocalCapture.CAPTURE_FAILHARD,
		at = @At(
			value = "FIELD",
			ordinal = 0,
			target = "Lnet/minecraft/block/entity/NoteBlockBlockEntity;powered:Z"
		)
	)
	private void logPowered(World world, BlockPos pos, BlockState state, Block neighborBlock, CallbackInfo ci, boolean powered) {
		rsmm$logPowered(world, pos, powered);
	}

	@Inject(
		method = "neighborChanged",
		locals = LocalCapture.CAPTURE_FAILHARD,
		at = @At(
			value = "FIELD",
			ordinal = 1,
			target = "Lnet/minecraft/block/entity/NoteBlockBlockEntity;powered:Z"
		)
	)
	private void logActive(World world, BlockPos pos, BlockState state, Block neighborBlock, CallbackInfo ci, boolean powered) {
		if (!world.isClient) {
			((IServerWorld)world).getMultimeter().logActive(world, pos, powered);
		}
	}

	@Override
	public boolean rsmm$logPoweredOnBlockUpdate() {
		return false;
	}

	@Override
	public boolean rsmm$isActive(World world, BlockPos pos, BlockState state) {
		BlockEntity blockEntity = world.getBlockEntity(pos);

		if (blockEntity instanceof NoteBlockBlockEntity) {
			return ((NoteBlockBlockEntity)blockEntity).powered;
		}

		return false;
	}
}
