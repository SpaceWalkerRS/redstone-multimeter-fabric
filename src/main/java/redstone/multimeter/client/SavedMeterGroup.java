package redstone.multimeter.client;

import java.util.ArrayList;
import java.util.List;

import redstone.multimeter.common.meter.MeterProperties;

public class SavedMeterGroup {

	private final String name;
	private final List<MeterProperties> meters;

	public SavedMeterGroup(String name, List<MeterProperties> meters) {
		this.name = name;
		this.meters = new ArrayList<>(meters);
	}

	public String getName() {
		return name;
	}

	public List<MeterProperties> getMeters() {
		return meters;
	}
}
