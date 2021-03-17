package rsmm.fabric.common.log;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import rsmm.fabric.common.log.entry.AbstractLogEntry;
import rsmm.fabric.common.log.entry.MeterActiveLogEntry;
import rsmm.fabric.common.log.entry.MeterPoweredLogEntry;

public class MeterGroupLogs {
	
	private final Map<Long, List<AbstractLogEntry>> logs;
	
	private long currentTick;
	
	public MeterGroupLogs() {
		this.logs = new HashMap<>();
	}
	
	public void clear() {
		logs.clear();
	}
	
	public void tick() {
		currentTick++;
		
		nextTick();
	}
	
	public void syncTime(long currentTick) {
		this.currentTick = currentTick;
		
		nextTick();
	}
	
	private void nextTick() {
		logs.put(currentTick, new LinkedList<>());
	}
	
	private void addLogEntry(AbstractLogEntry logEntry) {
		logs.get(currentTick).add(logEntry);
	}
	
	public void meterPoweredChanged(int index, boolean powered) {
		addLogEntry(new MeterPoweredLogEntry(index, powered));
	}
	
	public void meterActiveChanged(int index, boolean active) {
		addLogEntry(new MeterActiveLogEntry(index, active));
	}
}
