package redstone.multimeter.client.gui.screen;

import org.lwjgl.glfw.GLFW;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import redstone.multimeter.client.gui.CursorType;
import redstone.multimeter.client.gui.GuiRenderer;
import redstone.multimeter.client.gui.element.Element;
import redstone.multimeter.client.gui.element.input.CharacterEvent;
import redstone.multimeter.client.gui.element.input.KeyEvent;
import redstone.multimeter.client.gui.element.input.MouseEvent;
import redstone.multimeter.client.option.Options;

public class ScreenWrapper extends Screen {

	private final Screen parent;
	private final RSMMScreen screen;

	private int ticks;

	private double lastMouseX = Double.MIN_VALUE;
	private double lastMouseY = Double.MIN_VALUE;

	private int lastClickTicks = -1;
	private int lastClickButton = -1;

	public ScreenWrapper(Screen parent, RSMMScreen screen) {
		super(Component.literal(""));

		this.parent = parent;
		this.screen = screen;

		this.screen.wrapper = this;
	}

	@Override
	public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
		GuiRenderer renderer = new GuiRenderer(graphics);

		screen.render(renderer, mouseX, mouseY);
		screen.renderSecondPass(renderer, mouseX, mouseY);
		screen.renderTooltip(renderer, mouseX, mouseY);
	}

	@Override
	public final void mouseMoved(double mouseX, double mouseY) {
		screen.mouseMove(mouseX, mouseY);

		lastMouseX = mouseX;
		lastMouseY = mouseY;
	}

	@Override
	public final boolean mouseClicked(double mouseX, double mouseY, int button) {
		int ticksSinceLastClick = this.ticks - this.lastClickTicks;
		this.lastClickTicks = this.ticks;

		boolean doubleClick = ticksSinceLastClick < Options.Miscellaneous.DOUBLE_CLICK_TIME.get() && this.lastClickButton == button;
		boolean consumed = screen.mouseClick(MouseEvent.click(mouseX, mouseY, button, doubleClick));

		if (consumed) {
			mouseMoved(mouseX, mouseY);
		}

		return consumed;
	}

	@Override
	public final boolean mouseReleased(double mouseX, double mouseY, int button) {
		boolean consumed = screen.mouseRelease(MouseEvent.release(mouseX, mouseY, button));

		if (consumed) {
			mouseMoved(mouseX, mouseY);
		}

		return consumed;
	}

	@Override
	public final boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
		boolean consumed = screen.mouseDrag(MouseEvent.drag(mouseX, mouseY, button, deltaX, deltaY));

		if (consumed) {
			mouseMoved(mouseX, mouseY);
		}

		return consumed;
	}

	@Override
	public final boolean mouseScrolled(double mouseX, double mouseY, double amountX, double amountY) {
		boolean consumed = screen.mouseScroll(MouseEvent.scroll(mouseX, mouseY, amountX, amountY));

		if (consumed) {
			mouseMoved(mouseX, mouseY);
		}

		return consumed;
	}

	@Override
	public final boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (screen.keyPress(KeyEvent.press(keyCode, scanCode, modifiers))) {
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
		boolean consumed = screen.keyRelease(KeyEvent.release(keyCode, scanCode, modifiers));

		if (consumed) {
			mouseMoved(lastMouseX, lastMouseY);
		}

		return consumed;
	}

	@Override
	public final boolean charTyped(char chr, int modifiers) {
		boolean consumed = screen.typeChar(CharacterEvent.type(chr, modifiers));

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
		this.ticks++;
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
