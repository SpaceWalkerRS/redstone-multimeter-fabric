package redstone.multimeter.client.meter.log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.PriorityQueue;
import java.util.Queue;

import net.minecraft.client.gui.screen.Screen;

import redstone.multimeter.RedstoneMultimeterMod;
import redstone.multimeter.client.option.Options;
import redstone.multimeter.common.TickPhase;
import redstone.multimeter.common.meter.Meter;
import redstone.multimeter.common.meter.event.EventType;
import redstone.multimeter.common.meter.log.EventLog;
import redstone.multimeter.common.meter.log.MeterLogs;

public class LogPrinter {
	
	private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss");
	
	private final ClientLogManager logManager;
	private final File folder;
	private final Queue<MeterEventLog> printQueue;
	
	private BufferedWriter writer;
	private long prevTick;
	
	public LogPrinter(ClientLogManager logManager) {
		this.logManager = logManager;
		this.folder = new File(RedstoneMultimeterMod.NAMESPACE +  "/logs/");
		this.printQueue = new PriorityQueue<>();
		
		this.writer = null;
		this.prevTick = -1;
	}
	
	public ClientLogManager getLogManager() {
		return logManager;
	}
	
	public boolean isPrinting() {
		return writer != null;
	}
	
	public void toggle() {
		if (isPrinting()) {
			stop();
		} else {
			start();
		}
	}
	
	public void start() {
		if (isPrinting()) {
			return;
		}
		
		try {
			File file = createLogFile();
			FileWriter fw = new FileWriter(file);
			
			writer = new BufferedWriter(fw);
			
			writer.write("Logs for meter group \'" + logManager.getMeterGroup().getName() + "\'");
			writer.newLine();
			writer.write("Logs are added in chronological order");
			writer.newLine();
			writer.write("-------------------------------------");
			writer.newLine();
			
			if (Options.LogPrinter.PRINT_OLD_LOGS.get() || Screen.hasShiftDown()) {
				printLogs();
			} else {
				prevTick = logManager.getLastTick();
			}
		} catch (IOException e) {
			stop();
		}
	}
	
	public void stop() {
		prevTick = -1;
		
		if (!isPrinting()) {
			return;
		}
		
		try {
			writer.close();
		} catch (IOException e) {
			
		} finally {
			writer = null;
		}
	}
	
	private File createLogFile() throws IOException {
		if (!folder.exists()) {
			folder.mkdirs();
		}
		
		String date = DATE_FORMAT.format(new Date());
		File file = new File(folder, date + ".txt");
		
		int i = 1;
		
		while (file.exists()) {
			String fileName = String.format("%s (%d).txt", date, i++);
			file = new File(folder, fileName);
		}
		
		file.createNewFile();
		
		return file;
	}
	
	public void printLogs() {
		if (!isPrinting()) {
			return;
		}
		
		long lastTick = logManager.getLastTick();
		
		for (Meter meter : logManager.getMeterGroup().getMeters()) {
			MeterLogs logs = meter.getLogs();
			
			for (EventType type : EventType.ALL) {
				int index = logs.getLastLogBefore(type, prevTick + 1) + 1;
				EventLog log = logs.getLog(type, index);
				
				if (log == null) {
					continue;
				}
				
				while (!log.isAfter(lastTick)) {
					printQueue.add(new MeterEventLog(meter, log));
					log = logs.getLog(type, ++index);
					
					if (log == null) {
						break;
					}
				}
			}
		}
		
		prevTick = lastTick;
		
		print();
	}
	
	private void print() {
		long tick = -1;
		TickPhase phase = null;
		
		while (!printQueue.isEmpty()) {
			MeterEventLog meterEventLog = printQueue.poll();
			
			try {
				if (meterEventLog.log.getTick() != tick) {
					tick = meterEventLog.log.getTick();
					phase = null;
					
					writer.write("" + tick);
					writer.newLine();
				}
				if (meterEventLog.log.getTickPhase() != phase) {
					phase = meterEventLog.log.getTickPhase();
					
					writer.write("    " + phase.getName());
					writer.newLine();
				}
				
				writer.write("        " + meterEventLog.toString());
				writer.newLine();
			} catch (IOException e) {
				
			}
		}
	}
	
	public void onNewMeterGroup() {
		if (isPrinting()) {
			stop();
			start();
		}
	}
	
	private class MeterEventLog implements Comparable<MeterEventLog> {
		
		private final Meter meter;
		private final EventLog log;
		
		public MeterEventLog(Meter meter, EventLog log) {
			this.meter = meter;
			this.log = log;
		}
		
		@Override
		public String toString() {
			return String.format("%d - (%s) %s", log.getSubtick(), meter.getName(), log.getEvent().toString());
		}
		
		@Override
		public int compareTo(MeterEventLog o) {
			if (log.isBefore(o.log)) {
				return -1;
			}
			if (log.isAfter(o.log)) {
				return 1;
			}
			
			return 0;
		}
	}
}
