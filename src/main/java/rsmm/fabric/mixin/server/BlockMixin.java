package rsmm.fabric.mixin.server;

import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.block.Block;

import rsmm.fabric.common.Meterable;
import rsmm.fabric.interfaces.mixin.IBlock;

@Mixin(Block.class)
public abstract class BlockMixin implements IBlock {
	
	@Override
	public boolean isMeterable() {
		return this instanceof Meterable;
	}
}
