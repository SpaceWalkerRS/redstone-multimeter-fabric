package rsmm.fabric.block.chest;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import rsmm.fabric.block.PowerSource;

public class TrappedChestHelper {
	
	public static int getPower(World world, BlockPos pos, BlockState state) {
		return getPowerFromViewerCount(ChestBlockEntity.getPlayersLookingInChestCount(world, pos));
	}
	
	public static int getPowerFromViewerCount(int viewerCount) {
		if (viewerCount < 0) {
			return 0;
		}
		if (viewerCount > PowerSource.MAX_POWER) {
			return PowerSource.MAX_POWER;
		}
		
		return viewerCount;
	}
}
