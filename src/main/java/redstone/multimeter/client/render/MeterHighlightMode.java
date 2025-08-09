package redstone.multimeter.client.render;

import redstone.multimeter.client.option.Cyclable;

public enum MeterHighlightMode implements Cyclable<MeterHighlightMode> {

	NEVER("never"),
	ALWAYS("always"),
	IN_FOCUS_MODE("inFocusMode"),
	IN_FOCUS("inFocus");

	private final String key;

	private MeterHighlightMode(String key) {
		this.key = key;
	}

	@Override
	public String key() {
		return this.key;
	}

	@Override
	public String legacyKey() {
		return null;
	}
}
