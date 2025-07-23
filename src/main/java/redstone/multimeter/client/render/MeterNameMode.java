package redstone.multimeter.client.render;

import redstone.multimeter.client.option.Cyclable;

public enum MeterNameMode implements Cyclable<MeterNameMode> {

	NEVER("never", "NEVER"),
	ALWAYS("always", "ALWAYS"),
	IN_FOCUS_MODE("inFocusMode", "IN_FOCUS_MODE"),
	WHEN_PREVIEWING("whenPreviewingMeterGroup", "WHEN_PREVIEWING");

	private final String key;
	// used for parsing values from before RSMM 1.16
	private final String legacyKey;

	private MeterNameMode(String key, String legacyKey) {
		this.key = key;
		this.legacyKey = legacyKey;
	}

	@Override
	public String key() {
		return this.key;
	}

	@Override
	public String legacyKey() {
		return this.legacyKey;
	}
}
