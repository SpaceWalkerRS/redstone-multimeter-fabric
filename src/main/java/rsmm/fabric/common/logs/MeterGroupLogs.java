package rsmm.fabric.common.logs;

import java.util.LinkedList;
import java.util.List;

public class MeterGroupLogs {
	
	private final List<LogEntry> logs;
	
	public MeterGroupLogs() {
		this.logs = new LinkedList<>();
	}
	
	public void clear() {
		logs.clear();
	}
}
