package rsmm.fabric.mixin.meterable;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.ChestBlockEntity;

import rsmm.fabric.interfaces.mixin.IServerWorld;
import rsmm.fabric.server.Multimeter;
import rsmm.fabric.server.MultimeterServer;

@Mixin(ChestBlockEntity.class)
public class ChestBlockEntityMixin extends BlockEntity {
	
	@Shadow protected int viewerCount;
	
	public ChestBlockEntityMixin(BlockEntityType<?> type) {
		super(type);
	}
	
	@Inject(
			method = "onInvOpenOrClose",
			locals = LocalCapture.CAPTURE_FAILHARD,
			at = @At(
					value = "INVOKE",
					shift = Shift.BEFORE,
					target = "Lnet/minecraft/world/World;addSyncedBlockEvent(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/Block;II)V"
			)
	)
	private void onOnInvOpenOrCloseInjectBeforeAddSyncedBlockEvent(CallbackInfo ci, Block cachedBlock) {
		if (!world.isClient() && cachedBlock == Blocks.TRAPPED_CHEST) {
			MultimeterServer server = ((IServerWorld)world).getMultimeterServer();
			Multimeter multimeter = server.getMultimeter();
			
			multimeter.blockUpdate(world, pos, viewerCount > 0);
		}
	}
}
