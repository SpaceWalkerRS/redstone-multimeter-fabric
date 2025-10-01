package redstone.multimeter.client.gui.element.input;

import org.lwjgl.glfw.GLFW;

public class MouseEvent {

	private final double mouseX;
	private final double mouseY;
	private final int button;

	MouseEvent(double mouseX, double mouseY, int button) {
		this.mouseX = mouseX;
		this.mouseY = mouseY;
		this.button = button;
	}

	public double mouseX() {
		return this.mouseX;
	}

	public double mouseY() {
		return this.mouseY;
	}

	public int button() {
		return this.button;
	}

	public boolean isLeftButton() {
		return this.button == GLFW.GLFW_MOUSE_BUTTON_LEFT;
	}

	public boolean isRightButton() {
		return this.button == GLFW.GLFW_MOUSE_BUTTON_RIGHT;
	}

	public static MouseEvent.Click click(double mouseX, double mouseY, int button, boolean doubleClick) {
		return new Click(mouseX, mouseY, button, doubleClick);
	}

	public static MouseEvent.Release release(double mouseX, double mouseY, int button) {
		return new Release(mouseX, mouseY, button);
	}

	public static MouseEvent.Drag drag(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
		return new Drag(mouseX, mouseY, button, deltaX, deltaY);
	}

	public static MouseEvent.Scroll scroll(double mouseX, double mouseY, double scrollX, double scrollY) {
		return new Scroll(mouseX, mouseY, scrollX, scrollY);
	}

	public static class Click extends MouseEvent {

		private final boolean doubleClick;

		Click(double mouseX, double mouseY, int button, boolean doubleClick) {
			super(mouseX, mouseY, button);

			this.doubleClick = doubleClick;
		}

		public boolean doubleClick() {
			return this.doubleClick;
		}
	}

	public static class Release extends MouseEvent {

		Release(double mouseX, double mouseY, int button) {
			super(mouseX, mouseY, button);
		}
	}

	public static class Drag extends MouseEvent {

		private final double deltaX;
		private final double deltaY;

		Drag(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
			super(mouseX, mouseY, button);

			this.deltaX = deltaX;
			this.deltaY = deltaY;
		}

		public double deltaX() {
			return this.deltaX;
		}

		public double deltaY() {
			return this.deltaY;
		}
	}

	public static class Scroll extends MouseEvent {

		private final double scrollX;
		private final double scrollY;

		Scroll(double mouseX, double mouseY, double scrollX, double scrollY) {
			super(mouseX, mouseY, -1);

			this.scrollX = scrollX;
			this.scrollY = scrollY;
		}

		@Override
		public int button() {
			throw new UnsupportedOperationException("mouse scroll event has no assigned button id!");
		}

		public double scrollX() {
			return this.scrollX;
		}

		public double scrollY() {
			return this.scrollY;
		}
	}
}
