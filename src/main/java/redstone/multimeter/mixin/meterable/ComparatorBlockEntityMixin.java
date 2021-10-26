package redstone.multimeter.mixin.meterable;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.ComparatorBlockEntity;
import net.minecraft.util.math.BlockPos;

import redstone.multimeter.interfaces.mixin.IServerWorld;

@Mixin(ComparatorBlockEntity.class)
public class ComparatorBlockEntityMixin extends BlockEntity {
	
	@Shadow private int outputSignal;
	
	public ComparatorBlockEntityMixin(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}
	
	@Inject(
			method = "setOutputSignal",
			at = @At(
					value = "HEAD"
			)
	)
	public void onSetOutputSignalInjectAtHead(int newPower, CallbackInfo ci) {
		if (!world.isClient()) {
			((IServerWorld)world).getMultimeter().logPowerChange(world, pos, outputSignal, newPower);
		}
	}
}
