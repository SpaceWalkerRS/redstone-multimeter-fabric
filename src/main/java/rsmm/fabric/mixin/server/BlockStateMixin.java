package rsmm.fabric.mixin.server;

import java.util.Random;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.block.BlockState;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

import rsmm.fabric.interfaces.mixin.IServerWorld;
import rsmm.fabric.server.Multimeter;
import rsmm.fabric.server.MultimeterServer;

@Mixin(BlockState.class)
public class BlockStateMixin {
	
	@Inject(
			method = "randomTick",
			at = @At(
					value = "HEAD"
			)
	)
	private void onRandomTickInjectAtHead(ServerWorld world, BlockPos pos, Random random, CallbackInfo ci) {
		MultimeterServer server = ((IServerWorld)world).getMultimeterServer();
		Multimeter multimeter = server.getMultimeter();
		
		multimeter.logRandomTick(world, pos);
	}
}
