package redstone.multimeter.client.gui.element.button;

import java.util.function.Supplier;

import redstone.multimeter.client.gui.element.action.MousePress;
import redstone.multimeter.client.gui.element.input.CharacterEvent;
import redstone.multimeter.client.gui.element.input.KeyEvent;
import redstone.multimeter.client.gui.element.input.MouseEvent;
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
	public boolean mouseClick(MouseEvent.Click event) {
		boolean consumed = super.mouseClick(event);

		if (!consumed && isActive() && event.isLeftButton() && onPress.accept(this, event)) {
			Button.playClickSound();
			consumed = true;
		}

		return consumed;
	}

	@Override
	public boolean mouseDrag(MouseEvent.Drag event) {
		return false;
	}

	@Override
	public boolean mouseScroll(MouseEvent.Scroll event) {
		return false;
	}

	@Override
	public boolean keyPress(KeyEvent.Press event) {
		return false;
	}

	@Override
	public boolean keyRelease(KeyEvent.Release event) {
		return false;
	}

	@Override
	public boolean typeChar(CharacterEvent.Type event) {
		return false;
	}

	@Override
	public void tick() {
	}
}
