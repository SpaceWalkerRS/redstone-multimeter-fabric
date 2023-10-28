package redstone.multimeter.common.network;

import java.util.function.BiConsumer;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.FriendlyByteBuf.Reader;
import net.minecraft.resources.ResourceLocation;

import redstone.multimeter.common.network.packets.*;
import redstone.multimeter.registry.SupplierRegistry;

public class Packets {

	public static final Reader<PacketWrapper> READER = buffer -> {
		ResourceLocation key = buffer.readResourceLocation();
		RSMMPacket packet = Packets.create(key);

		if (packet == null) {
			throw new IllegalStateException("Unable to decode packet: " + key);
		}

		CompoundTag data = buffer.readNbt();
		packet.decode(data);

		return new PacketWrapper(packet);
	};
	public static final BiConsumer<RSMMPacket, FriendlyByteBuf> WRITER = (packet, buffer) -> {
		ResourceLocation key = Packets.getKey(packet);

		if (key == null) {
			throw new IllegalStateException("Unable to encode packet: " + packet.getClass());
		}

		CompoundTag data = new CompoundTag();
		packet.encode(data);

		buffer.writeResourceLocation(key);
		buffer.writeNbt(data);
	};

	private static final SupplierRegistry<RSMMPacket> REGISTRY;

	public static ResourceLocation getChannel() {
		return REGISTRY.getRegistryKey();
	}

	public static ResourceLocation getKey(RSMMPacket packet) {
		return REGISTRY.getKey(packet);
	}

	public static RSMMPacket create(ResourceLocation key) {
		return REGISTRY.get(key);
	}

	static {

		REGISTRY = new SupplierRegistry<>("network");

		REGISTRY.register("handshake"               , HandshakePacket.class             , () -> new HandshakePacket());
		REGISTRY.register("tick_phase_tree"         , TickPhaseTreePacket.class         , () -> new TickPhaseTreePacket());
		REGISTRY.register("rebuild_tick_phase_tree" , RebuildTickPhaseTreePacket.class  , () -> new RebuildTickPhaseTreePacket());
		REGISTRY.register("tick_time"               , TickTimePacket.class              , () -> new TickTimePacket());
		REGISTRY.register("meter_group_subscription", MeterGroupSubscriptionPacket.class, () -> new MeterGroupSubscriptionPacket());
		REGISTRY.register("meter_group_default"     , MeterGroupDefaultPacket.class     , () -> new MeterGroupDefaultPacket());
		REGISTRY.register("meter_group_refresh"     , MeterGroupRefreshPacket.class     , () -> new MeterGroupRefreshPacket());
		REGISTRY.register("meter_updates"           , MeterUpdatesPacket.class          , () -> new MeterUpdatesPacket());
		REGISTRY.register("meter_logs"              , MeterLogsPacket.class             , () -> new MeterLogsPacket());
		REGISTRY.register("clear_meter_group"       , ClearMeterGroupPacket.class       , () -> new ClearMeterGroupPacket());
		REGISTRY.register("add_meter"               , AddMeterPacket.class              , () -> new AddMeterPacket());
		REGISTRY.register("remove_meter"            , RemoveMeterPacket.class           , () -> new RemoveMeterPacket());
		REGISTRY.register("meter_update"            , MeterUpdatePacket.class           , () -> new MeterUpdatePacket());
		REGISTRY.register("meter_index"             , MeterIndexPacket.class            , () -> new MeterIndexPacket());
		REGISTRY.register("teleport_to_meter"       , TeleportToMeterPacket.class       , () -> new TeleportToMeterPacket());

	}
}
