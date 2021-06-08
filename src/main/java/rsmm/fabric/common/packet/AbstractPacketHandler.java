package rsmm.fabric.common.packet;

import java.util.HashMap;
import java.util.Map;

import io.netty.buffer.Unpooled;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Packet;
import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;

import rsmm.fabric.RedstoneMultimeterMod;
import rsmm.fabric.common.packet.types.*;

public abstract class AbstractPacketHandler {
	
	public static final Identifier PACKET_IDENTIFIER = new Identifier(RedstoneMultimeterMod.MOD_ID, "network");
	
	protected Packet<?> encodePacket(AbstractRSMMPacket packet) {
		PacketByteBuf buffer = new PacketByteBuf(Unpooled.buffer());
		CompoundTag data = new CompoundTag();
		
		PacketType packetType = PacketType.fromPacket(packet);
		if (packetType == PacketType.INVALID) {
			throw new IllegalStateException("Unable to encode packet: " + packet.getClass());
		}
		packet.encode(data);
		
		buffer.writeByte(packetType.getIndex());
		buffer.writeCompoundTag(data);
		
		return toCustomPayloadPacket(buffer);
	}
	
	protected abstract Packet<?> toCustomPayloadPacket(PacketByteBuf buffer);
	
	protected AbstractRSMMPacket decodePacket(PacketByteBuf buffer) throws InstantiationException, IllegalAccessException {
		byte index = buffer.readByte();
		CompoundTag data = buffer.readCompoundTag();
		
		PacketType type = PacketType.fromIndex(index);
		if (type == PacketType.INVALID) {
			throw new IllegalStateException("Unable to decode packet type: " + index);
		}
		AbstractRSMMPacket packet = type.getClazz().newInstance();
		
		packet.decode(data);
		
		return packet;
	}
	
	public abstract void sendPacket(AbstractRSMMPacket packet);
	
	private enum PacketType {
		
		INVALID(0, null),
		JOIN_MULTIMETER_SERVER(1, JoinMultimeterServerPacket.class),
		SERVER_TICK(2, ServerTickPacket.class),
		METER_GROUP_DATA(3, MeterGroupDataPacket.class),
		METER_LOGS(4, MeterLogsPacket.class),
		METER_CHANGES(5, MeterChangesPacket.class),
		TOGGLE_METER(6, ToggleMeterPacket.class),
		REMOVE_ALL_METERS(7, RemoveAllMetersPacket.class),
		ADD_METER(8, AddMeterPacket.class),
		REMOVE_METER(9, RemoveMeterPacket.class),
		METER_CHANGE(10, MeterChangePacket.class),
		TELEPORT_TO_METER(11, TeleportToMeterPacket.class);
		
		private static final PacketType[] PACKET_TYPES;
		private static final Map<Class<? extends AbstractRSMMPacket>, PacketType> PACKET_TO_TYPE;
		
		static {
			PACKET_TYPES = new PacketType[values().length];
			PACKET_TO_TYPE = new HashMap<>();
			
			for (PacketType packetType : values()) {
				PACKET_TYPES[packetType.index] = packetType;
				PACKET_TO_TYPE.put(packetType.clazz, packetType);
			}
		}
		
		private final int index;
		private final Class<? extends AbstractRSMMPacket> clazz;
		
		PacketType(int index, Class<? extends AbstractRSMMPacket> clazz) {
			this.index = index;
			this.clazz = clazz;
		}
		
		public static PacketType fromIndex(int index) {
			if (index > 0 && index < PACKET_TYPES.length) {
				return PACKET_TYPES[index];
			}
			
			return INVALID;
		}
		
		public static PacketType fromPacket(AbstractRSMMPacket packet) {
			return PACKET_TO_TYPE.getOrDefault(packet.getClass(), INVALID);
		}
		
		public int getIndex() {
			return index;
		}
		
		public Class<? extends AbstractRSMMPacket> getClazz() {
			return clazz;
		}
	}
}
