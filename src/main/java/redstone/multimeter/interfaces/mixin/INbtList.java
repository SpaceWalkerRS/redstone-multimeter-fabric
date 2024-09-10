package redstone.multimeter.interfaces.mixin;

import net.minecraft.nbt.NbtByteArray;
import net.minecraft.nbt.NbtLong;

public interface INbtList {

	NbtLong getLong(int index);

	NbtByteArray getByteArray(int index);

}
