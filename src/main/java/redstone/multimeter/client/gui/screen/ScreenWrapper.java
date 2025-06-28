package redstone.multimeter.client.gui.screen;

import org.lwjgl.glfw.GLFW;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;

import redstone.multimeter.client.gui.CursorType;
import redstone.multimeter.client.gui.GuiRenderer;
import redstone.multimeter.client.gui.element.Element;

public class ScreenWrapper extends Screen {

	private final Screen parent;
	private final RSMMScreen screen;

	private double lastMouseX = Double.MIN_VALUE;
	private double lastMouseY = Double.MIN_VALUE;

	public ScreenWrapper(Screen parent, RSMMScreen screen) {
		super(null);

		this.parent = parent;
		this.screen = screen;

		this.screen.wrapper = this;
	}

	@Override
	public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
		screen.render(new GuiRenderer(graphics), mouseX, mouseY);
	}

	@Override
	public final void mouseMoved(double mouseX, double mouseY) {
		screen.mouseMove(mouseX, mouseY);

		lastMouseX = mouseX;
		lastMouseY = mouseY;
	}

	@Override
	public final boolean mouseClicked(double mouseX, double mouseY, int button) {
		boolean consumed = screen.mouseClick(mouseX, mouseY, button);

		if (consumed) {
			mouseMoved(mouseX, mouseY);
		}

		return consumed;
	}

	@Override
	public final boolean mouseReleased(double mouseX, double mouseY, int button) {
		boolean consumed = screen.mouseRelease(mouseX, mouseY, button);

		if (consumed) {
			mouseMoved(mouseX, mouseY);
		}

		return consumed;
	}

	@Override
	public final boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
		boolean consumed = screen.mouseDrag(mouseX, mouseY, button, deltaX, deltaY);

		if (consumed) {
			mouseMoved(mouseX, mouseY);
		}

		return consumed;
	}

	@Override
	public final boolean mouseScrolled(double mouseX, double mouseY, double amount) {
		return false;
	}

	public final boolean mouseScrolled(double mouseX, double mouseY, double amountX, double amountY) {
		boolean consumed = screen.mouseScroll(mouseX, mouseY, amountX, amountY);

		if (consumed) {
			mouseMoved(mouseX, mouseY);
		}

		return consumed;
	}

	@Override
	public final boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (screen.keyPress(keyCode, scanCode, modifiers)) {
			mouseMoved(lastMouseX, lastMouseY);
			return true;
		}
		if (screen.shouldCloseOnEsc() && keyCode == GLFW.GLFW_KEY_ESCAPE) {
			screen.close();
			return true;
		}

		return false;
	}

	@Override
	public final boolean keyReleased(int keyCode, int scanCode, int modifiers) {
		boolean consumed = screen.keyRelease(keyCode, scanCode, modifiers);

		if (consumed) {
			mouseMoved(lastMouseX, lastMouseY);
		}

		return consumed;
	}

	@Override
	public final boolean charTyped(char chr, int modifiers) {
		boolean consumed = screen.typeChar(chr, modifiers);

		if (consumed) {
			mouseMoved(lastMouseX, lastMouseY);
		}

		return consumed;
	}

	@Override
	public boolean shouldCloseOnEsc() {
		return screen.shouldCloseOnEsc();
	}

	@Override
	protected final void init() {
		screen.init(width, height);
	}

	@Override
	public void tick() {
		screen.tick();
	}

	@Override
	public void removed() {
		screen.onRemoved();
		Element.setCursor(CursorType.ARROW);
	}

	public Screen getParent() {
		return parent;
	}

	public RSMMScreen getScreen() {
		return screen;
	}
}
