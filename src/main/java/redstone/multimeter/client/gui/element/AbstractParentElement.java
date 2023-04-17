package redstone.multimeter.client.gui.element;

import java.util.ArrayList;
import java.util.List;

import redstone.multimeter.client.gui.Tooltip;

public abstract class AbstractParentElement extends AbstractElement {

	private final List<Element> children = new ArrayList<>();

	private boolean focused;
	private Element hoveredElement;
	private Element focusedElement;

	protected AbstractParentElement() {
		this(0, 0, 0, 0);
	}

	protected AbstractParentElement(int x, int y, int width, int height) {
		super(x, y, width, height);
	}

	@Override
	public void render(int mouseX, int mouseY) {
		for (int index = 0; index < children.size(); index++) {
			Element child = children.get(index);

			if (child.isVisible()) {
				child.render(mouseX, mouseY);
			}
		}
	}

	@Override
	public void mouseMove(double mouseX, double mouseY) {
		hoveredElement = null;

		for (int index = 0; index < children.size(); index++) {
			Element child = children.get(index);

			if (child.isVisible()) {
				child.mouseMove(mouseX, mouseY);

				if (hoveredElement == null && child.isHovered(mouseX, mouseY)) {
					hoveredElement = child;
				}
			}
		}
	}

	@Override
	public boolean mouseClick(double mouseX, double mouseY, int button) {
		boolean consumed = super.mouseClick(mouseX, mouseY, button);

		if (!consumed) {
			Element focused = updateFocusedElement();

			if (focused != null) {
				consumed = focused.mouseClick(mouseX, mouseY, button);
			}
		}

		return consumed;
	}

	@Override
	public boolean mouseRelease(double mouseX, double mouseY, int button) {
		boolean consumed = super.mouseRelease(mouseX, mouseY, button);

		if (!consumed) {
			Element focused = getFocusedElement();

			if (focused != null) {
				consumed = focused.mouseRelease(mouseX, mouseY, button);
			}
		}

		return consumed;
	}

	@Override
	public boolean mouseDrag(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
		if (isDraggingMouse()) {
			Element focused = getFocusedElement();

			if (focused != null) {
				return focused.mouseDrag(mouseX, mouseY, button, deltaX, deltaY);
			}
		}

		return false;
	}

	@Override
	public boolean mouseScroll(double mouseX, double mouseY, double scrollX, double scrollY) {
		Element hovered = getHoveredElement();
		return hovered != null && hovered.mouseScroll(mouseX, mouseY, scrollX, scrollY);
	}

	@Override
	public boolean keyPress(int keyCode, int scanCode, int modifiers) {
		Element focused = getFocusedElement();
		return focused != null && focused.keyPress(keyCode, scanCode, modifiers);
	}

	@Override
	public boolean keyRelease(int keyCode, int scanCode, int modifiers) {
		Element focused = getFocusedElement();
		return focused != null && focused.keyRelease(keyCode, scanCode, modifiers);
	}

	@Override
	public boolean typeChar(char chr, int modifiers) {
		Element focused = getFocusedElement();
		return focused != null && focused.typeChar(chr, modifiers);
	}

	@Override
	public void onRemoved() {
		for (int index = 0; index < children.size(); index++) {
			children.get(index).onRemoved();
		}
	}

	@Override
	public boolean isFocused() {
		return focused;
	}

	@Override
	public void setFocused(boolean focused) {
		this.focused = focused;

		if (!isFocused()) {
			setFocusedElement(null);
		}
	}

	@Override
	public void tick() {
		for (int index = 0; index < children.size(); index++) {
			Element child = children.get(index);

			if (child.isVisible()) {
				child.tick();
			}
		}
	}

	@Override
	public void setX(int x) {
		super.setX(x);
		onChangedX(x);
	}

	@Override
	public void setY(int y) {
		super.setY(y);
		onChangedY(y);
	}

	@Override
	public Tooltip getTooltip(int mouseX, int mouseY) {
		Element hovered = getHoveredElement();
		return hovered == null ? super.getTooltip(mouseX, mouseY) : hovered.getTooltip(mouseX, mouseY);
	}

	@Override
	public void update() {
		for (int index = 0; index < children.size(); index++) {
			children.get(index).update();
		}
	}

	protected List<Element> getChildren() {
		return children;
	}

	protected void addChild(Element element) {
		children.add(element);
	}

	protected void addChild(int index, Element element) {
		children.add(index, element);
	}

	public void removeChildren() {
		hoveredElement = null;
		focusedElement = null;

		for (int index = 0; index < children.size(); index++) {
			children.get(index).onRemoved();
		}

		children.clear();
	}

	protected Element getHoveredElement() {
		return hoveredElement;
	}

	protected void updateHoveredElement(double mouseX, double mouseY) {
		hoveredElement = getHoveredElement(mouseX, mouseY);
	}

	private Element getHoveredElement(double mouseX, double mouseY) {
		if (!isDraggingMouse() && isHovered(mouseX, mouseY)) {
			for (int index = 0; index < children.size(); index++) {
				Element child = children.get(index);

				if (child.isVisible() && child.isHovered(mouseX, mouseY)) {
					return child;
				}
			}
		}

		return null;
	}

	protected Element getFocusedElement() {
		if (focusedElement != null && !focusedElement.isFocused()) {
			setFocusedElement(null);
		}

		return focusedElement;
	}

	protected Element updateFocusedElement() {
		return setFocusedElement(hoveredElement);
	}

	private Element setFocusedElement(Element element) {
		if (element == focusedElement) {
			return focusedElement;
		}

		if (focusedElement != null) {
			focusedElement.setFocused(false);
		}

		focusedElement = element;

		if (focusedElement != null) {
			focusedElement.setFocused(true);
		}

		return focusedElement;
	}

	protected abstract void onChangedX(int x);

	protected abstract void onChangedY(int y);

}
