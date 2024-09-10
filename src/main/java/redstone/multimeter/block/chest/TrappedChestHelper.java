package redstone.multimeter.block.chest;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

import redstone.multimeter.block.PowerSource;

public class TrappedChestHelper {

	public static int TYPE = 1;

	public static int getPower(World world, int x, int y, int z, int metadata) {
		BlockEntity blockEntity = world.getBlockEntity(x, y, z);

		if (blockEntity instanceof ChestBlockEntity) {
			return getPowerFromViewerCount(((ChestBlockEntity)blockEntity).viewerCount);
		}

		return PowerSource.MIN_POWER;
	}

	public static int getPowerFromViewerCount(int viewerCount) {
		return MathHelper.clamp(viewerCount, PowerSource.MIN_POWER, PowerSource.MAX_POWER);
	}
}
