package redstone.multimeter.mixin.common;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.nbt.NbtByteArray;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtLong;

import redstone.multimeter.interfaces.mixin.INbtList;
import redstone.multimeter.util.NbtUtils;

@Mixin(NbtList.class)
public class NbtListMixin implements INbtList {

	@Shadow
	private List<NbtElement> elements;

	@Override
	public NbtLong getLong(int index) {
		if (index >= 0 && index < elements.size()) {
			NbtElement element = this.elements.get(index);
			return element.getType() == NbtUtils.TYPE_LONG ? (NbtLong)element : new NbtLong(null, 0L);
		} else {
			return new NbtLong(null, 0L);
		}
	}

	@Override
	public NbtByteArray getByteArray(int index) {
		if (index >= 0 && index < elements.size()) {
			NbtElement element = this.elements.get(index);
			return element.getType() == NbtUtils.TYPE_BYTE_ARRAY ? (NbtByteArray)element : new NbtByteArray(null, new byte[0]);
		} else {
			return new NbtByteArray(null, new byte[0]);
		}
	}
}
