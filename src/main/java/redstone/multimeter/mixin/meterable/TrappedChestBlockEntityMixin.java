package redstone.multimeter.mixin.meterable;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.TrappedChestBlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import redstone.multimeter.block.chest.TrappedChestHelper;
import redstone.multimeter.interfaces.mixin.IServerWorld;
import redstone.multimeter.server.Multimeter;
import redstone.multimeter.server.MultimeterServer;

@Mixin(TrappedChestBlockEntity.class)
public class TrappedChestBlockEntityMixin {
	
	@Inject(
			method = "onInvOpenOrClose",
			at = @At(
					value = "HEAD"
			)
	)
	private void onOnInvOpenOrCloseInjectAtHead(World world, BlockPos pos, BlockState state, int oldViewerCount, int newViewerCount, CallbackInfo ci) {
		if (!world.isClient()) {
			MultimeterServer server = ((IServerWorld)world).getMultimeterServer();
			Multimeter multimeter = server.getMultimeter();
			
			int oldPower = TrappedChestHelper.getPowerFromViewerCount(oldViewerCount);
			int newPower = TrappedChestHelper.getPowerFromViewerCount(newViewerCount);
			
			multimeter.logPowerChange(world, pos, oldPower, newPower);
			multimeter.logActive(world, pos, newPower > 0);
		}
	}
}
