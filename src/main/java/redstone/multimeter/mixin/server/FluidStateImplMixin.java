package redstone.multimeter.mixin.server;

import java.util.Random;

import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.class_4024;
import net.minecraft.class_4025;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import redstone.multimeter.interfaces.mixin.IFluid;
import redstone.multimeter.interfaces.mixin.IServerWorld;

@Mixin(class_4025.class)
public abstract class FluidStateImplMixin implements class_4024 {
	
	@Override
	public void method_17806(World world, BlockPos pos, Random random) {
		((IServerWorld)world).getMultimeter().logRandomTick(world, pos);
		((IFluid)method_17807()).randomTick(world, pos, this, random);
	}
}
