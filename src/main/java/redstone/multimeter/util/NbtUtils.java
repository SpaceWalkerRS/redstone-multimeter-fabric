package redstone.multimeter.util;

import net.minecraft.nbt.NbtByte;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.util.Identifier;

public class NbtUtils {
	
	public static final NbtElement NULL = NbtByte.ZERO;
	
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
