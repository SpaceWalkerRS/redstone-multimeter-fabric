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
			boolean consumed = false;

			if (Mouse.getEventButtonState()) {
				lastButton = button;
				buttonTicks = Minecraft.getTime();

				consumed = screen.mouseClick(mouseX, mouseY, button);
			} else {
				consumed = screen.mouseRelease(mouseX, mouseY, button);

				if (button == lastButton) {
					lastButton = -1;
				}
			}

			if (consumed) {
				screen.mouseMove(mouseX, mouseY);
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
			boolean consumed = screen.mouseScroll(mouseX, mouseY, 0, scrollY);

			if (consumed) {
				screen.mouseMove(mouseX, mouseY);
			}
		}
	}

	private void handleDrag() {
		if (lastButton != -1 && buttonTicks > 0L) {
			double deltaX = mouseX - prevX;
			double deltaY = mouseY - prevY;

			if (deltaX != 0 && deltaY != 0) {
				boolean consumed = screen.mouseDrag(mouseX, mouseY, lastButton, deltaX, deltaY);

				if (consumed) {
					screen.mouseMove(mouseX, mouseY);
				}
			}
		}
	}

	private void handleKeyboardEvents() {
		while (Keyboard.next()) {
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
	}
}
