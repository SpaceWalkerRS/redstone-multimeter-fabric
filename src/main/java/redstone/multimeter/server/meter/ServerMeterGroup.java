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

import com.google.common.base.Supplier;

import net.minecraft.server.entity.living.player.ServerPlayerEntity;
import net.minecraft.world.World;

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
import redstone.multimeter.util.Direction;

public class ServerMeterGroup extends MeterGroup {

	private final Multimeter multimeter;
	private final ServerLogManager logManager;

	private final String owner;
	private final Set<String> members;
	private final Set<String> subscribers;

	private final List<Long> removedMeters;
	private final Map<Long, MeterProperties> meterUpdates;
	private boolean meterIndicesChanged;

	private boolean isPrivate;
	private boolean idle;
	private long idleTime;

	public ServerMeterGroup(Multimeter multimeter, String name, ServerPlayerEntity owner) {
		super(name);

		this.multimeter = multimeter;
		this.logManager = new ServerLogManager(this);

		this.owner = owner.getDisplayName();
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

		World world = multimeter.getServer().getWorld(newPos);

		if (world == null) {
			return;
		}

		super.moveMeter(meter, newPos);
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

		moveMeter(meter, pos.offset(dir));
	}

	public boolean setMeterIndex(long id, int index) {
		return hasMeter(id) && setIndex(getMeter(id), index);
	}

	public boolean isPastMeterLimit() {
		int limit = multimeter.options.meter_group.meter_limit;
		return limit >= 0 && getMeters().size() >= limit;
	}

	public String getOwner() {
		return owner;
	}

	public boolean isOwnedBy(ServerPlayerEntity player) {
		return isOwnedBy(player.getDisplayName());
	}

	public boolean isOwnedBy(String playerName) {
		return owner.equals(playerName);
	}

	public boolean hasMembers() {
		return !members.isEmpty();
	}

	public Collection<String> getMembers() {
		return Collections.unmodifiableCollection(members);
	}

	public boolean hasMember(ServerPlayerEntity player) {
		return hasMember(player.getDisplayName());
	}

	public boolean hasMember(String playerName) {
		return members.contains(playerName);
	}

	public void addMember(String playerName) {
		members.add(playerName);
	}

	public void removeMember(String playerName) {
		members.remove(playerName);
	}

	public void clearMembers() {
		members.clear();
	}

	public boolean hasSubscribers() {
		return !subscribers.isEmpty();
	}

	public Collection<String> getSubscribers() {
		return Collections.unmodifiableCollection(subscribers);
	}

	public boolean hasSubscriber(ServerPlayerEntity player) {
		return hasSubscriber(player.getDisplayName());
	}

	public boolean hasSubscriber(String playerName) {
		return subscribers.contains(playerName);
	}

	public void addSubscriber(String playerName) {
		subscribers.add(playerName);
	}

	public void removeSubscriber(String playerName) {
		subscribers.remove(playerName);
	}

	public boolean isPrivate() {
		return isPrivate;
	}

	public void setPrivate(boolean isPrivate) {
		this.isPrivate = isPrivate;

		if (isPrivate) {
			for (String playerName : subscribers) {
				if (playerName != owner) {
					addMember(playerName);
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

	public void tryLogEvent(World world, int x, int y, int z, EventType type, Supplier<Integer> data, MeterEventPredicate predicate) {
		DimPos pos = new DimPos(world, x, y, z);
		Meter meter = getMeterAt(pos);

		if (meter != null && meter.isMetering(type)) {
			MeterEvent event = new MeterEvent(type, data.get());

			if (predicate.test(this, meter, event)) {
				logManager.logEvent(world, meter, event);
			}
		}
	}
}
