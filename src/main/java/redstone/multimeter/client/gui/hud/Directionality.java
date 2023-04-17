package redstone.multimeter.client.gui.hud;

import redstone.multimeter.client.option.Cyclable;

public class Directionality {

	public static enum X implements Cyclable<X> {

		LEFT_TO_RIGHT("Left-to-Right"),
		RIGHT_TO_LEFT("Right-to-Left");

		private final String name;

		private X(String name) {
			this.name = name;
		}

		@Override
		public String getName() {
			return name;
		}
	}

	public static enum Y implements Cyclable<Y> {

		TOP_TO_BOTTOM("Top-to-Bottom"),
		BOTTOM_TO_TOP("Bottom-to-Top");

		private final String name;

		private Y(String name) {
			this.name = name;
		}

		@Override
		public String getName() {
			return name;
		}
	}
}
