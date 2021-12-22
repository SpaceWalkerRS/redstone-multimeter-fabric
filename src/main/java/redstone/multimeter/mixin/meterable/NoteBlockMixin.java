package redstone.multimeter.mixin.meterable;

import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.block.NoteBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.NoteBlockBlockEntity;
import net.minecraft.world.World;

import redstone.multimeter.block.Meterable;

@Mixin(NoteBlock.class)
public abstract class NoteBlockMixin implements Meterable {
	
	@Override
	public boolean isActive(World world, int x, int y, int z, int metadata) {
		BlockEntity blockEntity = world.method_3781(x, y, z);
		
		if (blockEntity instanceof NoteBlockBlockEntity) {
			return ((NoteBlockBlockEntity)blockEntity).field_559;
		}
		
		return false;
	}
}
