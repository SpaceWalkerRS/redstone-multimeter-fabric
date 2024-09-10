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
	private void logPowered(World world, int x, int y, int z, Block neighborBlock, CallbackInfo ci, int powered /* the fuck? */) {
		rsmm$logPowered(world, x, y, z, powered != 0);
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
	private void logActive(World world, int x, int y, int z, Block neighborBlock, CallbackInfo ci, int powered /* the fuck? */) {
		if (!world.isMultiplayer) {
			((IServerWorld)world).getMultimeter().logActive(world, x, y, z, powered != 0);
		}
	}

	@Override
	public boolean rsmm$logPoweredOnBlockUpdate() {
		return false;
	}

	@Override
	public boolean rsmm$isActive(World world, int x, int y, int z, int metadata) {
		BlockEntity blockEntity = world.getBlockEntity(x, y, z);

		if (blockEntity instanceof NoteBlockBlockEntity) {
			return ((NoteBlockBlockEntity)blockEntity).powered;
		}

		return false;
	}
}
