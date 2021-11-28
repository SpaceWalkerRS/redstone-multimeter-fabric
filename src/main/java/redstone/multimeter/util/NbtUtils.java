package redstone.multimeter.util;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;

public class NbtUtils {
	
	public static NbtCompound identifierToNbt(Identifier id) {
		NbtCompound nbt = new NbtCompound();
		
		nbt.putString("namespace", id.getNamespace());
		nbt.putString("path", id.getPath());
		
		return nbt;
	}
	
	public static Identifier nbtToIdentifier(NbtCompound nbt) {
		String namespace = nbt.getString("namespace");
		String path = nbt.getString("path");
		
		return new Identifier(namespace, path);
	}
}
