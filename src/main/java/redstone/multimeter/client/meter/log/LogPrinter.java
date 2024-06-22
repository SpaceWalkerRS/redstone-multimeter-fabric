package redstone.multimeter.client.meter.log;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.PriorityQueue;
import java.util.Queue;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;

import redstone.multimeter.RedstoneMultimeterMod;
import redstone.multimeter.client.MultimeterClient;
import redstone.multimeter.client.option.Options;
import redstone.multimeter.common.TickPhase;
import redstone.multimeter.common.meter.Meter;
import redstone.multimeter.common.meter.event.EventType;
import redstone.multimeter.common.meter.log.EventLog;
import redstone.multimeter.common.meter.log.MeterLogs;

public class LogPrinter {

	private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss");

	private final ClientLogManager logManager;
	private final MultimeterClient client;
	private final Path folder;
	private final Queue<MeterEventLog> printQueue;

	private BufferedWriter writer;
	private long firstTick;
	private long prevTick;

	public LogPrinter(ClientLogManager logManager) {
		this.logManager = logManager;
		this.client = this.logManager.getMeterGroup().getMultimeterClient();
		Minecraft minecraft = this.client.getMinecraft();
		this.folder = minecraft.runDir.toPath().resolve(RedstoneMultimeterMod.NAMESPACE + "/logs/");
		this.printQueue = new PriorityQueue<>();

		this.writer = null;
		this.firstTick = -1;
		this.prevTick = -1;
	}

	public ClientLogManager getLogManager() {
		return logManager;
	}

	private long getGameTime() {
		return logManager.getMeterGroup().getMultimeterClient().getPrevGameTime();
	}

	public boolean isPrinting() {
		return writer != null;
	}

	public void toggle() {
		if (isPrinting()) {
			stop(true);
		} else {
			start(true);
		}
	}

	public void start(boolean notifyPlayer) {
		if (isPrinting()) {
			return;
		}

		try {
			Path file = createLogFile();
			BufferedWriter bw = Files.newBufferedWriter(file);

			writer = new BufferedWriter(bw);
			firstTick = getGameTime();

			writer.write("Logs for meter group \'" + logManager.getMeterGroup().getName() + "\'");
			writer.newLine();
			writer.write("Logs are added in chronological order");
			writer.newLine();
			writer.write("-------------------------------------");
			writer.newLine();

			if (Options.LogPrinter.PRINT_OLD_LOGS.get() || Screen.isShiftDown()) {
				printLogs();
			} else {
				prevTick = firstTick;
			}

			if (notifyPlayer) {
				Text message = new LiteralText("Started printing logs to file...");
				client.sendMessage(message);
			}

			client.getHud().onTogglePrinter();
		} catch (IOException e) {
			stop(notifyPlayer);
		}
	}

	public void stop(boolean notifyPlayer) {
		firstTick = -1;
		prevTick = -1;

		if (!isPrinting()) {
			return;
		}

		try {
			writer.close();
		} catch (IOException e) {

		} finally {
			writer = null;

			if (notifyPlayer) {
				Text message = new LiteralText("Stopped printing logs to file");
				client.sendMessage(message);
			}

			client.getHud().onTogglePrinter();
		}
	}

	private Path createLogFile() throws IOException {
		if (!Files.exists(folder)) {
			Files.createDirectories(folder);
		}

		String date = DATE_FORMAT.format(new Date());
		Path file = folder.resolve(date + ".txt");

		int i = 1;

		while (Files.exists(file)) {
			String fileName = String.format("%s (%d).txt", date, i++);
			file = folder.resolve(fileName);
		}

		return file;
	}

	public void tick() {
		if (isPrinting()) {
			int limit = Options.LogPrinter.MAX_RUNTIME.get();
			long runtime = getGameTime() - firstTick;

			if (limit >= 0 && runtime > limit) {
				Text message = new LiteralText("Printer exceeded maximum runtime!");
				client.sendMessage(message);

				stop(true);
			}
		}
	}

	public void printLogs() {
		if (!isPrinting()) {
			return;
		}

		long lastTick = getGameTime();

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

		try {
			while (!printQueue.isEmpty()) {
				MeterEventLog meterEventLog = printQueue.poll();

				if (meterEventLog.log.getTick() != tick) {
					tick = meterEventLog.log.getTick();
					phase = null;

					writer.write("" + tick);
					writer.newLine();
				}
				if (!meterEventLog.log.getTickPhase().equals(phase)) {
					phase = meterEventLog.log.getTickPhase();

					writer.write("    " + phase.toString());
					writer.newLine();
				}

				writer.write("        " + meterEventLog.toString());
				writer.newLine();
			}
		} catch (IOException e) {
			Text message = new LiteralText("Printer encountered issues!");
			client.sendMessage(message);

			stop(true);
		}
	}

	public void onNewMeterGroup() {
		if (isPrinting()) {
			stop(false);
			start(false);
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
