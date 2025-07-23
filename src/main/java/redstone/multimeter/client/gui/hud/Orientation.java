package redstone.multimeter.client.gui.hud;

import redstone.multimeter.client.option.Cyclable;

public class Orientation {

	public static enum X implements Cyclable<X> {

		LEFT_TO_RIGHT("leftToRight", "LEFT_TO_RIGHT"),
		RIGHT_TO_LEFT("rightToLeft", "RIGHT_TO_LEFT");

		private final String key;
		// used for parsing values from before RSMM 1.16
		private final String legacyKey;

		private X(String key, String legacyKey) {
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

	public static enum Y implements Cyclable<Y> {

		TOP_TO_BOTTOM("topToBottom", "TOP_TO_BOTTOM"),
		BOTTOM_TO_TOP("bottomToTop", "BOTTOM_TO_TOP");

		private final String key;
		// used for parsing values from before RSMM 1.16
		private final String legacyKey;

		private Y(String key, String legacyKey) {
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
}
