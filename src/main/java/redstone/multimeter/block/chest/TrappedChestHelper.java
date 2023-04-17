package redstone.multimeter.block.chest;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import redstone.multimeter.block.PowerSource;

public class TrappedChestHelper {

	public static int getPower(Level level, BlockPos pos, BlockState state) {
		return getPowerFromOpenerCount(ChestBlockEntity.getOpenCount(level, pos));
	}

	public static int getPowerFromOpenerCount(int openerCount) {
		return Mth.clamp(openerCount, PowerSource.MIN_POWER, PowerSource.MAX_POWER);
	}
}
