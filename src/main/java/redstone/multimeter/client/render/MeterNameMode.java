package redstone.multimeter.client.render;

import redstone.multimeter.client.option.Cyclable;

public enum MeterNameMode implements Cyclable<MeterNameMode> {

	NEVER("never"),
	ALWAYS("always"),
	IN_FOCUS_MODE("inFocusMode"),
	WHEN_PREVIEWING("whenPreviewingMeterGroup");

	private final String key;

	private MeterNameMode(String key) {
		this.key = key;
	}

	@Override
	public String key() {
		return key;
	}
}
