package redstone.multimeter.client.gui.element;

import redstone.multimeter.client.gui.tooltip.Tooltip;
import redstone.multimeter.client.gui.tooltip.Tooltips;

public abstract class AbstractElement implements Element {

	private int x;
	private int y;
	private int width;
	private int height;
	private boolean hovered;
	private boolean dragging;
	private boolean focused;
	private boolean visible;

	protected AbstractElement(int x, int y, int width, int height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.dragging = false;
		this.visible = true;
	}

	@Override
	public boolean mouseClick(double mouseX, double mouseY, int button) {
		if (button == MOUSE_BUTTON_LEFT) {
			this.setDraggingMouse(true);
		}

		return false;
	}

	@Override
	public boolean mouseRelease(double mouseX, double mouseY, int button) {
		if (button == MOUSE_BUTTON_LEFT) {
			this.setDraggingMouse(false);
		}

		return false;
	}

	@Override
	public boolean isHovered() {
		return this.hovered;
	}

	@Override
	public void setHovered(boolean hovered) {
		this.hovered = hovered;
	}

	@Override
	public boolean isDraggingMouse() {
		return this.dragging;
	}

	@Override
	public void setDraggingMouse(boolean dragging) {
		this.dragging = dragging;
	}

	@Override
	public void onRemoved() {
		this.hovered = false;
		this.dragging = false;
		this.focused = false;
	}

	@Override
	public boolean isFocused() {
		return this.focused;
	}

	@Override
	public void setFocused(boolean focused) {
		this.focused = focused;
	}

	@Override
	public int getX() {
		return this.x;
	}

	@Override
	public void setX(int x) {
		this.x = x;
	}

	@Override
	public int getY() {
		return this.y;
	}

	@Override
	public void setY(int y) {
		this.y = y;
	}

	@Override
	public int getWidth() {
		return this.width;
	}

	@Override
	public int getHeight() {
		return this.height;
	}

	@Override
	public boolean isVisible() {
		return this.visible;
	}

	@Override
	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	@Override
	public void setWidth(int width) {
		this.width = width;
	}

	@Override
	public void setHeight(int height) {
		this.height = height;
	}

	@Override
	public Tooltip getTooltip(int mouseX, int mouseY) {
		return Tooltips.EMPTY;
	}
}
