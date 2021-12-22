package redstone.multimeter.mixin.server;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.LongTag;
import net.minecraft.nbt.Tag;

import redstone.multimeter.interfaces.mixin.IListTag;
import redstone.multimeter.util.NbtUtils;

@Mixin(ListTag.class)
public class ListTagMixin implements IListTag {
	
	@Shadow private List<Tag> value;
	@Shadow private byte type;
	
	@Override
	public long getLong(int index) {
		if (index < 0 || index >= value.size()) {
			return 0L;
		}
		
		Tag tag = value.get(index);
		
		if (tag.getType() == NbtUtils.TYPE_LONG) {
			return ((LongTag)tag).method_7371();
		}
		
		return 0L;
	}
}
