package redstone.multimeter.client.gui.screen;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;

public class ScreenWrapper extends Screen {

	private final Screen parent;
	private final RSMMScreen screen;

	private double prevMouseX;
	private double prevMouseY;
	private double mouseX;
	private double mouseY;
	private int lastButton;
	private long lastClickTime;

	public ScreenWrapper(Screen parent, RSMMScreen screen) {
		this.parent = parent;
		this.screen = screen;

		this.screen.wrapper = this;
	}

	@Override
	public void render(int mouseX, int mouseY, float delta) {
		// input events are handled once per tick
		// but we want it once per frame for a snappier experience
		handleInputs();

		screen.render(mouseX, mouseY);
	}

	@Override
	public final void init() {
		screen.init(width, height);
	}

	@Override
	public void tick() {
		screen.tick();
	}

	@Override
	public void removed() {
		screen.onRemoved();
	}

	@Override
	public void handleMouse() {
		boolean consumed = false;

		prevMouseX = mouseX;
		prevMouseY = mouseY;
		mouseX = (double)Mouse.getEventX() * width / minecraft.width;
		mouseY = height - 1 - (double)Mouse.getEventY() * height / minecraft.height;

		if (mouseX != prevMouseX || mouseY != prevMouseY) {
			screen.mouseMove(mouseX, mouseY);
		}

		int button = Mouse.getEventButton();

		if (Mouse.getEventButtonState()) {
			lastButton = button;
			lastClickTime = Minecraft.getTime();

			consumed = screen.mouseClick(mouseX, mouseY, button);
		} else if (button != -1) {
			consumed = screen.mouseRelease(mouseX, mouseY, button);

			if (button == lastButton) {
				lastButton = -1;
			}
		} else if (lastButton != -1 && Minecraft.getTime() - lastClickTime > 0) {
			double deltaX = mouseX - prevMouseX;
			double deltaY = mouseY - prevMouseY;

			consumed = screen.mouseDrag(mouseX, mouseY, lastButton, deltaX, deltaY);
		}

		double scrollY = 0.009D * Mouse.getEventDWheel();

		if (scrollY != 0) {
			consumed = screen.mouseScroll(mouseX, mouseY, 0, scrollY);
		}

		if (consumed) {
			screen.mouseMove(mouseX, mouseY);
		}
	}

	@Override
	public void handleKeyboard() {
		char chr = Keyboard.getEventCharacter();
		int key = Keyboard.getEventKey();
		boolean consumed = false;

		if (key != Keyboard.CHAR_NONE) {
			if (Keyboard.getEventKeyState()) {
				consumed = screen.keyPress(key);
				
				if (!consumed && key == Keyboard.KEY_ESCAPE) {
					screen.close();
				}
			} else {
				consumed = screen.keyRelease(key);
			}
		}
		if (chr >= ' ') {
			consumed = screen.typeChar(chr);
		}

		if (consumed) {
			screen.mouseMove(mouseX, mouseY);
		}
	}

	public Screen getParent() {
		return parent;
	}

	public RSMMScreen getScreen() {
		return screen;
	}
}
