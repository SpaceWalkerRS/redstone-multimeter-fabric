package redstone.multimeter.common.meter;

import java.awt.Color;

import redstone.multimeter.client.option.Cyclable;

public enum ColorPicker implements Cyclable<ColorPicker> {

	RANDOM("random", "RANDOM") {

		private int index;

		@Override
		public int next() {
			float hue = ((index * 11) % 8 + (index / 8) / 2.0F) / 8.0F;
			index = (index + 1) % 16;

			return Color.HSBtoRGB(hue, 0.7F, 1.0F);
		}
	},
	RAINBOW("rainbow", "RAINBOW") {

		private int index;

		@Override
		public int next() {
			float hue = index / 32.0F;
			index = (index + 1) % 32;

			return Color.HSBtoRGB(hue, 0.7F, 1.0F);
		}
	};

	private final String key;
	// used for parsing values from before RSMM 1.16
	private final String legacyKey;

	private ColorPicker(String key, String legacyKey) {
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

	public abstract int next();

}
