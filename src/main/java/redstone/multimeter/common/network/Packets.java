package redstone.multimeter.common.network;

import redstone.multimeter.common.network.packets.*;
import redstone.multimeter.registry.SupplierRegistry;

public class Packets {

	private static final SupplierRegistry<RSMMPacket> REGISTRY;

	public static String getChannel() {
		return REGISTRY.getRegistryKey();
	}

	public static String getKey(RSMMPacket packet) {
		return REGISTRY.getKey(packet);
	}

	public static RSMMPacket create(String key) {
		return REGISTRY.get(key);
	}

	static {

		REGISTRY = new SupplierRegistry<>();

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
