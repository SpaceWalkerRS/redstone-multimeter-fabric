package redstone.multimeter.block.chest;

import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

import redstone.multimeter.block.PowerSource;

public class TrappedChestHelper {
	
	public static int getPower(World world, BlockPos pos, IBlockState state) {
		TileEntity blockEntity = world.getTileEntity(pos);
		
		if (blockEntity instanceof TileEntityChest) {
			return getPowerFromViewerCount(((TileEntityChest)blockEntity).numPlayersUsing);
		}
		
		return PowerSource.MIN_POWER;
	}
	
	public static int getPowerFromViewerCount(int viewerCount) {
		return MathHelper.clamp(viewerCount, PowerSource.MIN_POWER, PowerSource.MAX_POWER);
	}
}
