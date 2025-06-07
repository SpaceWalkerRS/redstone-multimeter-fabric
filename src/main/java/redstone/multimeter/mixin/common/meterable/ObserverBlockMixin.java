package redstone.multimeter.mixin.common.meterable;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.block.Block;
import net.minecraft.world.World;

import redstone.multimeter.block.Meterable;
import redstone.multimeter.block.PowerSource;
import redstone.multimeter.interfaces.mixin.IServerWorld;

@Pseudo // compat with Legacy Observers mod
@Mixin(targets = "legacy/observers/block/ObserverBlock")
public class ObserverBlockMixin implements Meterable, PowerSource {

	@Shadow
	private static boolean getPowered(int metadata) { return false; }

	@Inject(
		method = "neighborStateChanged",
		at = @At(
			value = "HEAD"
		)
	)
	private void neighborStateChanged(World world, int x, int y, int z, Block neighborBlock, int neighborX, int neighborY, int neighborZ, CallbackInfo ci) {
		if (!world.isMultiplayer) {
			((IServerWorld)world).getMultimeterServer().getMultimeter().logObserverUpdate(world, x, y, z);
		}
	}

	@Override
	public boolean rsmm$isActive(World world, int x, int y, int z, int metadata) {
		return getPowered(metadata);
	}

	@Override
	public int rsmm$getPowerLevel(World world, int x, int y, int z, int metadata) {
		return getPowered(metadata) ? MAX_POWER : MIN_POWER;
	}
}
