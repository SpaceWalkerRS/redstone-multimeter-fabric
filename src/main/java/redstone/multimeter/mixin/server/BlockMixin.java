package redstone.multimeter.mixin.server;

import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.block.Block;

import redstone.multimeter.interfaces.mixin.IBlock;

@Mixin(Block.class)
public abstract class BlockMixin implements IBlock {
	
}
