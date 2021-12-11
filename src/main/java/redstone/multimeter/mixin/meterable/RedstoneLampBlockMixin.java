package redstone.multimeter.mixin.meterable;

import java.util.Random;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.block.BlockState;
import net.minecraft.block.RedstoneLampBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import redstone.multimeter.block.MeterableBlock;

@Mixin(RedstoneLampBlock.class)
public class RedstoneLampBlockMixin implements MeterableBlock {
	
	@Shadow @Final private boolean field_1155;
	
	@Inject(
			method = "scheduledTick",
			at = @At(
					value = "HEAD"
			)
	)
	private void onScheduledTick(World world, BlockPos pos, BlockState state, Random random, CallbackInfo ci) {
		logPowered(world, pos, world.isReceivingRedstonePower(pos));
	}
	
	@Override
	public boolean isActive(World world, BlockPos pos, BlockState state) {
		return field_1155;
	}
}
