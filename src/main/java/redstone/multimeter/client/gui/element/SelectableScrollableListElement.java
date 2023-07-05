package redstone.multimeter.client.gui.element;

import java.util.List;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.gui.screen.Screen;

import redstone.multimeter.client.MultimeterClient;
import redstone.multimeter.client.gui.element.button.IButton;

public class SelectableScrollableListElement extends ScrollableListElement {

	private Element selectedElement;

	protected SelectableScrollableListElement(MultimeterClient client, int width, int height) {
		this(client, width, height, 0, 0);
	}

	protected SelectableScrollableListElement(MultimeterClient client, int width, int height, int topBorder, int bottomBorder) {
		super(client, width, height, topBorder, bottomBorder);
	}

	@Override
	protected void renderElement(Element element, int mouseX, int mouseY) {
		boolean selected = (element == getSelectedElement());
		boolean hovered = (element == getHoveredElement());
		boolean drawBackground = selected || hovered;

		if (drawBackground) {
			drawBackground(element, selected);
		}

		super.renderElement(element, mouseX, mouseY);

		if (drawBackground) {
			drawBorder(element, selected);
		}
	}

	@Override
	public boolean mouseClick(double mouseX, double mouseY, int button) {
		boolean consumed = super.mouseClick(mouseX, mouseY, button);

		if (button == MOUSE_BUTTON_LEFT) {
			consumed |= select(getFocusedElement(), true);
		}

		return consumed;
	}

	@Override
	public boolean keyPress(int keyCode) {
		boolean consumed = super.keyPress(keyCode);

		if (!consumed) {
			switch (keyCode) {
			case Keyboard.KEY_TAB:
				return moveSelection(!Screen.isShiftDown(), false);
			case Keyboard.KEY_UP:
				return moveSelection(false, false);
			case Keyboard.KEY_DOWN:
				return moveSelection(true, false);
			case Keyboard.KEY_PRIOR:
				return moveSelection(false, true);
			case Keyboard.KEY_NEXT:
				return moveSelection(true, true);
			}
		}

		return consumed;
	}

	@Override
	public void clear() {
		super.clear();
		selectedElement = null;
	}

	protected boolean select(Element element, boolean playClick) {
		if (element == null || element == selectedElement) {
			return false;
		}

		selectedElement = element;
		selectionChanged(element);

		if (playClick) {
			IButton.playClickSound(client);
		}

		return true;
	}

	protected boolean moveSelection(boolean forward, boolean skipAll) {
		List<Element> children = getChildren();

		if (children.isEmpty()) {
			return false;
		}
		if (skipAll || selectedElement == null) {
			return select(children.get((forward ^ skipAll) ? 0 : children.size() - 1), false);
		}

		int index = children.indexOf(selectedElement);

		if (index < 0) {
			return false;
		}

		index += forward ? 1 : -1;

		if (index >= 0 && index < children.size()) {
			return select(children.get(index), false);
		}

		return false;
	}

	protected Element getSelectedElement() {
		return selectedElement;
	}

	protected void selectionChanged(Element element) {
		double dy = getAmountOffScreen(element);

		if (dy != 0.0D) {
			scroll(dy);
		}
	}

	protected void drawBackground(Element element, boolean selected) {
		renderRect(element.getX(), element.getY(), element.getWidth(), element.getHeight(), getBackgroundColor(selected));
	}

	protected void drawBorder(Element element, boolean selected) {
		renderBorder(element.getX(), element.getY(), element.getWidth(), element.getHeight(), getBorderColor(selected));
	}

	protected int getBackgroundColor(boolean selected) {
		return selected ? 0x80000000 : 0x20000000;
	}

	protected int getBorderColor(boolean selected) {
		return selected ? 0xFFFFFFFF : 0x40FFFFFF;
	}
}
