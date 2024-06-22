package redstone.multimeter.util;

import net.minecraft.nbt.ByteTag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;

public class NbtUtils {

	public static final Tag NULL = ByteTag.ZERO;

	public static CompoundTag resourceLocationToNbt(ResourceLocation location) {
		CompoundTag nbt = new CompoundTag();

		nbt.putString("namespace", location.getNamespace());
		nbt.putString("path", location.getPath());

		return nbt;
	}

	public static ResourceLocation nbtToResourceLocation(CompoundTag nbt) {
		String namespace = nbt.getString("namespace");
		String path = nbt.getString("path");

		return ResourceLocation.fromNamespaceAndPath(namespace, path);
	}
}
