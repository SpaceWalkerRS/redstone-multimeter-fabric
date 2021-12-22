package redstone.multimeter.mixin.meterable;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.block.ChestBlock;
import net.minecraft.world.World;

import redstone.multimeter.block.Meterable;
import redstone.multimeter.block.PowerSource;
import redstone.multimeter.block.chest.TrappedChestHelper;

@Mixin(ChestBlock.class)
public abstract class ChestBlockMixin implements Meterable, PowerSource {
	
	@Shadow @Final private int field_5532;
	
	@Override
	public boolean isActive(World world, int x, int y, int z, int metadata) {
		return isTrappedRSMM() && TrappedChestHelper.getPower(world, x, y, z) > MIN_POWER;
	}
	
	@Override
	public boolean logPowerChangeOnStateChange() {
		return false;
	}
	
	@Override
	public int getPowerLevel(World world, int x, int y, int z, int metadata) {
		return isTrappedRSMM() ? TrappedChestHelper.getPower(world, x, y, z) : MIN_POWER;
	}
	
	private boolean isTrappedRSMM() {
		return field_5532 == 1;
	}
}
