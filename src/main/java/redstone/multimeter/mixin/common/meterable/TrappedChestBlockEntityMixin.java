package redstone.multimeter.mixin.common.meterable;

import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.TrappedChestBlockEntity;

import redstone.multimeter.block.PowerSource;
import redstone.multimeter.block.chest.TrappedChestHelper;
import redstone.multimeter.interfaces.mixin.IChestBlockEntity;
import redstone.multimeter.interfaces.mixin.IServerWorld;
import redstone.multimeter.server.Multimeter;

@Mixin(TrappedChestBlockEntity.class)
public class TrappedChestBlockEntityMixin extends BlockEntity implements IChestBlockEntity {

	private TrappedChestBlockEntityMixin(BlockEntityType<?> type) {
		super(type);
	}

	@Override
	public void signalViewerCount(int oldViewerCount, int newViewerCount) {
		if (!world.isClient()) {
			Multimeter multimeter = ((IServerWorld)world).getMultimeter();

			int oldPower = TrappedChestHelper.getPowerFromViewerCount(oldViewerCount);
			int newPower = TrappedChestHelper.getPowerFromViewerCount(newViewerCount);

			multimeter.logPowerChange(world, pos, oldPower, newPower);
			multimeter.logActive(world, pos, newPower > PowerSource.MIN_POWER);
		}
	}
}
