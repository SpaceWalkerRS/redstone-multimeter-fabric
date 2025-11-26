package redstone.multimeter.client.gui.screen;

import org.lwjgl.glfw.GLFW;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.MouseButtonEvent;
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
	public final boolean mouseClicked(MouseButtonEvent event, boolean _doubleClick) {
		int ticksSinceLastClick = this.ticks - this.lastClickTicks;
		this.lastClickTicks = this.ticks;

		boolean doubleClick = ticksSinceLastClick < Options.Miscellaneous.DOUBLE_CLICK_TIME.get() && this.lastClickButton == event.button();
		boolean consumed = screen.mouseClick(MouseEvent.click(event.x(), event.y(), event.button(), doubleClick));

		if (consumed) {
			mouseMoved(event.x(), event.y());
		}

		return consumed;
	}

	@Override
	public final boolean mouseReleased(MouseButtonEvent event) {
		boolean consumed = screen.mouseRelease(MouseEvent.release(event.x(), event.y(), event.button()));

		if (consumed) {
			mouseMoved(event.x(), event.y());
		}

		return consumed;
	}

	@Override
	public final boolean mouseDragged(MouseButtonEvent event, double deltaX, double deltaY) {
		boolean consumed = screen.mouseDrag(MouseEvent.drag(event.x(), event.y(), event.button(), deltaX, deltaY));

		if (consumed) {
			mouseMoved(event.x(), event.y());
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
	public final boolean keyPressed(net.minecraft.client.input.KeyEvent event) {
		if (screen.keyPress(KeyEvent.press(event.key(), event.scancode(), event.modifiers()))) {
			mouseMoved(lastMouseX, lastMouseY);
			return true;
		}
		if (screen.shouldCloseOnEsc() && event.key() == GLFW.GLFW_KEY_ESCAPE) {
			screen.close();
			return true;
		}

		return false;
	}

	@Override
	public final boolean keyReleased(net.minecraft.client.input.KeyEvent event) {
		boolean consumed = screen.keyRelease(KeyEvent.release(event.key(), event.scancode(), event.modifiers()));

		if (consumed) {
			mouseMoved(lastMouseX, lastMouseY);
		}

		return consumed;
	}

	@Override
	public final boolean charTyped(net.minecraft.client.input.CharacterEvent event) {
		boolean consumed = screen.typeChar(CharacterEvent.type(event.codepoint(), event.modifiers()));

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
