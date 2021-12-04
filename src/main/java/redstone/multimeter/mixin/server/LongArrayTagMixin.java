package redstone.multimeter.mixin.server;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.nbt.LongArrayTag;

import redstone.multimeter.interfaces.mixin.ILongArrayTag;

@Mixin(LongArrayTag.class)
public class LongArrayTagMixin implements ILongArrayTag {
	
	@Shadow private long[] value;
	
	@Override
	public long[] getLongArray() {
		return value;
	}
}
