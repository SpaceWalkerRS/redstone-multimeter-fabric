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

@Mixin(TrappedChestBlockEntity.class)
public class TrappedChestBlockEntityMixin {

	@Inject(
		method = "signalOpenCount",
		at = @At(
			value = "HEAD"
		)
	)
	private void logPowerChangeAndActive(Level level, BlockPos pos, BlockState state, int oldOpenerCount, int newOpenerCount, CallbackInfo ci) {
		if (!level.isClientSide()) {
			Multimeter multimeter = ((IServerLevel)level).getMultimeter();

			int oldPower = TrappedChestHelper.getPowerFromOpenerCount(oldOpenerCount);
			int newPower = TrappedChestHelper.getPowerFromOpenerCount(newOpenerCount);

			multimeter.logPowerChange(level, pos, oldPower, newPower);
			multimeter.logActive(level, pos, newPower > PowerSource.MIN_POWER);
		}
	}
}
