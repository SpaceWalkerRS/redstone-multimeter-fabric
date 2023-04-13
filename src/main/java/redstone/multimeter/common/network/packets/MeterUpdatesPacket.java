package redstone.multimeter.common.network.packets;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap.Entry;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerPlayer;

import redstone.multimeter.client.MultimeterClient;
import redstone.multimeter.common.meter.MeterProperties;
import redstone.multimeter.common.network.RSMMPacket;
import redstone.multimeter.server.MultimeterServer;

public class MeterUpdatesPacket implements RSMMPacket {
	
	private List<Long> removedMeters;
	private Long2ObjectMap<MeterProperties> meterUpdates;
	private List<Long> meters;
	
	public MeterUpdatesPacket() {
		this.removedMeters = new ArrayList<>();
		this.meterUpdates = new Long2ObjectOpenHashMap<>();
		this.meters = new ArrayList<>();
	}
	
	public MeterUpdatesPacket(List<Long> removedMeters, Map<Long, MeterProperties> updates, List<Long> meters) {
		this.removedMeters = new ArrayList<>(removedMeters);
		this.meterUpdates = new Long2ObjectOpenHashMap<>(updates);
		this.meters = new ArrayList<>(meters);
	}
	
	@Override
	public void encode(CompoundTag data) {
		if (!removedMeters.isEmpty()) {
			data.putLongArray("removed", removedMeters);
		}
		if (!meterUpdates.isEmpty()) {
			ListTag list = new ListTag();

			for (Entry<MeterProperties> entry : meterUpdates.long2ObjectEntrySet()) {
				long id = entry.getLongKey();
				MeterProperties update = entry.getValue();

				CompoundTag nbt = update.toNbt();
				nbt.putLong("id", id);
				list.add(nbt);
			}

			data.put("updates", list);
		}
		if (!meters.isEmpty()) {
			data.putLongArray("meters", meters);
		}
	}
	
	@Override
	public void decode(CompoundTag data) {
		if (data.contains("removed")) {
			long[] removed = data.getLongArray("removed");

			for (long id : removed) {
				removedMeters.add(id);
			}
		}
		if (data.contains("updates")) {
			ListTag updates = data.getList("updates", Tag.TAG_COMPOUND);

			for (int i = 0; i < updates.size(); i++) {
				CompoundTag nbt = updates.getCompound(i);
				long id = nbt.getLong("id");
				MeterProperties update = MeterProperties.fromNbt(nbt);

				meterUpdates.put(id, update);
			}
		}
		if (data.contains("meters")) {
			long[] ids = data.getLongArray("meters");

			for (long id : ids) {
				meters.add(id);
			}
		}
	}
	
	@Override
	public void handle(MultimeterServer server, ServerPlayer player) {
	}
	
	@Override
	public void handle(MultimeterClient client) {
		client.getMeterGroup().updateMeters(removedMeters, meterUpdates, meters);
	}
}
