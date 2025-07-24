package redstone.multimeter.client.gui.element;

import java.util.ArrayList;
import java.util.List;

import redstone.multimeter.client.gui.GuiRenderer;
import redstone.multimeter.client.gui.tooltip.Tooltip;

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
	public void render(GuiRenderer renderer, int mouseX, int mouseY) {
		for (int index = 0; index < this.children.size(); index++) {
			Element child = this.children.get(index);

			if (child.isVisible()) {
				child.render(renderer, mouseX, mouseY);
			}
		}
	}

	@Override
	public void mouseMove(double mouseX, double mouseY) {
		if (!this.isDraggingMouse()) {
			this.updateHoveredElement(mouseX, mouseY);
		}

		for (int index = 0; index < this.children.size(); index++) {
			Element child = this.children.get(index);

			if (child.isVisible()) {
				child.mouseMove(mouseX, mouseY);
			}
		}
	}

	@Override
	public boolean mouseClick(double mouseX, double mouseY, int button) {
		boolean consumed = super.mouseClick(mouseX, mouseY, button);

		if (!consumed) {
			Element focused = this.updateFocusedElement();

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
			Element focused = this.getFocusedElement();

			if (focused != null) {
				consumed = focused.mouseRelease(mouseX, mouseY, button);
			}
		}

		return consumed;
	}

	@Override
	public boolean mouseDrag(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
		if (this.isDraggingMouse()) {
			Element focused = this.getFocusedElement();

			if (focused != null) {
				return focused.mouseDrag(mouseX, mouseY, button, deltaX, deltaY);
			}
		}

		return false;
	}

	@Override
	public boolean mouseScroll(double mouseX, double mouseY, double scrollX, double scrollY) {
		Element hovered = this.getHoveredElement();
		return hovered != null && hovered.mouseScroll(mouseX, mouseY, scrollX, scrollY);
	}

	@Override
	public boolean keyPress(int keyCode) {
		Element focused = this.getFocusedElement();
		return focused != null && focused.keyPress(keyCode);
	}

	@Override
	public boolean keyRelease(int keyCode) {
		Element focused = this.getFocusedElement();
		return focused != null && focused.keyRelease(keyCode);
	}

	@Override
	public boolean typeChar(char chr) {
		Element focused = this.getFocusedElement();
		return focused != null && focused.typeChar(chr);
	}

	@Override
	public void onRemoved() {
		super.onRemoved();

		this.hovered = null;
		this.focused = null;

		for (int index = 0; index < this.children.size(); index++) {
			this.children.get(index).onRemoved();
		}
	}

	@Override
	public void setFocused(boolean focused) {
		super.setFocused(focused);

		if (!this.isFocused()) {
			this.setFocusedElement(null);
		}
	}

	@Override
	public void tick() {
		for (int index = 0; index < this.children.size(); index++) {
			Element child = this.children.get(index);

			if (child.isVisible()) {
				child.tick();
			}
		}
	}

	@Override
	public Tooltip getTooltip(int mouseX, int mouseY) {
		Element hovered = this.getHoveredElement();
		return hovered == null ? super.getTooltip(mouseX, mouseY) : hovered.getTooltip(mouseX, mouseY);
	}

	@Override
	public void update() {
		for (int index = 0; index < this.children.size(); index++) {
			this.children.get(index).update();
		}
	}

	protected List<Element> getChildren() {
		return this.children;
	}

	protected void addChild(Element element) {
		this.children.add(element);
	}

	protected void addChild(int index, Element element) {
		this.children.add(index, element);
	}

	public void removeChildren() {
		this.hovered = null;
		this.focused = null;

		for (int index = 0; index < this.children.size(); index++) {
			this.children.get(index).onRemoved();
		}

		this.children.clear();
	}

	protected Element getHoveredElement() {
		return this.hovered;
	}

	private void updateHoveredElement(double mouseX, double mouseY) {
		this.hovered = null;

		for (int index = 0; index < this.children.size(); index++) {
			Element child = this.children.get(index);

			if (this.hovered == null && this.isHovered() && child.isVisible() && child.isMouseOver(mouseX, mouseY)) {
				this.hovered = child;
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
		if (this.hovered != null) {
			this.hovered.setHovered(true);
		}
	}

	protected Element getFocusedElement() {
		if (this.focused != null && !this.focused.isFocused()) {
			this.setFocusedElement(null);
		}

		return this.focused;
	}

	protected Element updateFocusedElement() {
		return this.setFocusedElement(this.hovered);
	}

	private Element setFocusedElement(Element element) {
		if (element == this.focused) {
			return this.focused;
		}

		if (this.focused != null) {
			this.focused.setFocused(false);
		}

		this.focused = element;

		if (this.focused != null) {
			this.focused.setFocused(true);
		}

		return this.focused;
	}
}
