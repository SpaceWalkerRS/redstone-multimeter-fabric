package redstone.multimeter.mixin.meterable;

import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.class_3746;
import net.minecraft.block.entity.ChestBlockEntity;

import redstone.multimeter.block.PowerSource;
import redstone.multimeter.block.chest.TrappedChestHelper;
import redstone.multimeter.interfaces.mixin.IChestBlockEntity;
import redstone.multimeter.interfaces.mixin.IServerWorld;
import redstone.multimeter.server.Multimeter;
import redstone.multimeter.server.MultimeterServer;

@Mixin(class_3746.class)
public class TrappedChestBlockEntityMixin extends ChestBlockEntity implements IChestBlockEntity {
	
	@Override
	public void invOpenOrClose(boolean open) {
		if (!world.method_16390()) {
			MultimeterServer server = ((IServerWorld)world).getMultimeterServer();
			Multimeter multimeter = server.getMultimeter();
			
			int oldViewerCount = open ? viewerCount - 1 : viewerCount + 1;
			
			int oldPower = TrappedChestHelper.getPowerFromViewerCount(oldViewerCount);
			int newPower = TrappedChestHelper.getPowerFromViewerCount(viewerCount);
			
			multimeter.logPowerChange(world, pos, oldPower, newPower);
			multimeter.logActive(world, pos, newPower > PowerSource.MIN_POWER);
		}
	}
}
