package redstone.multimeter.block.chest;

import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.block.state.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

import redstone.multimeter.block.PowerSource;

public class TrappedChestHelper {

	public static int getPower(World world, BlockPos pos, BlockState state) {
		return getPowerFromViewerCount(ChestBlockEntity.getViewerCount(world, pos));
	}

	public static int getPowerFromViewerCount(int viewerCount) {
		return MathHelper.clamp(viewerCount, PowerSource.MIN_POWER, PowerSource.MAX_POWER);
	}
}
