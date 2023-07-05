package redstone.multimeter.mixin.common;

import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.block.Block;

import redstone.multimeter.interfaces.mixin.IBlock;

@Mixin(Block.class)
public class BlockMixin implements IBlock {
}
