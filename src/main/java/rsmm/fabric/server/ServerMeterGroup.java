package rsmm.fabric.server;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiPredicate;
import java.util.function.Consumer;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;

import rsmm.fabric.common.Meter;
import rsmm.fabric.common.MeterGroup;
import rsmm.fabric.common.MeterProperties;
import rsmm.fabric.common.WorldPos;
import rsmm.fabric.common.event.EventType;
import rsmm.fabric.common.network.packets.MeterUpdatesPacket;

public class ServerMeterGroup extends MeterGroup {
	
	private final Multimeter multimeter;
	private final Set<ServerPlayerEntity> subscribers;
	private final ServerLogManager logManager;
	
	private final List<Long> removedMeters;
	private final Map<Long, MeterProperties> meterUpdates;
	
	public ServerMeterGroup(Multimeter multimeter, String name) {
		super(name);
		
		this.multimeter = multimeter;
		this.subscribers = new HashSet<>();
		this.logManager = new ServerLogManager(this);
		
		this.removedMeters = new ArrayList<>();
		this.meterUpdates = new LinkedHashMap<>();
	}
	
	@Override
	public void clear() {
		super.clear();
		
		removedMeters.clear();
		meterUpdates.clear();
	}
	
	@Override
	protected boolean moveMeter(Meter meter, WorldPos newPos) {
		if (hasMeterAt(newPos)) {
			return false;
		}
		
		World world = multimeter.getMultimeterServer().getWorldOf(newPos);
		
		if (world == null) {
			return false;
		}
		
		return super.moveMeter(meter, newPos);
	}
	
	@Override
	protected void meterPosChanged(Meter meter) {
		addMeterUpdate(meter.getId(), properties -> properties.setPos(meter.getPos()));
	}
	
	@Override
	protected void meterNameChanged(Meter meter) {
		addMeterUpdate(meter.getId(), properties -> properties.setName(meter.getName()));
	}
	
	@Override
	protected void meterColorChanged(Meter meter) {
		addMeterUpdate(meter.getId(), properties -> properties.setColor(meter.getColor()));
	}
	
	@Override
	protected void meterMovableChanged(Meter meter) {
		addMeterUpdate(meter.getId(), properties -> properties.setMovable(meter.isMovable()));
	}
	
	@Override
	protected void meterEventTypesChanged(Meter meter) {
		addMeterUpdate(meter.getId(), properties -> properties.setEventTypes(meter.getEventTypes()));
	}
	
	@Override
	public ServerLogManager getLogManager() {
		return logManager;
	}
	
	public Multimeter getMultimeter() {
		return multimeter;
	}
	
	public void addMeter(MeterProperties properties) {
		Meter meter = new Meter(properties);
		
		if (addMeter(meter)) {
			meterUpdates.put(meter.getId(), properties);
		}
	}
	
	public void removeMeter(long id) {
		Meter meter = getMeter(id);
		
		if (meter != null && removeMeter(meter)) {
			removedMeters.add(id);
			meterUpdates.remove(id);
		}
	}
	
	public void updateMeter(long id, MeterProperties newProperties) {
		Meter meter = getMeter(id);
		
		if (meter != null) {
			updateMeter(meter, newProperties);
		}
	}
	
	private void addMeterUpdate(long id, Consumer<MeterProperties> update) {
		update.accept(meterUpdates.computeIfAbsent(id, key -> new MeterProperties()));
	}
	
	public boolean tryMoveMeter(long id, WorldPos newPos, boolean byPiston) {
		if (!hasMeter(id)) {
			return false;
		}
		
		Meter meter = getMeter(id);
		
		if (byPiston && !meter.isMovable()) {
			return false;
		}
		
		return moveMeter(meter, newPos);
	}
	
	public boolean hasSubscribers() {
		return !subscribers.isEmpty();
	}
	
	public Set<ServerPlayerEntity> getSubscribers() {
		return Collections.unmodifiableSet(subscribers);
	}
	
	public void addSubscriber(ServerPlayerEntity player) {
		subscribers.add(player);
	}
	
	public void removeSubscriber(ServerPlayerEntity player) {
		subscribers.remove(player);
	}
	
	public void flushUpdates() {
		if (removedMeters.isEmpty() && meterUpdates.isEmpty()) {
			return;
		}
		
		MeterUpdatesPacket packet = new MeterUpdatesPacket(removedMeters, meterUpdates);
		multimeter.getMultimeterServer().getPacketHandler().sendPacketToPlayers(packet, subscribers);
		
		removedMeters.clear();
		meterUpdates.clear();
	}
	
	public void tryLogEvent(WorldPos pos, EventType type, int metaData, BiPredicate<ServerMeterGroup, Meter> meterPredicate) {
		if (hasMeterAt(pos)) {
			Meter meter = getMeterAt(pos);
			
			if (meterPredicate.test(this, meter)) {
				logManager.logEvent(meter, type, metaData);
			}
		}
	}
}
