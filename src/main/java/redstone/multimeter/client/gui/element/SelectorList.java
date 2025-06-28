package redstone.multimeter.client.gui.element;

import java.util.List;

import org.lwjgl.glfw.GLFW;

import net.minecraft.client.gui.screen.Screen;

import redstone.multimeter.client.gui.GuiRenderer;
import redstone.multimeter.client.gui.element.button.Button;

public class SelectorList extends ScrollableList {

	private Element selection;

	protected SelectorList(int width, int height) {
		this(width, height, 0, 0);
	}

	protected SelectorList(int width, int height, int topBorder, int bottomBorder) {
		super(width, height, topBorder, bottomBorder);
	}

	@Override
	protected void renderElement(GuiRenderer renderer, Element element, int mouseX, int mouseY) {
		boolean selected = (element == this.getSelectedElement());
		boolean hovered = (element == this.getHoveredElement());
		boolean drawBackground = selected || hovered;

		if (drawBackground) {
			this.drawBackground(renderer, element, selected);
		}

		super.renderElement(renderer, element, mouseX, mouseY);

		if (drawBackground) {
			this.drawBorder(renderer, element, selected);
		}
	}

	@Override
	public boolean mouseClick(double mouseX, double mouseY, int button) {
		boolean consumed = super.mouseClick(mouseX, mouseY, button);

		if (button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
			consumed |= this.setSelection(this.getFocusedElement(), true);
		}

		return consumed;
	}

	@Override
	public boolean keyPress(int keyCode, int scanCode, int modifiers) {
		boolean consumed = super.keyPress(keyCode, scanCode, modifiers);

		if (!consumed) {
			switch (keyCode) {
			case GLFW.GLFW_KEY_TAB:
				return this.moveSelection(!Screen.isShiftDown(), false);
			case GLFW.GLFW_KEY_UP:
				return this.moveSelection(false, false);
			case GLFW.GLFW_KEY_DOWN:
				return this.moveSelection(true, false);
			case GLFW.GLFW_KEY_PAGE_UP:
				return this.moveSelection(false, true);
			case GLFW.GLFW_KEY_PAGE_DOWN:
				return this.moveSelection(true, true);
			}
		}

		return consumed;
	}

	@Override
	public void clear() {
		super.clear();
		this.selection = null;
	}

	private boolean setSelection(Element element, boolean playClick) {
		if (element == null || element == this.selection) {
			return false;
		}

		this.selection = element;
		this.selectionChanged(element);

		if (playClick) {
			Button.playClickSound();
		}

		return true;
	}

	private boolean moveSelection(boolean forward, boolean skipAll) {
		List<Element> children = this.getChildren();

		if (children.isEmpty()) {
			return false;
		}
		if (skipAll || this.selection == null) {
			return setSelection(children.get((forward ^ skipAll) ? 0 : children.size() - 1), false);
		}

		int index = children.indexOf(this.selection);

		if (index < 0) {
			return false;
		}

		index += forward ? 1 : -1;

		if (index >= 0 && index < children.size()) {
			return this.setSelection(children.get(index), false);
		}

		return false;
	}

	protected Element getSelectedElement() {
		return this.selection;
	}

	protected void selectionChanged(Element element) {
		double dy = this.getAmountOffScreen(element);

		if (dy != 0.0D) {
			this.scroll(dy);
		}
	}

	protected void drawBackground(GuiRenderer renderer, Element element, boolean selected) {
		renderer.fill(element.getX(), element.getY(), element.getX() + element.getWidth(), element.getY() + element.getHeight(), this.getBackgroundColor(selected));
	}

	protected void drawBorder(GuiRenderer renderer, Element element, boolean selected) {
		renderer.borders(element.getX(), element.getY(), element.getX() + element.getWidth(), element.getY() + element.getHeight(), this.getBorderColor(selected));
	}

	protected int getBackgroundColor(boolean selected) {
		return selected ? 0x80000000 : 0x20000000;
	}

	protected int getBorderColor(boolean selected) {
		return selected ? 0xFFFFFFFF : 0x40FFFFFF;
	}
}
