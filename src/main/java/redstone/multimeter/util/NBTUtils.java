package redstone.multimeter.util;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;

public class NBTUtils {
	
	public static NbtCompound identifierToNBT(Identifier id) {
		NbtCompound nbt = new NbtCompound();
		
		nbt.putString("namespace", id.getNamespace());
		nbt.putString("path", id.getPath());
		
		return nbt;
	}
	
	public static Identifier NBTToIdentifier(NbtCompound nbt) {
		String namespace = nbt.getString("namespace");
		String path = nbt.getString("path");
		
		return new Identifier(namespace, path);
	}
}
