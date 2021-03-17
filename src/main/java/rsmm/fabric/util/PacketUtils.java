package rsmm.fabric.util;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

import rsmm.fabric.common.WorldPos;

public class PacketUtils {
	
	public static void writeWorldPos(PacketByteBuf buffer, WorldPos pos) {
		buffer.writeIdentifier(pos.getWorldId());
		buffer.writeBlockPos(pos);
	}
	
	public static WorldPos readWorldPos(PacketByteBuf buffer) {
		Identifier worldId = buffer.readIdentifier();
		BlockPos pos = buffer.readBlockPos();
		
		return new WorldPos(worldId, pos);
	}
}
