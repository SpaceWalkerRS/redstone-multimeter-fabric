package redstone.multimeter.common.meter.log;

import net.minecraft.nbt.CompoundTag;

import redstone.multimeter.client.gui.text.Texts;
import redstone.multimeter.client.gui.tooltip.Tooltip;
import redstone.multimeter.client.gui.tooltip.Tooltips;
import redstone.multimeter.common.TickPhase;
import redstone.multimeter.common.meter.event.MeterEvent;

public class EventLog {

	private long tick;
	private int subtick;
	private TickPhase tickPhase;
	private MeterEvent event;

	private EventLog() {
	}

	public EventLog(long tick, int subtick, TickPhase tickPhase, MeterEvent event) {
		this.tick = tick;
		this.subtick = subtick;
		this.tickPhase = tickPhase;
		this.event = event;
	}

	public long getTick() {
		return tick;
	}

	public int getSubtick() {
		return subtick;
	}

	public boolean isAt(long tick) {
		return this.tick == tick;
	}

	public boolean isAt(long tick, int subtick) {
		return this.tick == tick && this.subtick == subtick;
	}

	public boolean isBefore(long tick) {
		return this.tick < tick;
	}

	public boolean isBefore(long tick, int subtick) {
		if (this.tick == tick) {
			return this.subtick < subtick;
		}

		return this.tick < tick;
	}

	public boolean isBefore(EventLog event) {
		return isBefore(event.getTick(), event.getSubtick());
	}

	public boolean isAfter(long tick) {
		return this.tick > tick;
	}

	public boolean isAfter(long tick, int subtick) {
		if (this.tick == tick) {
			return this.subtick > subtick;
		}

		return this.tick > tick;
	}

	public boolean isAfter(EventLog event) {
		return isAfter(event.getTick(), event.getSubtick());
	}

	public TickPhase getTickPhase() {
		return tickPhase;
	}

	public MeterEvent getEvent() {
		return event;
	}

	public Tooltip getTooltip() {
		return Tooltips.builder()
			.lines(event::buildTooltip)
			.line(Texts.keyValue("tick", tick))
			.line(Texts.keyValue("subtick", subtick))
			.lines(tickPhase::buildTooltip)
			.build();
	}

	public CompoundTag toNbt() {
		CompoundTag nbt = new CompoundTag();

		nbt.put("meter event", event.toNbt());
		nbt.putLong("tick", tick);
		nbt.putInt("subtick", subtick);
		nbt.put("tick phase", tickPhase.toNbt());

		return nbt;
	}

	public static EventLog fromNbt(CompoundTag nbt) {
		EventLog log = new EventLog();

		log.event = MeterEvent.fromNbt(nbt.getCompound("meter event").get());
		log.tick = nbt.getLong("tick").get();
		log.subtick = nbt.getInt("subtick").get();
		log.tickPhase = TickPhase.fromNbt(nbt.get("tick phase"));

		return log;
	}
}
