package redstone.multimeter.common.meter;

import java.awt.Color;

import redstone.multimeter.client.option.Cyclable;
import redstone.multimeter.util.ColorUtils;

public enum ColorPicker implements Cyclable<ColorPicker> {

	RANDOM("random") {

		private int index;

		@Override
		public int next() {
			float hue = ((index * 11) % 8 + (index / 8) / 2.0F) / 8.0F;
			index = (index + 1) % 16;

			return ColorUtils.setAlpha(Color.HSBtoRGB(hue, 0.7F, 1.0F), 0xFF);
		}
	},
	RAINBOW("rainbow") {

		private int index;

		@Override
		public int next() {
			float hue = index / 32.0F;
			index = (index + 1) % 32;

			return ColorUtils.setAlpha(Color.HSBtoRGB(hue, 0.7F, 1.0F), 0xFF);
		}
	};

	private ColorPicker(String name) {
		this.name = name;
	}

	private final String name;

	@Override
	public String getName() {
		return name;
	}

	public abstract int next();

}
