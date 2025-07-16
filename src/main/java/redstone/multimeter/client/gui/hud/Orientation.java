package redstone.multimeter.client.gui.hud;

import redstone.multimeter.client.option.Cyclable;

public class Orientation {

	public static enum X implements Cyclable<X> {

		LEFT_TO_RIGHT("leftToRight"),
		RIGHT_TO_LEFT("rightToLeft");

		private final String key;

		private X(String key) {
			this.key = key;
		}

		@Override
		public String key() {
			return key;
		}
	}

	public static enum Y implements Cyclable<Y> {

		TOP_TO_BOTTOM("topToBottom"),
		BOTTOM_TO_TOP("bottomToTop");

		private final String key;

		private Y(String key) {
			this.key = key;
		}

		@Override
		public String key() {
			return key;
		}
	}
}
