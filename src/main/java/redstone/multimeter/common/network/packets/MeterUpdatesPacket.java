package redstone.multimeter.common.network.packets;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.LongTag;
import net.minecraft.server.network.ServerPlayerEntity;

import redstone.multimeter.client.MultimeterClient;
import redstone.multimeter.common.meter.MeterProperties;
import redstone.multimeter.common.network.RSMMPacket;
import redstone.multimeter.interfaces.mixin.IListTag;
import redstone.multimeter.server.MultimeterServer;
import redstone.multimeter.util.NbtUtils;

public class MeterUpdatesPacket implements RSMMPacket {
	
	private List<Long> removedMeters;
	private Map<Long, MeterProperties> meterUpdates;
	
	public MeterUpdatesPacket() {
		this.removedMeters = new ArrayList<>();
		this.meterUpdates = new HashMap<>();
	}
	
	public MeterUpdatesPacket(List<Long> removedMeters, Map<Long, MeterProperties> updates) {
		this.removedMeters = new ArrayList<>(removedMeters);
		this.meterUpdates = new HashMap<>(updates);
	}
	
	@Override
	public void encode(CompoundTag data) {
		ListTag ids = new ListTag();
		ListTag updates = new ListTag();
		
		for (int index = 0; index < removedMeters.size(); index++) {
			long id = removedMeters.get(index);
			ids.add(new LongTag(id));
		}
		for (Entry<Long, MeterProperties> entry : meterUpdates.entrySet()) {
			long id = entry.getKey();
			MeterProperties update = entry.getValue();
			
			CompoundTag nbt = update.toNbt();
			nbt.putLong("id", id);
			updates.add(nbt);
		}
		
		data.put("removed meters", ids);
		data.put("meter updates", updates);
	}
	
	@Override
	public void decode(CompoundTag data) {
		ListTag idList = data.getList("removed meters", NbtUtils.TYPE_LONG);
		ListTag updateList = data.getList("meter updates", NbtUtils.TYPE_COMPOUND);
		
		for (int index = 0; index < idList.size(); index++) {
			removedMeters.add(((IListTag)idList).getLong(index));
		}
		for (int index = 0; index < updateList.size(); index++) {
			CompoundTag nbt = updateList.getCompound(index);
			
			long id = nbt.getLong("id");
			MeterProperties update = MeterProperties.fromNbt(nbt);
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
