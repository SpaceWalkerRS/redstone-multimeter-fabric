package redstone.multimeter.client.gui.element.input;

public class KeyEvent {

	private final int keyCode;
	private final int scanCode;
	private final int modifiers;

	KeyEvent(int keyCode, int scanCode, int modifiers) {
		this.keyCode = keyCode;
		this.scanCode = scanCode;
		this.modifiers = modifiers;
	}

	public int keyCode() {
		return this.keyCode;
	}

	public int scanCode() {
		return this.scanCode;
	}

	public int modifiers() {
		return this.modifiers;
	}

	public static KeyEvent.Press press(int keyCode, int scanCode, int modifiers) {
		return new Press(keyCode, scanCode, modifiers);
	}

	public static KeyEvent.Release release(int keyCode, int scanCode, int modifiers) {
		return new Release(keyCode, scanCode, modifiers);
	}

	public static class Press extends KeyEvent {

		Press(int keyCode, int scanCode, int modifiers) {
			super(keyCode, scanCode, modifiers);
		}
	}

	public static class Release extends KeyEvent {

		Release(int keyCode, int scanCode, int modifiers) {
			super(keyCode, scanCode, modifiers);
		}
	}
}
