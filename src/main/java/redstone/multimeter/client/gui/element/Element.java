package redstone.multimeter.client.gui.element;

import redstone.multimeter.client.gui.Tooltip;

public interface Element {

	int MOUSE_BUTTON_LEFT  = 0;
	int MOUSE_BUTTON_RIGHT = 1;
	int MOUSE_SCROLL_UP    = 7;
	int MOUSE_SCROLL_DOWN  = 8;

	void render(int mouseX, int mouseY);

	default boolean isMouseOver(double mouseX, double mouseY) {
		return mouseX >= getX() && mouseX <= (getX() + getWidth()) && mouseY >= getY() && mouseY <= (getY() + getHeight());
	}

	void mouseMove(double mouseX, double mouseY);

	boolean mouseClick(double mouseX, double mouseY, int button);

	boolean mouseRelease(double mouseX, double mouseY, int button);

	boolean mouseDrag(double mouseX, double mouseY, int button, double deltaX, double deltaY);

	boolean mouseScroll(double mouseX, double mouseY, double scrollX, double scrollY);

	boolean keyPress(int keyCode);

	boolean keyRelease(int keyCode);

	boolean typeChar(char chr);

	boolean isHovered();

	void setHovered(boolean hovered);

	boolean isDraggingMouse();

	void setDraggingMouse(boolean draggingMouse);

	void onRemoved();

	boolean isFocused();

	void setFocused(boolean focused);

	void tick();

	int getX();

	void setX(int x);

	int getY();

	void setY(int y);

	int getWidth();

	void setWidth(int width);

	int getHeight();

	void setHeight(int height);

	boolean isVisible();

	void setVisible(boolean visible);

	default Tooltip getTooltip(int mouseX, int mouseY) {
		return Tooltip.EMPTY;
	}

	void update();

}
