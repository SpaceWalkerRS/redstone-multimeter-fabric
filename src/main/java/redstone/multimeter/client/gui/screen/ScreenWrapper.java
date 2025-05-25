package redstone.multimeter.client.gui.screen;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;

public class ScreenWrapper extends Screen {

	private final Screen parent;
	private final RSMMScreen screen;

	private double prevX;
	private double prevY;
	private double mouseX;
	private double mouseY;
	private int lastButton;
	private long buttonTicks;

	public ScreenWrapper(Screen parent, RSMMScreen screen) {
		this.parent = parent;
		this.screen = screen;

		this.screen.wrapper = this;
	}

	@Override
	public void render(int mouseX, int mouseY, float delta) {
		screen.render(mouseX, mouseY);
	}

	@Override
	public void handleInputs() {
		if (Mouse.isCreated()) {
			handleMouseEvents();
		}
		if (Keyboard.isCreated()) {
			handleKeyboardEvents();
		}
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

	public Screen getParent() {
		return parent;
	}

	public RSMMScreen getScreen() {
		return screen;
	}

	private void handleMouseEvents() {
		updateMousePos();
		handleScroll();

		while (Mouse.next()) {
			int button = Mouse.getEventButton();

			if (Mouse.getEventButtonState()) {
				lastButton = button;
				buttonTicks = Minecraft.getTime();

				screen.mouseClick(mouseX, mouseY, button);
			} else {
				screen.mouseRelease(mouseX, mouseY, button);

				if (button == lastButton) {
					lastButton = -1;
				}
			}
		}

		handleDrag();
	}

	private void updateMousePos() {
		prevX = mouseX;
		prevY = mouseY;
		mouseX = (double)Mouse.getX() * width / minecraft.width;
		mouseY = height - 1 - (double)Mouse.getY() * height / minecraft.height;

		if (mouseX != prevX || mouseY != prevY) {
			screen.mouseMove(mouseX, mouseY);
		}
	}

	private void handleScroll() {
		double scrollY = 0.05D * Mouse.getDWheel();

		if (scrollY != 0) {
			screen.mouseScroll(mouseX, mouseY, 0, scrollY);
		}
	}

	private void handleDrag() {
		if (lastButton != -1 && buttonTicks > 0L) {
			double deltaX = mouseX - prevX;
			double deltaY = mouseY - prevY;

			if (deltaX != 0 && deltaY != 0) {
				screen.mouseDrag(mouseX, mouseY, lastButton, deltaX, deltaY);
			}
		}
	}

	private void handleKeyboardEvents() {
		while (Keyboard.next()) {
			char chr = Keyboard.getEventCharacter();
			int key = Keyboard.getEventKey();

			if (key != Keyboard.CHAR_NONE) {
				if (Keyboard.getEventKeyState()) {
					if (!screen.keyPress(key) && key == Keyboard.KEY_ESCAPE) {
						screen.close();
					}
				} else {
					screen.keyRelease(key);
				}
			}
			if (chr >= ' ') {
				screen.typeChar(chr);
			}
		}
	}
}
