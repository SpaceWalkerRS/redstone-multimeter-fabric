package redstone.multimeter.mixin.common.meterable;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.block.BlockRedstoneDiode;
import net.minecraft.block.BlockRedstoneRepeater;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

import redstone.multimeter.block.MeterableBlock;
import redstone.multimeter.block.PowerSource;

@Mixin(BlockRedstoneRepeater.class)
public abstract class BlockRedstoneRepeaterMixin extends BlockRedstoneDiode implements MeterableBlock, PowerSource {
	
	protected BlockRedstoneRepeaterMixin(boolean powered) {
		super(powered);
	}
	
	@Inject(
			method = "isLocked",
			at = @At(
					value = "RETURN"
			)
	)
	private void onIsLocked(IBlockAccess world, BlockPos pos, IBlockState state, CallbackInfoReturnable<Boolean> cir) {
		if (cir.getReturnValue() && world instanceof WorldServer) {
			logPoweredRSMM((WorldServer)world, pos, state);
		}
	}
	
	@Override
	public int getPowerLevelRSMM(World world, BlockPos pos, IBlockState state) {
		return isRepeaterPowered ? MAX_POWER : MIN_POWER;
	}
}
