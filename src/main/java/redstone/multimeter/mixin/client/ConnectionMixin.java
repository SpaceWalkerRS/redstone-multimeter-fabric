package redstone.multimeter.mixin.client;

import java.util.Iterator;
import java.util.Queue;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.network.Connection;
import net.minecraft.network.handler.PacketHandler;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.CustomPayloadC2SPacket;
import net.minecraft.network.packet.s2c.play.CustomPayloadS2CPacket;
import redstone.multimeter.common.network.Packets;
import redstone.multimeter.interfaces.mixin.IConnection;

@Mixin(Connection.class)
public class ConnectionMixin implements IConnection {

	@Shadow private Queue<Packet> readQueue;
	@Shadow private PacketHandler listener;

	@Override
	public void rsmm$handleRsmmPackets() {
		Iterator<Packet> it = readQueue.iterator();

		while (it.hasNext()) {
			Packet p = it.next();

			if (p instanceof CustomPayloadC2SPacket) {
				CustomPayloadC2SPacket packet = (CustomPayloadC2SPacket) p;

				if (Packets.getChannel().equals(packet.getChannel())) {
					p.handle(listener);
					it.remove();
				}
			}
			if (p instanceof CustomPayloadS2CPacket) {
				CustomPayloadS2CPacket packet = (CustomPayloadS2CPacket) p;

				if (Packets.getChannel().equals(packet.getChannel())) {
					p.handle(listener);
					it.remove();
				}
			}
		}
	}
}
