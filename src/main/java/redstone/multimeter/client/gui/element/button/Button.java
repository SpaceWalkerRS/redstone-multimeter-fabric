package redstone.multimeter.client.gui.element.button;

import java.util.function.Supplier;

import org.lwjgl.glfw.GLFW;

import net.minecraft.text.Text;

import redstone.multimeter.client.MultimeterClient;
import redstone.multimeter.client.gui.Tooltip;
import redstone.multimeter.client.gui.element.action.MousePress;

public class Button extends AbstractButton {

	private final MousePress<Button> onPress;

	public Button(MultimeterClient client, int x, int y, Supplier<Text> message, Supplier<Tooltip> tooltip, MousePress<Button> onPress) {
		this(client, x, y, DEFAULT_WIDTH, DEFAULT_HEIGHT, message, tooltip, onPress);
	}

	public Button(MultimeterClient client, int x, int y, int width, int height, Supplier<Text> message, Supplier<Tooltip> tooltip, MousePress<Button> onPress) {
		super(client, x, y, width, height, message, tooltip);

		this.onPress = onPress;
	}

	@Override
	public boolean mouseClick(double mouseX, double mouseY, int button) {
		boolean consumed = super.mouseClick(mouseX, mouseY, button);

		if (!consumed && isActive() && button == GLFW.GLFW_MOUSE_BUTTON_LEFT && onPress.accept(this)) {
			playClickSound();
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
	public boolean keyPress(int keyCode, int scanCode, int modifiers) {
		return false;
	}

	@Override
	public boolean keyRelease(int keyCode, int scanCode, int modifiers) {
		return false;
	}

	@Override
	public boolean typeChar(char chr, int modifiers) {
		return false;
	}

	@Override
	public void onRemoved() {
	}

	@Override
	public void tick() {
	}
}
