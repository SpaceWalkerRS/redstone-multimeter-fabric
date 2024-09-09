package redstone.multimeter.client.render;

import redstone.multimeter.client.option.Cyclable;

public enum MeterNameMode implements Cyclable<MeterNameMode> {

	NEVER("Never"),
	ALWAYS("Always"),
	IN_FOCUS_MODE("In Focus Mode"),
	WHEN_PREVIEWING("When Previewing");

	private final String name;

	private MeterNameMode(String name) {
		this.name = name;
	}

	@Override
	public String getName() {
		return name;
	}
}
