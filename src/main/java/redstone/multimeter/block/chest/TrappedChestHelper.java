package redstone.multimeter.block.chest;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

import redstone.multimeter.block.PowerSource;

public class TrappedChestHelper {
	
	public static int getPower(World world, BlockPos pos, BlockState state) {
		BlockEntity blockEntity = world.getBlockEntity(pos);
		
		if (blockEntity instanceof ChestBlockEntity) {
			return getPowerFromViewerCount(((ChestBlockEntity)blockEntity).viewerCount);
		}
		
		return PowerSource.MIN_POWER;
	}
	
	public static int getPowerFromViewerCount(int viewerCount) {
		return MathHelper.clamp(viewerCount, PowerSource.MIN_POWER, PowerSource.MAX_POWER);
	}
}
