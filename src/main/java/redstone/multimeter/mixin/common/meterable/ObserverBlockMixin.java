package redstone.multimeter.mixin.common.meterable;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.block.Block;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.property.BooleanProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import redstone.multimeter.block.Meterable;
import redstone.multimeter.block.PowerSource;
import redstone.multimeter.interfaces.mixin.IServerWorld;

@Pseudo // compat with Legacy Observers mod
@Mixin(targets = "legacy/observers/block/ObserverBlock")
public class ObserverBlockMixin implements Meterable, PowerSource {

	@Shadow @Final
	private static BooleanProperty POWERED;

	@Inject(
		method = "neighborStateChanged",
		at = @At(
			value = "HEAD"
		)
	)
	private void neighborStateChanged(BlockState state, World world, BlockPos pos, Block neighborBlock, BlockPos neighborPos, CallbackInfo ci) {
		if (!world.isClient) {
			((IServerWorld)world).getMultimeterServer().getMultimeter().logObserverUpdate(world, pos);
		}
	}

	@Override
	public boolean rsmm$isActive(World world, BlockPos pos, BlockState state) {
		return state.get(POWERED);
	}

	@Override
	public int rsmm$getPowerLevel(World world, BlockPos pos, BlockState state) {
		return state.get(POWERED) ? MAX_POWER : MIN_POWER;
	}
}
