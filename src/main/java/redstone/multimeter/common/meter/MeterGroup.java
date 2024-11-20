package redstone.multimeter.common.meter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;

import redstone.multimeter.common.DimPos;
import redstone.multimeter.common.meter.log.LogManager;

public abstract class MeterGroup {

	private final String name;
	private final List<Meter> meters;
	private final Map<Long, Integer> byId;
	private final Map<DimPos, Integer> byPos;

	protected MeterGroup(String name) {
		this.name = name;
		this.meters = new ArrayList<>();
		this.byId = new HashMap<>();
		this.byPos = new HashMap<>();
	}

	public static boolean isValidName(String name) {
		return !name.trim().isEmpty() && name.length() <= getMaxNameLength();
	}

	public static int getMaxNameLength() {
		return 64;
	}

	public String getName() {
		return name;
	}

	public void clear() {
		meters.clear();
		byId.clear();
		byPos.clear();
		getLogManager().clearLogs();
	}

	public boolean hasMeters() {
		return !meters.isEmpty();
	}

	public List<Meter> getMeters() {
		return Collections.unmodifiableList(meters);
	}

	public boolean hasMeter(long id) {
		return byId.containsKey(id);
	}

	public boolean hasMeterAt(DimPos pos) {
		return byPos.containsKey(pos);
	}

	public Meter getMeter(long id) {
		return fromIndex(byId.getOrDefault(id, -1));
	}

	public Meter getMeterAt(DimPos pos) {
		return fromIndex(byPos.getOrDefault(pos, -1));
	}

	private Meter fromIndex(int index) {
		return (index < 0 || index >= meters.size()) ? null : meters.get(index);
	}

	protected boolean addMeter(Meter meter) {
		// This check prevents meters from being added twice and
		// multiple meters from being added at the same position.
		if (byId.containsKey(meter.getId()) || byPos.containsKey(meter.getPos())) {
			return false;
		}

		byId.put(meter.getId(), meters.size());
		byPos.put(meter.getPos(), meters.size());
		meters.add(meter);

		meterAdded(meter);

		return true;
	}

	protected boolean removeMeter(Meter meter) {
		int index = byId.getOrDefault(meter.getId(), -1);

		if (index < 0 || index >= meters.size()) {
			return false;
		}

		meters.remove(index);
		byId.remove(meter.getId(), index);
		byPos.remove(meter.getPos(), index);

		for (; index < meters.size(); index++) {
			Meter m = meters.get(index);

			byId.compute(m.getId(), (id, prevIndex) -> prevIndex - 1);
			byPos.compute(m.getPos(), (pos, prevIndex) -> prevIndex - 1);
		}

		meterRemoved(meter);

		return true;
	}

	protected boolean updateMeter(Meter meter, MeterProperties newProperties) {
		meter.applyUpdate(properties -> {
			boolean changed = false;

			if (newProperties.getPos() != null) {
				moveMeter(meter, newProperties.getPos());
			}
			if (newProperties.getName() != null) {
				changed |= properties.setName(newProperties.getName());
			}
			if (newProperties.getColor() != null) {
				changed |= properties.setColor(newProperties.getColor());
			}
			if (newProperties.getMovable() != null) {
				changed |= properties.setMovable(newProperties.getMovable());
			}
			if (newProperties.getEventTypes() != null) {
				changed |= properties.setEventTypes(newProperties.getEventTypes());
			}

			if (changed) {
				meterUpdated(meter);
			}
		});

		return true;
	}

	protected void moveMeter(Meter meter, DimPos newPos) {
		long id = meter.getId();
		DimPos pos = meter.getPos();

		if (pos.equals(newPos)) {
			return;
		}

		int index = byId.getOrDefault(id, -1);

		if (index < 0 || index >= meters.size()) {
			return;
		}

		byPos.remove(pos, index);
		byPos.put(newPos, index);

		meter.applyUpdate(properties -> {
			if (properties.setPos(newPos)) {
				meterUpdated(meter);
			}
		});
	}

	protected boolean setIndex(Meter meter, int index) {
		int oldIndex = byId.getOrDefault(meter.getId(), -1);

		if (index < 0 || index >= meters.size() || oldIndex < 0) {
			return false;
		}

		meters.remove(oldIndex);
		meters.add(index, meter);

		int start = Math.min(oldIndex, index);
		int end = Math.max(oldIndex, index);

		for (index = start; index <= end; index++) {
			meter = meters.get(index);

			byId.put(meter.getId(), index);
			byPos.put(meter.getPos(), index);

			indexChanged(meter);
		}

		return true;
	}

	protected abstract void meterAdded(Meter meter);

	protected abstract void meterRemoved(Meter meter);

	protected abstract void meterUpdated(Meter meter);

	protected abstract void indexChanged(Meter meter);

	public abstract LogManager getLogManager();

	public NbtCompound toNbt() {
		NbtList list = new NbtList();

		for (Meter meter : meters) {
			list.add(meter.toNbt());
		}

		NbtCompound nbt = new NbtCompound();
		nbt.put("meters", list);

		return nbt;
	}

	public void updateFromNbt(NbtCompound nbt) {
		clear();

		NbtList list = nbt.getList("meters");

		for (int index = 0; index < list.size(); index++) {
			NbtCompound meterNbt = (NbtCompound)list.get(index);
			Meter meter = Meter.fromNbt(meterNbt);

			addMeter(meter);
		}
	}
}
