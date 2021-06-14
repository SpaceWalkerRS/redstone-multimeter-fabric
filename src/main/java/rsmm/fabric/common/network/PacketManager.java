package rsmm.fabric.common.network;

import net.minecraft.util.Identifier;

import rsmm.fabric.common.network.packets.*;
import rsmm.fabric.registry.SupplierClazzRegistry;

public class PacketManager {
	
	private static final SupplierClazzRegistry<RSMMPacket> PACKETS;
	
	public static Identifier getPacketChannelId() {
		return PACKETS.getRegistryId();
	}
	
	public static <P extends RSMMPacket> Identifier getId(P packet) {
		return PACKETS.getId(packet);
	}
	
	public static <P extends RSMMPacket> P getPacket(Identifier id) {
		return PACKETS.get(id);
	}
	
	static {
		
		PACKETS = new SupplierClazzRegistry<>("network");
		
		PACKETS.register("join_multimeter_server", JoinMultimeterServerPacket.class, () -> new JoinMultimeterServerPacket());
		PACKETS.register("server_tick"           , ServerTickPacket.class          , () -> new ServerTickPacket());
		PACKETS.register("meter_group_data"      , MeterGroupDataPacket.class      , () -> new MeterGroupDataPacket());
		PACKETS.register("meter_changes"         , MeterChangesPacket.class        , () -> new MeterChangesPacket());
		PACKETS.register("meter_logs"            , MeterLogsPacket.class           , () -> new MeterLogsPacket());
		PACKETS.register("toggle_meter"          , ToggleMeterPacket.class         , () -> new ToggleMeterPacket());
		PACKETS.register("remove_all_meters"     , RemoveAllMetersPacket.class     , () -> new RemoveAllMetersPacket());
		PACKETS.register("add_meter"             , AddMeterPacket.class            , () -> new AddMeterPacket());
		PACKETS.register("remove_meter"          , RemoveMeterPacket.class         , () -> new RemoveMeterPacket());
		PACKETS.register("meter_change"          , MeterChangePacket.class         , () -> new MeterChangePacket());
		PACKETS.register("teleport_to_meter"     , TeleportToMeterPacket.class     , () -> new TeleportToMeterPacket());
	}
}
