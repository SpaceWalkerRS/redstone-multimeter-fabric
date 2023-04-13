package redstone.multimeter.mixin.common.meterable;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.TrappedChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import redstone.multimeter.block.PowerSource;
import redstone.multimeter.block.chest.TrappedChestHelper;
import redstone.multimeter.interfaces.mixin.IServerLevel;
import redstone.multimeter.server.Multimeter;
import redstone.multimeter.server.MultimeterServer;

@Mixin(TrappedChestBlockEntity.class)
public class TrappedChestBlockEntityMixin {

	@Inject(
		method = "signalOpenCount",
		at = @At(
			value = "HEAD"
		)
	)
	private void logPowerChangeAndActive(Level world, BlockPos pos, BlockState state, int oldOpenerCount, int newOpenerCount, CallbackInfo ci) {
		if (!world.isClientSide()) {
			MultimeterServer server = ((IServerLevel)world).getMultimeterServer();
			Multimeter multimeter = server.getMultimeter();

			int oldPower = TrappedChestHelper.getPowerFromOpenerCount(oldOpenerCount);
			int newPower = TrappedChestHelper.getPowerFromOpenerCount(newOpenerCount);

			multimeter.logPowerChange(world, pos, oldPower, newPower);
			multimeter.logActive(world, pos, newPower > PowerSource.MIN_POWER);
		}
	}
}
