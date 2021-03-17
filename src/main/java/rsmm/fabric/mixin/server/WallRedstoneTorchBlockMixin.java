package rsmm.fabric.mixin.server;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.block.BlockState;
import net.minecraft.block.WallRedstoneTorchBlock;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import rsmm.fabric.common.Meterable;
import rsmm.fabric.common.Multimeter;
import rsmm.fabric.interfaces.mixin.IServerWorld;
import rsmm.fabric.server.MultimeterServer;

@Mixin(WallRedstoneTorchBlock.class)
public class WallRedstoneTorchBlockMixin implements Meterable {
	
	@Inject(
			method = "shouldUnpower",
			at = @At(
					value = "RETURN"
			)
	)
	private void onShouldUnpowerInjectAtReturn(World world, BlockPos pos, BlockState state, CallbackInfoReturnable<Boolean> cir) {
		if (!world.isClient()) {
			boolean powered = cir.getReturnValue();
			
			MultimeterServer server = ((IServerWorld)world).getMultimeterServer();
			Multimeter multimeter = server.getMultimeter();
			
			multimeter.blockUpdate(world, pos, powered);
		}
	}
	
	@Override
	public boolean isActive(World world, BlockPos pos, BlockState state) {
		return state.get(Properties.LIT);
	}
}
