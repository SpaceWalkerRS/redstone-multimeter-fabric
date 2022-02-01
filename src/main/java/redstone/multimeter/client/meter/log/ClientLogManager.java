package redstone.multimeter.client.meter.log;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import redstone.multimeter.client.meter.ClientMeterGroup;
import redstone.multimeter.common.meter.Meter;
import redstone.multimeter.common.meter.log.LogManager;
import redstone.multimeter.util.NbtUtils;

public class ClientLogManager extends LogManager {
	
	/** The maximum age relative to the selected tick */
	private static final long AGE_CUTOFF = 10000L;
	/** The maximum age relative to the current server tick */
	private static final long MAX_LOG_AGE = 1000000L;
	
	private final ClientMeterGroup meterGroup;
	/** The number of logged events in any tick */
	private final Map<Long, Integer> subticks;
	private final LogPrinter printer;
	
	public ClientLogManager(ClientMeterGroup meterGroup) {
		this.meterGroup = meterGroup;
		this.subticks = new LinkedHashMap<>();
		this.printer = new LogPrinter(this);
	}
	
	@Override
	protected ClientMeterGroup getMeterGroup() {
		return meterGroup;
	}
	
	@Override
	protected long getLastTick() {
		return meterGroup.getMultimeterClient().getPrevServerTime();
	}
	
	@Override
	public void clearLogs() {
		super.clearLogs();
		subticks.clear();
	}
	
	public LogPrinter getPrinter() {
		return printer;
	}
	
	public int getSubTickCount(long tick) {
		return subticks.getOrDefault(tick, 0);
	}
	
	public void tick() {
		clearOldLogs();
		printer.tick();
	}
	
	private void clearOldLogs() {
		long selectedTickCutoff = meterGroup.getMultimeterClient().getHUD().getSelectedTick() - AGE_CUTOFF;
		long serverTickCutoff = getLastTick() - MAX_LOG_AGE;
		long cutoff = (selectedTickCutoff > serverTickCutoff) ? selectedTickCutoff : serverTickCutoff;
		
		Iterator<Long> it = subticks.keySet().iterator();
		
		while (it.hasNext()) {
			if (it.next() > cutoff) {
				break;
			}
			
			it.remove();
		}
		
		for (Meter meter : meterGroup.getMeters()) {
			meter.getLogs().clearOldLogs(cutoff);
		}
	}
	
	/**
	 * Log all events from the past server tick
	 */
	public void updateMeterLogs(NBTTagCompound data) {
		int subtickCount = data.getInteger("subticks");
		subticks.put(getLastTick(), subtickCount);
		
		NBTTagList list = data.getTagList("logs", NbtUtils.TYPE_COMPOUND);
		
		for (int index = 0; index < list.tagCount(); index++) {
			NBTTagCompound nbt = list.getCompoundTagAt(index);
			
			long id = nbt.getLong("id");
			Meter meter = meterGroup.getMeter(id);
			
			if (meter != null) {
				NBTTagCompound logs = nbt.getCompoundTag("logs");
				boolean powered = nbt.getBoolean("powered");
				boolean active = nbt.getBoolean("active");
				
				meter.getLogs().updateFromNbt(logs);
				meter.setPowered(powered);
				meter.setActive(active);
			}
		}
		
		printer.printLogs();
	}
}
