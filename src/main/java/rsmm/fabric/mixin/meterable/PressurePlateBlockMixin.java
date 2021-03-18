package rsmm.fabric.mixin.meterable;

import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.block.BlockState;
import net.minecraft.block.PressurePlateBlock;
import net.minecraft.state.property.Properties;

import rsmm.fabric.interfaces.mixin.IPressurePlate;

@Mixin(PressurePlateBlock.class)
public class PressurePlateBlockMixin implements IPressurePlate {
	
	@Override
	public boolean isActive(BlockState state) {
		return state.get(Properties.POWERED);
	}
}
