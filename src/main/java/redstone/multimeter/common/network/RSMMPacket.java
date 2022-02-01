package redstone.multimeter.common.network;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;

import redstone.multimeter.client.MultimeterClient;
import redstone.multimeter.server.MultimeterServer;

public interface RSMMPacket {
	
	public void encode(NBTTagCompound data);
	
	public void decode(NBTTagCompound data);
	
	public void execute(MultimeterServer server, EntityPlayerMP player);
	
	public void execute(MultimeterClient client);
	
}
