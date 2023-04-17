package redstone.multimeter.mixin.common.meterable;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.ComparatorBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import redstone.multimeter.interfaces.mixin.IServerLevel;

@Mixin(ComparatorBlockEntity.class)
public class ComparatorBlockEntityMixin extends BlockEntity {

	@Shadow private int output;

	private ComparatorBlockEntityMixin(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	@Inject(
		method = "setOutputSignal",
		at = @At(
			value = "HEAD"
		)
	)
	public void logPowerChange(int newOutput, CallbackInfo ci) {
		if (!level.isClientSide()) {
			((IServerLevel)level).getMultimeter().logPowerChange(level, worldPosition, output, newOutput);
		}
	}
}
