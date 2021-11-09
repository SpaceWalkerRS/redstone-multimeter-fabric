package redstone.multimeter.mixin.server;

import java.util.Iterator;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import redstone.multimeter.interfaces.mixin.IBlock;
import redstone.multimeter.interfaces.mixin.IServerWorld;
import redstone.multimeter.interfaces.mixin.IWorld;
import redstone.multimeter.server.Multimeter;
import redstone.multimeter.server.MultimeterServer;

@Mixin(World.class)
public abstract class WorldMixin implements IWorld {
	
	@Shadow public abstract boolean isClient();
	@Shadow public abstract boolean isReceivingRedstonePower(BlockPos pos);
	
	@Inject(
			method = "updateNeighbor",
			locals = LocalCapture.CAPTURE_FAILHARD,
			at = @At(
					value = "INVOKE",
					shift = Shift.BEFORE,
					target = "Lnet/minecraft/block/BlockState;neighborUpdate(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/Block;Lnet/minecraft/util/math/BlockPos;Z)V"
			)
	)
	private void onBlockUpdate(BlockPos pos, Block fromBlock, BlockPos fromPos, CallbackInfo ci, BlockState state) {
		if (isClient()) {
			return;
		}
		
		MultimeterServer server = ((IServerWorld)this).getMultimeterServer();
		Multimeter multimeter = server.getMultimeter();
		
		multimeter.logBlockUpdate((World)(Object)this, pos);
		
		// 'powered' changes for most meterable blocks are handled in those classes
		// to reduce expensive calls to 
		// World.isReceivingRedstonePower and World.getReceivedRedstonePower
		if (((IBlock)state.getBlock()).logPoweredOnBlockUpdate()) {
			multimeter.logPowered((World)(Object)this, pos, state);
		}
	}
	
	@Inject(
			method = "updateComparators",
			locals = LocalCapture.CAPTURE_FAILHARD,
			at = @At(
					value = "INVOKE",
					shift = Shift.BEFORE,
					target = "Lnet/minecraft/block/BlockState;neighborUpdate(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/Block;Lnet/minecraft/util/math/BlockPos;Z)V"
			)
	)
	private void onComparatorUpdate(BlockPos fromPos, Block fromBlock, CallbackInfo ci, Iterator<Direction> it, Direction dir, BlockPos pos) {
		if (!isClient()) {
			((IServerWorld)this).getMultimeter().logComparatorUpdate((World)(Object)this, pos);
		}
	}
}
