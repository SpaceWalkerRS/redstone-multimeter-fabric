package redstone.multimeter.common.network.packets;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap.Entry;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtLong;
import net.minecraft.server.entity.living.player.ServerPlayerEntity;

import redstone.multimeter.client.MultimeterClient;
import redstone.multimeter.common.meter.MeterProperties;
import redstone.multimeter.common.network.RSMMPacket;
import redstone.multimeter.interfaces.mixin.INbtList;
import redstone.multimeter.server.MultimeterServer;
import redstone.multimeter.util.NbtUtils;

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
	public void encode(NbtCompound data) {
		if (!removedMeters.isEmpty()) {
			NbtList list = new NbtList();

			for (int i = 0; i < removedMeters.size(); i++) {
				list.add(new NbtLong(removedMeters.get(i)));
			}

			data.put("removed", list);
		}
		if (!meterUpdates.isEmpty()) {
			NbtList list = new NbtList();

			for (Entry<MeterProperties> entry : meterUpdates.long2ObjectEntrySet()) {
				long id = entry.getLongKey();
				MeterProperties update = entry.getValue();

				NbtCompound nbt = update.toNbt();
				nbt.putLong("id", id);
				list.add(nbt);
			}

			data.put("updates", list);
		}
		if (!meters.isEmpty()) {
			NbtList list = new NbtList();

			for (int i = 0; i < meters.size(); i++) {
				list.add(new NbtLong(meters.get(i)));
			}

			data.put("meters", list);
		}
	}
	
	@Override
	public void decode(NbtCompound data) {
		if (data.contains("removed")) {
			NbtList ids = data.getList("removed", NbtUtils.TYPE_LONG);

			for (int i = 0; i < ids.size(); i++) {
				NbtLong nbt = ((INbtList)ids).getLong(i);
				long id = nbt.getLong();

				removedMeters.add(id);
			}
		}
		if (data.contains("updates")) {
			NbtList updates = data.getList("updates", NbtUtils.TYPE_COMPOUND);

			for (int i = 0; i < updates.size(); i++) {
				NbtCompound nbt = updates.getCompound(i);
				long id = nbt.getLong("id");
				MeterProperties update = MeterProperties.fromNbt(nbt);

				meterUpdates.put(id, update);
			}
		}
		if (data.contains("meters")) {
			NbtList ids = data.getList("meters", NbtUtils.TYPE_LONG);

			for (int i = 0; i < ids.size(); i++) {
				NbtLong nbt = ((INbtList)ids).getLong(i);
				long id = nbt.getLong();

				meters.add(id);
			}
		}
	}
	
	@Override
	public void handle(MultimeterServer server, ServerPlayerEntity player) {
	}
	
	@Override
	public void handle(MultimeterClient client) {
		client.getMeterGroup().updateMeters(removedMeters, meterUpdates, meters);
	}
}
