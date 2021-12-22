package redstone.multimeter.mixin.server;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.class_3323;

import redstone.multimeter.interfaces.mixin.ILongArrayTag;

@Mixin(class_3323.class)
public class LongArrayTagMixin implements ILongArrayTag {
	
	@Shadow private long[] field_16245;
	
	@Override
	public long[] getLongArray() {
		return field_16245;
	}
}
