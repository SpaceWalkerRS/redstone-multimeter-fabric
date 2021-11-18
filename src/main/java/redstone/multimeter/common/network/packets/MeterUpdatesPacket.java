package redstone.multimeter.common.network.packets;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap.Entry;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.network.ServerPlayerEntity;

import redstone.multimeter.client.MultimeterClient;
import redstone.multimeter.common.meter.MeterProperties;
import redstone.multimeter.common.network.RSMMPacket;
import redstone.multimeter.server.MultimeterServer;

public class MeterUpdatesPacket implements RSMMPacket {
	
	private List<Long> removedMeters;
	private Long2ObjectMap<MeterProperties> meterUpdates;
	
	public MeterUpdatesPacket() {
		this.removedMeters = new ArrayList<>();
		this.meterUpdates = new Long2ObjectOpenHashMap<>();
	}
	
	public MeterUpdatesPacket(List<Long> removedMeters, Map<Long, MeterProperties> updates) {
		this.removedMeters = new ArrayList<>(removedMeters);
		this.meterUpdates = new Long2ObjectOpenHashMap<>(updates);
	}
	
	@Override
	public void encode(CompoundTag data) {
		ListTag list = new ListTag();
		
		for (Entry<MeterProperties> entry : meterUpdates.long2ObjectEntrySet()) {
			long id = entry.getLongKey();
			MeterProperties update = entry.getValue();
			
			CompoundTag nbt = update.toNBT();
			nbt.putLong("id", id);
			list.add(nbt);
		}
		
		data.putLongArray("removed meters", removedMeters);
		data.put("meter updates", list);
	}
	
	@Override
	public void decode(CompoundTag data) {
		long[] ids = data.getLongArray("removed meters");
		ListTag list = data.getList("meter updates", 10);
		
		for (long id : ids) {
			removedMeters.add(id);
		}
		for (int index = 0; index < list.size(); index++) {
			CompoundTag nbt = list.getCompound(index);
			
			long id = nbt.getLong("id");
			MeterProperties update = MeterProperties.fromNBT(nbt);
			meterUpdates.put(id, update);
		}
	}
	
	@Override
	public void execute(MultimeterServer server, ServerPlayerEntity player) {
		
	}
	
	@Override
	public void execute(MultimeterClient client) {
		client.getMeterGroup().updateMeters(removedMeters, meterUpdates);
	}
}
