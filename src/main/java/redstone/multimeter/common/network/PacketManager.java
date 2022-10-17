package redstone.multimeter.common.network;

import net.minecraft.util.Identifier;

import redstone.multimeter.common.network.packets.*;
import redstone.multimeter.registry.SupplierRegistry;

public class PacketManager {
	
	private static final SupplierRegistry<RSMMPacket> PACKETS;
	
	public static Identifier getChannelId() {
		return PACKETS.getRegistryId();
	}
	
	public static Identifier getId(RSMMPacket packet) {
		return PACKETS.getId(packet);
	}
	
	public static RSMMPacket create(Identifier id) {
		return PACKETS.get(id);
	}
	
	static {
		
		PACKETS = new SupplierRegistry<>("network");
		
		PACKETS.register("handshake"               , HandshakePacket.class             , () -> new HandshakePacket());
		PACKETS.register("tick_phase_tree"         , TickPhaseTreePacket.class         , () -> new TickPhaseTreePacket());
		PACKETS.register("server_tick"             , ServerTickPacket.class            , () -> new ServerTickPacket());
		PACKETS.register("meter_group_subscription", MeterGroupSubscriptionPacket.class, () -> new MeterGroupSubscriptionPacket());
		PACKETS.register("meter_group_default"     , MeterGroupDefaultPacket.class     , () -> new MeterGroupDefaultPacket());
		PACKETS.register("meter_group_refresh"     , MeterGroupRefreshPacket.class     , () -> new MeterGroupRefreshPacket());
		PACKETS.register("meter_updates"           , MeterUpdatesPacket.class          , () -> new MeterUpdatesPacket());
		PACKETS.register("meter_logs"              , MeterLogsPacket.class             , () -> new MeterLogsPacket());
		PACKETS.register("clear_meter_group"       , ClearMeterGroupPacket.class       , () -> new ClearMeterGroupPacket());
		PACKETS.register("add_meter"               , AddMeterPacket.class              , () -> new AddMeterPacket());
		PACKETS.register("remove_meter"            , RemoveMeterPacket.class           , () -> new RemoveMeterPacket());
		PACKETS.register("meter_update"            , MeterUpdatePacket.class           , () -> new MeterUpdatePacket());
		PACKETS.register("teleport_to_meter"       , TeleportToMeterPacket.class       , () -> new TeleportToMeterPacket());
		
	}
}
