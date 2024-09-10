package redstone.multimeter.mixin.common.meterable;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.block.ChestBlock;
import net.minecraft.world.World;

import redstone.multimeter.block.Meterable;
import redstone.multimeter.block.PowerSource;
import redstone.multimeter.block.chest.TrappedChestHelper;

@Mixin(ChestBlock.class)
public class ChestBlockMixin implements Meterable, PowerSource {

	@Shadow @Final private int type;

	@Override
	public boolean rsmm$isMeterable() {
		return type == TrappedChestHelper.TYPE;
	}

	@Override
	public boolean rsmm$isPowerSource() {
		return type == TrappedChestHelper.TYPE;
	}

	@Override
	public boolean rsmm$isActive(World world, int x, int y, int z, int metadata) {
		return type == TrappedChestHelper.TYPE && TrappedChestHelper.getPower(world, x, y, z, metadata) > MIN_POWER;
	}

	@Override
	public boolean rsmm$logPowerChangeOnStateChange() {
		return false;
	}

	@Override
	public int rsmm$getPowerLevel(World world, int x, int y, int z, int metadata) {
		return type == TrappedChestHelper.TYPE ? TrappedChestHelper.getPower(world, x, y, z, metadata) : MIN_POWER;
	}
}
