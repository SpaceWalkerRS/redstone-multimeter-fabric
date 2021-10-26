package redstone.multimeter.mixin.meterable;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.block.BlockState;
import net.minecraft.block.RedstoneWireBlock;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import redstone.multimeter.block.MeterableBlock;
import redstone.multimeter.block.PowerSource;

@Mixin(RedstoneWireBlock.class)
public abstract class RedstoneWireBlockMixin implements MeterableBlock, PowerSource {
	
	@Shadow protected abstract int getReceivedRedstonePower(World world, BlockPos pos);
	
	@Inject(
			method = "getReceivedRedstonePower",
			at = @At(
					value = "RETURN"
			)
	)
	private void onGetReceivedRedstonePower(World world, BlockPos pos, CallbackInfoReturnable<Integer> cir) {
		logPowered(world, pos, cir.getReturnValue() > 0);
	}
	
	@Override
	public boolean logPoweredOnBlockUpdate() {
		return false;
	}
	
	@Override
	public boolean isPowered(World world, BlockPos pos, BlockState state) {
		return getReceivedRedstonePower(world, pos) > 0;
	}
	
	@Override
	public boolean isActive(World world, BlockPos pos, BlockState state) {
		return state.get(Properties.POWER) > 0;
	}
	
	@Override
	public int getPowerLevel(World world, BlockPos pos, BlockState state) {
		return state.get(Properties.POWER);
	}
}
