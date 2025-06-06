package redstone.multimeter.mixin.common.meterable;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.block.ComparatorBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ComparatorBlockEntity;
import net.minecraft.world.World;

import redstone.multimeter.block.MeterableBlock;
import redstone.multimeter.block.PowerSource;

@Mixin(ComparatorBlock.class)
public abstract class ComparatorBlockMixin extends DiodeBlockMixin implements MeterableBlock, PowerSource {

	@Inject(
		method = "calculateOutputSignal",
		at = @At(
			value = "RETURN"
		)
	)
	private void logPowered(World world, int x, int y, int z, int metadata, CallbackInfoReturnable<Integer> cir) {
		rsmm$logPowered(world, x, y, z, cir.getReturnValue() > MIN_POWER);
	}

	@Override
	public boolean rsmm$logPowerChangeOnStateChange() {
		return false;
	}

	@Override
	public int rsmm$getPowerLevel(World world, int x, int y, int z, int metadata) {
		BlockEntity blockEntity = world.getBlockEntity(x, y, z);

		if (blockEntity instanceof ComparatorBlockEntity) {
			return ((ComparatorBlockEntity)blockEntity).getOutputSignal();
		}

		return MIN_POWER;
	}
}
