package redstone.multimeter.server.meter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Supplier;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;

import redstone.multimeter.common.DimPos;
import redstone.multimeter.common.meter.Meter;
import redstone.multimeter.common.meter.MeterGroup;
import redstone.multimeter.common.meter.MeterProperties;
import redstone.multimeter.common.meter.MeterProperties.MutableMeterProperties;
import redstone.multimeter.common.meter.event.EventType;
import redstone.multimeter.common.meter.event.MeterEvent;
import redstone.multimeter.common.network.packets.MeterUpdatesPacket;
import redstone.multimeter.server.Multimeter;
import redstone.multimeter.server.meter.event.MeterEventPredicate;
import redstone.multimeter.server.meter.log.ServerLogManager;

public class ServerMeterGroup extends MeterGroup {

	private final Multimeter multimeter;
	private final ServerLogManager logManager;

	private final UUID owner;
	private final Set<UUID> members;
	private final Set<UUID> subscribers;

	private final List<Long> removedMeters;
	private final Map<Long, MeterProperties> meterUpdates;
	private boolean meterIndicesChanged;

	private boolean isPrivate;
	private boolean idle;
	private long idleTime;

	public ServerMeterGroup(Multimeter multimeter, String name, ServerPlayer owner) {
		super(name);

		this.multimeter = multimeter;
		this.logManager = new ServerLogManager(this);

		this.owner = owner.getUUID();
		this.members = new HashSet<>();
		this.subscribers = new HashSet<>();

		this.removedMeters = new ArrayList<>();
		this.meterUpdates = new LinkedHashMap<>();

		this.isPrivate = false;
		this.idle = true;
		this.idleTime = 0L;
	}

	@Override
	public void clear() {
		super.clear();

		removedMeters.clear();
		meterUpdates.clear();
	}

	@Override
	protected void moveMeter(Meter meter, DimPos newPos) {
		if (hasMeterAt(newPos)) {
			return;
		}

		Level level = multimeter.getServer().getLevel(newPos);

		if (level == null) {
			return;
		}

		super.moveMeter(meter, newPos);
	}

	@Override
	protected void meterAdded(Meter meter) {
		meterUpdates.put(meter.getId(), meter.getProperties());
	}

	@Override
	protected void meterRemoved(Meter meter) {
		removedMeters.add(meter.getId());
		meterUpdates.remove(meter.getId());
	}

	@Override
	protected void meterUpdated(Meter meter) {
		meterUpdates.put(meter.getId(), meter.getProperties());
	}

	@Override
	protected void indexChanged(Meter meter) {
		meterIndicesChanged = true;
	}

	@Override
	public ServerLogManager getLogManager() {
		return logManager;
	}

	public Multimeter getMultimeter() {
		return multimeter;
	}

	public boolean addMeter(MutableMeterProperties properties) {
		return addMeter(new Meter(properties));
	}

	public boolean removeMeter(long id) {
		return hasMeter(id) && removeMeter(getMeter(id));
	}

	public boolean updateMeter(long id, MeterProperties newProperties) {
		return hasMeter(id) && updateMeter(getMeter(id), newProperties);
	}

	public void tryMoveMeter(DimPos pos, Direction dir) {
		if (!hasMeterAt(pos)) {
			return;
		}

		Meter meter = getMeterAt(pos);

		if (!meter.isMovable()) {
			return;
		}

		moveMeter(meter, pos.relative(dir));
	}

	public boolean setMeterIndex(long id, int index) {
		return hasMeter(id) && setIndex(getMeter(id), index);
	}

	public boolean isPastMeterLimit() {
		int limit = multimeter.options.meter_group.meter_limit;
		return limit >= 0 && getMeters().size() >= limit;
	}

	public UUID getOwner() {
		return owner;
	}

	public boolean isOwnedBy(ServerPlayer player) {
		return isOwnedBy(player.getUUID());
	}

	public boolean isOwnedBy(UUID playerUuid) {
		return owner.equals(playerUuid);
	}

	public boolean hasMembers() {
		return !members.isEmpty();
	}

	public Collection<UUID> getMembers() {
		return Collections.unmodifiableCollection(members);
	}

	public boolean hasMember(ServerPlayer player) {
		return hasMember(player.getUUID());
	}

	public boolean hasMember(UUID playerUuid) {
		return members.contains(playerUuid);
	}

	public void addMember(UUID playerUuid) {
		members.add(playerUuid);
	}

	public void removeMember(UUID playerUuid) {
		members.remove(playerUuid);
	}

	public void clearMembers() {
		members.clear();
	}

	public boolean hasSubscribers() {
		return !subscribers.isEmpty();
	}

	public Collection<UUID> getSubscribers() {
		return Collections.unmodifiableCollection(subscribers);
	}

	public boolean hasSubscriber(ServerPlayer player) {
		return hasSubscriber(player.getUUID());
	}

	public boolean hasSubscriber(UUID playerUuid) {
		return subscribers.contains(playerUuid);
	}

	public void addSubscriber(UUID playerUuid) {
		subscribers.add(playerUuid);
	}

	public void removeSubscriber(UUID playerUuid) {
		subscribers.remove(playerUuid);
	}

	public boolean isPrivate() {
		return isPrivate;
	}

	public void setPrivate(boolean isPrivate) {
		this.isPrivate = isPrivate;

		if (isPrivate) {
			for (UUID playerUuid : subscribers) {
				if (playerUuid != owner) {
					addMember(playerUuid);
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

	public boolean updateIdleState() {
		boolean wasIdle = idle;
		idle = !hasSubscribers();

		if (wasIdle && !idle) {
			idleTime = 0L;
		}

		return wasIdle != idle;
	}

	public boolean isPastIdleTimeLimit() {
		return idle && multimeter.options.meter_group.max_idle_time >= 0 && idleTime > multimeter.options.meter_group.max_idle_time;
	}

	public void tick() {
		if (idle) {
			idleTime++;
		}
	}

	public void broadcastUpdates() {
		if (removedMeters.isEmpty() && meterUpdates.isEmpty() && !meterIndicesChanged) {
			return;
		}

		List<Long> meters = new LinkedList<>();

		if (meterIndicesChanged) {
			for (Meter meter : getMeters()) {
				meters.add(meter.getId());
			}
		}

		MeterUpdatesPacket packet = new MeterUpdatesPacket(removedMeters, meterUpdates, meters);
		multimeter.getServer().getPlayerList().send(packet, this);

		removedMeters.clear();
		meterUpdates.clear();

		meterIndicesChanged = false;
	}

	public void tryLogEvent(Level level, BlockPos blockPos, EventType type, Supplier<Integer> data, MeterEventPredicate predicate) {
		DimPos pos = new DimPos(level, blockPos);
		Meter meter = getMeterAt(pos);

		if (meter != null && meter.isMetering(type)) {
			MeterEvent event = new MeterEvent(type, data.get());

			if (predicate.test(this, meter, event)) {
				logManager.logEvent(level, meter, event);
			}
		}
	}
}
