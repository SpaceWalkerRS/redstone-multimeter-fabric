package redstone.multimeter.client.gui.element;

import java.util.ArrayList;
import java.util.List;

import redstone.multimeter.client.gui.Tooltip;

public abstract class AbstractParentElement extends AbstractElement {

	private final List<Element> children = new ArrayList<>();

	private Element hovered;
	private Element focused;

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
		if (!isDraggingMouse()) {
			updateHoveredElement(mouseX, mouseY);
		}

		for (int index = 0; index < children.size(); index++) {
			Element child = children.get(index);

			if (child.isVisible()) {
				child.mouseMove(mouseX, mouseY);
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
	public boolean keyPress(int keyCode) {
		Element focused = getFocusedElement();
		return focused != null && focused.keyPress(keyCode);
	}

	@Override
	public boolean keyRelease(int keyCode) {
		Element focused = getFocusedElement();
		return focused != null && focused.keyRelease(keyCode);
	}

	@Override
	public boolean typeChar(char chr) {
		Element focused = getFocusedElement();
		return focused != null && focused.typeChar(chr);
	}

	@Override
	public void onRemoved() {
		super.onRemoved();

		hovered = null;
		focused = null;

		for (int index = 0; index < children.size(); index++) {
			children.get(index).onRemoved();
		}
	}

	@Override
	public void setFocused(boolean focused) {
		super.setFocused(focused);

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
		hovered = null;
		focused = null;

		for (int index = 0; index < children.size(); index++) {
			children.get(index).onRemoved();
		}

		children.clear();
	}

	protected Element getHoveredElement() {
		return hovered;
	}

	private void updateHoveredElement(double mouseX, double mouseY) {
		hovered = null;

		for (int index = 0; index < children.size(); index++) {
			Element child = children.get(index);

			if (hovered == null && isHovered() && child.isVisible() && child.isMouseOver(mouseX, mouseY)) {
				hovered = child;
			} else {
				child.setHovered(false);
			}
		}

		// always do this last, that way the transition from one
		// hovered element to another behaves consistently:
		//   oldHovered.setHovered(false);
		//   newHovered.setHovered(true);
		// if this is instead done during iteration, the order may
		// be different:
		//   newHovered.setHovered(true);
		//   oldHovered.setHovered(false);
		if (hovered != null) {
			hovered.setHovered(true);
		}
	}

	protected Element getFocusedElement() {
		if (focused != null && !focused.isFocused()) {
			setFocusedElement(null);
		}

		return focused;
	}

	protected Element updateFocusedElement() {
		return setFocusedElement(hovered);
	}

	private Element setFocusedElement(Element element) {
		if (element == focused) {
			return focused;
		}

		if (focused != null) {
			focused.setFocused(false);
		}

		focused = element;

		if (focused != null) {
			focused.setFocused(true);
		}

		return focused;
	}
}
