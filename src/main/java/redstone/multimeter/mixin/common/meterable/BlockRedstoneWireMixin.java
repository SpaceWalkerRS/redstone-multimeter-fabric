package redstone.multimeter.mixin.common.meterable;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.block.BlockRedstoneWire;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import redstone.multimeter.block.MeterableBlock;
import redstone.multimeter.block.PowerSource;

@Mixin(BlockRedstoneWire.class)
public abstract class BlockRedstoneWireMixin implements MeterableBlock, PowerSource {
	
	@Inject(
			method = "calculateCurrentChanges",
			locals = LocalCapture.CAPTURE_FAILHARD,
			at = @At(
					value = "FIELD",
					ordinal = 1,
					target = "Lnet/minecraft/block/BlockRedstoneWire;POWER:Lnet/minecraft/block/properties/PropertyInteger;"
			)
	)
	private void onUpdatePower(World world, BlockPos pos, BlockPos otherPos, IBlockState state, CallbackInfoReturnable<IBlockState> cir, IBlockState oldState, int oldPower, int receivedPower) {
		logPoweredRSMM(world, pos, receivedPower > MIN_POWER);
	}
	
	@Override
	public boolean logPoweredOnBlockUpdateRSMM() {
		return false;
	}
	
	// This method is only called on blocks where 'logPoweredOnBlockUpdate'
	// returns 'true', so it does not really matter that a potentially
	// incorrect value is returned.
	@Override
	public boolean isPoweredRSMM(World world, BlockPos pos, IBlockState state) {
		return isActiveRSMM(world, pos, state);
	}
	
	@Override
	public boolean isActiveRSMM(World world, BlockPos pos, IBlockState state) {
		return state.getValue(BlockRedstoneWire.POWER) > MIN_POWER;
	}
	
	@Override
	public int getPowerLevelRSMM(World world, BlockPos pos, IBlockState state) {
		return state.getValue(BlockRedstoneWire.POWER);
	}
}
