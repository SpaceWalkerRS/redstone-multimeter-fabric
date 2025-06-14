package redstone.multimeter.client.gui.element;

import org.lwjgl.glfw.GLFW;

public abstract class AbstractElement extends RenderHelper2D implements Element {

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
		if (button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
			setDraggingMouse(true);
		}

		return false;
	}

	@Override
	public boolean mouseRelease(double mouseX, double mouseY, int button) {
		if (button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
			setDraggingMouse(false);
		}

		return false;
	}

	@Override
	public boolean isHovered() {
		return hovered;
	}

	@Override
	public void setHovered(boolean hovered) {
		this.hovered = hovered;
	}

	@Override
	public boolean isDraggingMouse() {
		return dragging;
	}

	@Override
	public void setDraggingMouse(boolean dragging) {
		this.dragging = dragging;
	}

	@Override
	public void onRemoved() {
		hovered = false;
		dragging = false;
		focused = false;
	}

	@Override
	public boolean isFocused() {
		return focused;
	}

	@Override
	public void setFocused(boolean focused) {
		this.focused = focused;
	}

	@Override
	public int getX() {
		return x;
	}

	@Override
	public void setX(int x) {
		this.x = x;
	}

	@Override
	public int getY() {
		return y;
	}

	@Override
	public void setY(int y) {
		this.y = y;
	}

	@Override
	public int getWidth() {
		return width;
	}

	@Override
	public int getHeight() {
		return height;
	}

	@Override
	public boolean isVisible() {
		return visible;
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
}
