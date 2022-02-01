package redstone.multimeter.common.network.packets;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;

import redstone.multimeter.RedstoneMultimeterMod;
import redstone.multimeter.client.MultimeterClient;
import redstone.multimeter.common.network.RSMMPacket;
import redstone.multimeter.server.MultimeterServer;

public class HandshakePacket implements RSMMPacket {
	
	private String modVersion;
	
	public HandshakePacket() {
		modVersion = RedstoneMultimeterMod.MOD_VERSION;
	}
	
	@Override
	public void encode(NBTTagCompound data) {
		data.setString("mod version", modVersion);
	}
	
	@Override
	public void decode(NBTTagCompound data) {
		modVersion = data.getString("mod version");
	}
	
	@Override
	public void execute(MultimeterServer server, EntityPlayerMP player) {
		server.onHandshake(player, modVersion);
	}
	
	@Override
	public void execute(MultimeterClient client) {
		client.onHandshake(modVersion);
	}
}
