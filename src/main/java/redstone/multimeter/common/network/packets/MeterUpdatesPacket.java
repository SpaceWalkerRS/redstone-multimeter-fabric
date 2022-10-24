package redstone.multimeter.common.network.packets;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap.Entry;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.network.ServerPlayerEntity;

import redstone.multimeter.client.MultimeterClient;
import redstone.multimeter.common.meter.MeterProperties;
import redstone.multimeter.common.network.RSMMPacket;
import redstone.multimeter.server.MultimeterServer;

public class MeterUpdatesPacket implements RSMMPacket {
	
	private List<Long> removedMeters;
	private Long2ObjectMap<MeterProperties> meterUpdates;
	private List<Long> meterSwaps;
	
	public MeterUpdatesPacket() {
		this.removedMeters = new ArrayList<>();
		this.meterUpdates = new Long2ObjectOpenHashMap<>();
		this.meterSwaps = new ArrayList<>();
	}
	
	public MeterUpdatesPacket(List<Long> removedMeters, Map<Long, MeterProperties> updates, List<Long> meterSwaps) {
		this.removedMeters = new ArrayList<>(removedMeters);
		this.meterUpdates = new Long2ObjectOpenHashMap<>(updates);
		this.meterSwaps = new ArrayList<>(meterSwaps);
	}
	
	@Override
	public void encode(NbtCompound data) {
		NbtList list = new NbtList();
		
		for (Entry<MeterProperties> entry : meterUpdates.long2ObjectEntrySet()) {
			long id = entry.getLongKey();
			MeterProperties update = entry.getValue();
			
			NbtCompound nbt = update.toNbt();
			nbt.putLong("id", id);
			list.add(nbt);
		}
		
		data.putLongArray("removed meters", removedMeters);
		data.put("meter updates", list);
		data.putLongArray("meter swaps", meterSwaps);
	}
	
	@Override
	public void decode(NbtCompound data) {
		long[] removedIds = data.getLongArray("removed meters");
		NbtList list = data.getList("meter updates", NbtElement.COMPOUND_TYPE);
		long[] swappedIds = data.getLongArray("meter swaps");
		
		for (long id : removedIds) {
			removedMeters.add(id);
		}
		for (int index = 0; index < list.size(); index++) {
			NbtCompound nbt = list.getCompound(index);
			
			long id = nbt.getLong("id");
			MeterProperties update = MeterProperties.fromNbt(nbt);
			meterUpdates.put(id, update);
		}
		for (long id : swappedIds) {
			meterSwaps.add(id);
		}
	}
	
	@Override
	public void execute(MultimeterServer server, ServerPlayerEntity player) {
		
	}
	
	@Override
	public void execute(MultimeterClient client) {
		client.getMeterGroup().updateMeters(removedMeters, meterUpdates, meterSwaps);
	}
}
