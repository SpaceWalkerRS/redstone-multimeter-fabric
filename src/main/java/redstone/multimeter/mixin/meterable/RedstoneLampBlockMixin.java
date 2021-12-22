package redstone.multimeter.mixin.meterable;

import java.util.Random;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.class_3772;
import net.minecraft.block.BlockState;
import net.minecraft.block.RedstoneLampBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import redstone.multimeter.block.MeterableBlock;

@Mixin(RedstoneLampBlock.class)
public class RedstoneLampBlockMixin implements MeterableBlock {
	
	@Inject(
			method = "method_8661",
			at = @At(
					value = "HEAD"
			)
	)
	private void onScheduledTick(BlockState state, World world, BlockPos pos, Random random, CallbackInfo ci) {
		logPowered(world, pos, world.isReceivingRedstonePower(pos));
	}
	
	@Override
	public boolean isActive(World world, BlockPos pos, BlockState state) {
		return state.method_16934(class_3772.field_18778);
	}
}
