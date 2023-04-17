package redstone.multimeter.mixin.common.meterable;

import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.TrappedChestBlockEntity;

import redstone.multimeter.block.PowerSource;
import redstone.multimeter.block.chest.TrappedChestHelper;
import redstone.multimeter.interfaces.mixin.IChestBlockEntity;
import redstone.multimeter.interfaces.mixin.IServerLevel;
import redstone.multimeter.server.Multimeter;

@Mixin(TrappedChestBlockEntity.class)
public class TrappedChestBlockEntityMixin extends BlockEntity implements IChestBlockEntity {

	private TrappedChestBlockEntityMixin(BlockEntityType<?> type) {
		super(type);
	}

	@Override
	public void signalOpenerCount(int oldOpenerCount, int newOpenerCount) {
		if (!level.isClientSide()) {
			Multimeter multimeter = ((IServerLevel)level).getMultimeter();

			int oldPower = TrappedChestHelper.getPowerFromOpenerCount(oldOpenerCount);
			int newPower = TrappedChestHelper.getPowerFromOpenerCount(newOpenerCount);

			multimeter.logPowerChange(level, worldPosition, oldPower, newPower);
			multimeter.logActive(level, worldPosition, newPower > PowerSource.MIN_POWER);
		}
	}
}
