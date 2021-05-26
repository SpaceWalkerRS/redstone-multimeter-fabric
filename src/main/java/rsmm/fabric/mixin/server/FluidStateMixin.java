package rsmm.fabric.mixin.server;

import java.util.Random;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.fluid.FluidState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import rsmm.fabric.interfaces.mixin.IServerWorld;
import rsmm.fabric.server.Multimeter;
import rsmm.fabric.server.MultimeterServer;

@Mixin(FluidState.class)
public class FluidStateMixin {
	
	@Inject(
			method = "onRandomTick",
			at = @At(
					value = "HEAD"
			)
	)
	private void onOnRandomTickInjectAtHead(World world, BlockPos pos, Random random, CallbackInfo ci) {
		MultimeterServer server = ((IServerWorld)world).getMultimeterServer();
		Multimeter multimeter = server.getMultimeter();
		
		multimeter.logRandomTick(world, pos);
	}
}
