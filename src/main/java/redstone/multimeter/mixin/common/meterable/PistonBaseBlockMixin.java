package redstone.multimeter.mixin.common.meterable;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.block.Block;
import net.minecraft.block.PistonBaseBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.Directions;
import net.minecraft.world.World;

import redstone.multimeter.block.MeterableBlock;
import redstone.multimeter.interfaces.mixin.IServerWorld;
import redstone.multimeter.server.Multimeter;
import redstone.multimeter.util.Direction;

@Mixin(PistonBaseBlock.class)
public class PistonBaseBlockMixin implements MeterableBlock {

	@Shadow private boolean shouldExtend(World world, int x, int y, int z, int facing) { return false; }

	@Inject(
		method = "shouldExtend",
		at = @At(
			value = "RETURN"
		)
	)
	private void logPowered(World world, int x, int y, int z, int facing, CallbackInfoReturnable<Boolean> cir) {
		rsmm$logPowered(world, x, y, z, cir.getReturnValue());
	}

	@Inject(
		method = "doEvent",
		locals = LocalCapture.CAPTURE_FAILHARD,
		at = @At(
			value = "INVOKE",
			ordinal = 1,
			target = "Lnet/minecraft/world/World;setBlockWithMetadata(IIILnet/minecraft/block/Block;II)Z"
		)
	)
	private void logMoved(World world, int x, int y, int z, int type, int data, CallbackInfoReturnable<Boolean> cir, BlockEntity blockEntity, int moveFromX, int moveFromY, int moveFromZ) {
		rsmm$logMoved(world, moveFromX, moveFromY, moveFromZ, Directions.OPPOSITE[data]);
	}

	@Inject(
		method = "push",
		locals = LocalCapture.CAPTURE_FAILHARD,
		at = @At(
			value = "INVOKE",
			ordinal = 1,
			target = "Lnet/minecraft/world/World;setBlockWithMetadata(IIILnet/minecraft/block/Block;II)Z"
		)
	)
	private void logMoved(World world, int x, int y, int z, int dir, CallbackInfoReturnable<Boolean> cir, int moveToX, int moveToY, int moveToZ, int toMoveCount, int frontMostX, int frontMostY, int frontMostZ, Block[] blocksToMove, int moveFromX, int moveFromY, int moveFromZ) {
		rsmm$logMoved(world, moveFromX, moveFromY, moveFromZ, dir);
	}

	@Override
	public boolean rsmm$logPoweredOnBlockUpdate() {
		return false;
	}

	@Override
	public boolean rsmm$isPowered(World world, int x, int y, int z, int metadata) {
		return shouldExtend(world, x, y, z, PistonBaseBlock.getFacing(metadata));
	}

	@Override
	public boolean rsmm$isActive(World world, int x, int y, int z, int metadata) {
		return PistonBaseBlock.isExtended(metadata);
	}

	private void rsmm$logMoved(World world, int x, int y, int z, int dir) {
		if (!world.isMultiplayer) {
			Multimeter multimeter = ((IServerWorld)world).getMultimeter();
			Direction direction = Direction.fromIndex(dir);

			multimeter.logMoved(world, x, y, z, direction);
			multimeter.moveMeters(world, x, y, z, direction);
		}
	}
}
