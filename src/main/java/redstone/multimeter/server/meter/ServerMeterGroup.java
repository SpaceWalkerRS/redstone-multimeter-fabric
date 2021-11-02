package redstone.multimeter.server.meter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.BiPredicate;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;

import redstone.multimeter.common.WorldPos;
import redstone.multimeter.common.meter.Meter;
import redstone.multimeter.common.meter.MeterGroup;
import redstone.multimeter.common.meter.MeterProperties;
import redstone.multimeter.common.meter.event.EventType;
import redstone.multimeter.common.network.packets.MeterUpdatesPacket;
import redstone.multimeter.server.Multimeter;
import redstone.multimeter.server.meter.log.ServerLogManager;

public class ServerMeterGroup extends MeterGroup {
	
	private final Multimeter multimeter;
	private final ServerLogManager logManager;
	
	private final UUID owner;
	private final Set<UUID> members;
	private final Set<UUID> subscribers;
	
	private final List<Long> removedMeters;
	private final Map<Long, MeterProperties> meterUpdates;
	
	private boolean isPrivate;
	private boolean idle;
	private long idleTime;
	
	public ServerMeterGroup(Multimeter multimeter, String name, ServerPlayerEntity owner) {
		super(name);
		
		this.multimeter = multimeter;
		this.logManager = new ServerLogManager(this);
		
		this.owner = owner.getUuid();
		this.members = new HashSet<>();
		this.subscribers = new HashSet<>();
		
		this.removedMeters = new ArrayList<>();
		this.meterUpdates = new LinkedHashMap<>();
		
		this.isPrivate = false;
		this.idle = false;
		this.idleTime = 0L;
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
	protected void meterAdded(Meter meter) {
		meterUpdates.putIfAbsent(meter.getId(), meter.getProperties());
	}
	
	@Override
	protected void meterRemoved(Meter meter) {
		removedMeters.add(meter.getId());
		meterUpdates.remove(meter.getId());
	}
	
	@Override
	protected void meterUpdated(Meter meter) {
		meterUpdates.putIfAbsent(meter.getId(), meter.getProperties());
	}
	
	@Override
	public ServerLogManager getLogManager() {
		return logManager;
	}
	
	public Multimeter getMultimeter() {
		return multimeter;
	}
	
	public boolean addMeter(MeterProperties properties) {
		return addMeter(new Meter(properties));
	}
	
	public boolean removeMeter(long id) {
		return hasMeter(id) && removeMeter(getMeter(id));
	}
	
	public boolean updateMeter(long id, MeterProperties newProperties) {
		return hasMeter(id) && updateMeter(getMeter(id), newProperties);
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
	
	public boolean isOwnedBy(ServerPlayerEntity player) {
		return isOwnedBy(player.getUuid());
	}
	
	public boolean isOwnedBy(UUID playerUUID) {
		return owner.equals(playerUUID);
	}
	
	public boolean hasMembers() {
		return !members.isEmpty();
	}
	
	public Set<UUID> getMembers() {
		return Collections.unmodifiableSet(members);
	}
	
	public boolean hasMember(ServerPlayerEntity player) {
		return hasMember(player.getUuid());
	}
	
	public boolean hasMember(UUID playerUUID) {
		return members.contains(playerUUID);
	}
	
	public void addMember(UUID playerUUID) {
		members.add(playerUUID);
	}
	
	public void removeMember(UUID playerUUID) {
		members.remove(playerUUID);
	}
	
	public void clearMembers() {
		members.clear();
	}
	
	public boolean hasSubscribers() {
		return !subscribers.isEmpty();
	}
	
	public Set<UUID> getSubscribers() {
		return Collections.unmodifiableSet(subscribers);
	}
	
	public boolean hasSubscriber(ServerPlayerEntity player) {
		return hasSubscriber(player.getUuid());
	}
	
	public boolean hasSubscriber(UUID playerUUID) {
		return subscribers.contains(playerUUID);
	}
	
	public void addSubscriber(UUID playerUUID) {
		subscribers.add(playerUUID);
	}
	
	public void removeSubscriber(UUID playerUUID) {
		subscribers.remove(playerUUID);
	}
	
	public boolean isPrivate() {
		return isPrivate;
	}
	
	public void setPrivate(boolean isPrivate) {
		this.isPrivate = isPrivate;
		
		if (isPrivate) {
			for (UUID playerUUID : subscribers) {
				if (playerUUID != owner) {
					addMember(playerUUID);
				}
			}
		}
	}
	
	public boolean isIdle() {
		return idle;
	}
	
	public long getIdleTime() {
		return idleTime;
	}
	
	public void updateIdleState() {
		boolean wasIdle = idle;
		idle = !hasMeters() && !hasSubscribers();
		
		if (wasIdle && !idle) {
			idleTime = 0L;
		}
	}
	
	public void tick() {
		if (idle) {
			idleTime++;
		}
		
		logManager.tick();
	}
	
	public void flushUpdates() {
		if (removedMeters.isEmpty() && meterUpdates.isEmpty()) {
			return;
		}
		
		MeterUpdatesPacket packet = new MeterUpdatesPacket(removedMeters, meterUpdates);
		multimeter.getMultimeterServer().getPacketHandler().sendToSubscribers(packet, this);
		
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
