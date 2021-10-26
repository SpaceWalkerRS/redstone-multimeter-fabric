package redstone.multimeter.mixin.meterable;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.PoweredRailBlock;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import redstone.multimeter.block.MeterableBlock;

@Mixin(PoweredRailBlock.class)
public abstract class PoweredRailBlockMixin implements MeterableBlock {
	
	@Shadow protected abstract boolean isPoweredByOtherRails(World world, BlockPos pos, BlockState state, boolean boolean4, int distance);
	
	@Inject(
			method = "updateBlockState",
			at = @At(
					value = "HEAD"
			)
	)
	private void onUpdateBlockStateInjectAtHead(BlockState state, World world, BlockPos pos, Block neighbor, CallbackInfo ci) {
		logPowered(world, pos, isPowered(world, pos, state));
	}
	
	@Override
	public boolean logPoweredOnBlockUpdate() {
		return false;
	}
	
	@Override
	public boolean isPowered(World world, BlockPos pos, BlockState state) {
		return world.isReceivingRedstonePower(pos) || isPoweredByOtherRails(world, pos, state, true, 0) || isPoweredByOtherRails(world, pos, state, false, 0);
	}
	
	@Override
	public boolean isActive(World world, BlockPos pos, BlockState state) {
		return state.get(Properties.POWERED);
	}
}
