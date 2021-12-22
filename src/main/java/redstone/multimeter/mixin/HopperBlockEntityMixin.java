package redstone.multimeter.mixin;

import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.class_3739;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.HopperBlockEntity;
import net.minecraft.util.HopperProvider;
import net.minecraft.world.World;

@Mixin(HopperBlockEntity.class)
public abstract class HopperBlockEntityMixin extends BlockEntity implements HopperProvider {
	
	public HopperBlockEntityMixin(class_3739<?> arg) {
		super(arg);
	}
	
	// This fixes an AbstractMethodError
	@Override
	public World getServerWorld() {
		return world;
	}
}
