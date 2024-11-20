package redstone.multimeter.mixin.client;

import java.util.Iterator;
import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.network.LocalConnection;
import net.minecraft.network.PacketHandler;
import net.minecraft.network.packet.CustomPayloadPacket;
import net.minecraft.network.packet.Packet;

import redstone.multimeter.common.network.Packets;
import redstone.multimeter.interfaces.mixin.IConnection;

@Mixin(LocalConnection.class)
public class LocalConnectionMixin implements IConnection {

	@Shadow private List<Packet> readQueue;
	@Shadow private PacketHandler listener;

	@Override
	public void rsmm$handleRsmmPackets() {
		Iterator<Packet> it = readQueue.iterator();

		while (it.hasNext()) {
			Packet p = it.next();

			if (p instanceof CustomPayloadPacket) {
				CustomPayloadPacket packet = (CustomPayloadPacket) p;

				if (Packets.getChannel().equals(packet.channel)) {
					it.remove();
					p.handle(listener);
				}
			}
		}
	}
}
