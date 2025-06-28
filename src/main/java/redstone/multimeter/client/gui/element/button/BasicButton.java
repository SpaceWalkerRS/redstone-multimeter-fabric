package redstone.multimeter.client.gui.element.button;

import java.util.function.Supplier;

import redstone.multimeter.client.gui.element.action.MousePress;
import redstone.multimeter.client.gui.text.Text;
import redstone.multimeter.client.gui.tooltip.Tooltip;

public class BasicButton extends AbstractButton {

	private final MousePress<BasicButton> onPress;

	public BasicButton(int x, int y, Supplier<Text> message, Supplier<Tooltip> tooltip, MousePress<BasicButton> onPress) {
		this(x, y, DEFAULT_WIDTH, DEFAULT_HEIGHT, message, tooltip, onPress);
	}

	public BasicButton(int x, int y, int width, int height, Supplier<Text> message, Supplier<Tooltip> tooltip, MousePress<BasicButton> onPress) {
		super(x, y, width, height, message, tooltip);

		this.onPress = onPress;
	}

	@Override
	public void mouseMove(double mouseX, double mouseY) {
	}

	@Override
	public boolean mouseClick(double mouseX, double mouseY, int button) {
		boolean consumed = super.mouseClick(mouseX, mouseY, button);

		if (!consumed && isActive() && button == MOUSE_BUTTON_LEFT && onPress.accept(this)) {
			Button.playClickSound();
			consumed = true;
		}

		return consumed;
	}

	@Override
	public boolean mouseDrag(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
		return false;
	}

	@Override
	public boolean mouseScroll(double mouseX, double mouseY, double scrollX, double scrollY) {
		return false;
	}

	@Override
	public boolean keyPress(int keyCode) {
		return false;
	}

	@Override
	public boolean keyRelease(int keyCode) {
		return false;
	}

	@Override
	public boolean typeChar(char chr) {
		return false;
	}

	@Override
	public void tick() {
	}
}
