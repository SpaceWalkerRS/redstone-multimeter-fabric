package redstone.multimeter.common.network;

import java.io.DataInput;
import java.io.IOException;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.Packet;
import redstone.multimeter.RedstoneMultimeterMod;
import redstone.multimeter.mixin.common.PacketAccess;
import redstone.multimeter.util.DataStreams;

public abstract class PacketHandler {

	public Packet encode(RSMMPacket packet) {
		String key = Packets.getKey(packet);

		if (key == null) {
			throw new IllegalStateException("Unable to encode packet: " + packet.getClass());
		}

		NbtCompound data = new NbtCompound();
		packet.encode(data);

		try {
			byte[] buffer = DataStreams.output(output -> {
				Packet.writeString(key, output);
				PacketAccess.rsmm$writeCompound(data, output);
			}).toByteArray();

			return toCustomPayload(Packets.getChannel(), buffer);
		} catch (IOException e) {
			RedstoneMultimeterMod.LOGGER.warn("unable to encode packet " + key, e);
			return toCustomPayload("", new byte[0]);
		}
	}

	protected abstract Packet toCustomPayload(String channel, byte[] data);

	protected RSMMPacket decode(DataInput input) throws IOException {
		String key = Packet.readString(input, 32767);
		RSMMPacket packet = Packets.create(key);

		if (packet == null) {
			throw new IllegalStateException("Unable to decode packet: " + key);
		}

		NbtCompound data = Packet.readCompound(input);
		packet.decode(data);

		return packet;
	}
}
