package redstone.multimeter.mixin.meterable;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.class_830;
import net.minecraft.block.Block;
import net.minecraft.block.PistonBlock;
import net.minecraft.world.World;

import redstone.multimeter.block.MeterableBlock;
import redstone.multimeter.interfaces.mixin.IServerWorld;
import redstone.multimeter.server.Multimeter;
import redstone.multimeter.util.Direction;

@Mixin(PistonBlock.class)
public abstract class PistonBlockMixin implements MeterableBlock {
	
	@Shadow protected abstract boolean method_557(World world, int x, int y, int z, int facing);
	
	@Inject(
			method = "method_557",
			at = @At(
					value = "RETURN"
			)
	)
	private void onShouldExtend(World world, int x, int y, int z, int facing, CallbackInfoReturnable<Boolean> cir) {
		logPowered(world, x, y, z, cir.getReturnValue());
	}
	
	@Inject(
			method = "method_435",
			at = @At(
					value = "INVOKE",
					ordinal = 1,
					target = "Lnet/minecraft/world/World;method_4721(IIILnet/minecraft/block/Block;II)Z"
			)
	)
	private void onRetractBlock(World world, int x, int y, int z, int type, int data, CallbackInfoReturnable<Boolean> cir) {
		if (!world.isClient) {
			int movedX = x + 2 * class_830.field_3062[data];
			int movedY = y + 2 * class_830.field_3063[data];
			int movedZ = z + 2 * class_830.field_3064[data];
			Direction dir = Direction.fromIndex(data);
			
			onBlockMoved(world, movedX, movedY, movedZ, dir);
		}
	}
	
	@Inject(
			method = "method_560",
			locals = LocalCapture.CAPTURE_FAILHARD,
			at = @At(
					value = "INVOKE",
					ordinal = 1,
					target = "Lnet/minecraft/world/World;method_4721(IIILnet/minecraft/block/Block;II)Z"
			)
	)
	private void onBlockMoved(World world, int x, int y, int z, int dir, CallbackInfoReturnable<Boolean> cir, int destX, int destY, int destZ, int idk, int idk2, int idk3, int index, Block[] movedBlocks, int movedX, int movedY, int movedZ, Block movedBlock, int movedMetadata) {
		if (!world.isClient) {
			onBlockMoved(world, movedX, movedY, movedZ, Direction.fromIndex(dir));
		}
	}
	
	@Override
	public boolean logPoweredOnBlockUpdate() {
		return false;
	}
	
	@Override
	public boolean isPowered(World world, int x, int y, int z, int metadata) {
		return method_557(world, x, y, z, PistonBlock.method_556(metadata));
	}
	
	@Override
	public boolean isActive(World world, int x, int y, int z, int metadata) {
		return PistonBlock.method_558(metadata);
	}
	
	private void onBlockMoved(World world, int x, int y, int z, Direction dir) {
		Multimeter multimeter = ((IServerWorld)world).getMultimeter();
		
		multimeter.logMoved(world, x, y, z, dir);
		multimeter.moveMeters(world, x, y, z, dir);
	}
}
