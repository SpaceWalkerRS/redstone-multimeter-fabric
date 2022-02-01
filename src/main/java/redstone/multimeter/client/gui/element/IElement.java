package redstone.multimeter.client.gui.element;

import redstone.multimeter.client.gui.Tooltip;

public interface IElement {
	
	public static final int MOUSE_BUTTON_LEFT  = 0;
	public static final int MOUSE_BUTTON_RIGHT = 1;
	public static final int MOUSE_SCROLL_UP    = 7;
	public static final int MOUSE_SCROLL_DOWN  = 8;
	
	public void render(int mouseX, int mouseY);
	
	public void mouseMove(double mouseX, double mouseY);
	
	default boolean mouseClick(double mouseX, double mouseY, int button) {
		if (button == MOUSE_BUTTON_LEFT) {
			setDraggingMouse(true);
		}
		
		return false;
	}
	
	default boolean mouseRelease(double mouseX, double mouseY, int button) {
		if (button == MOUSE_BUTTON_LEFT) {
			setDraggingMouse(false);
		}
		
		return false;
	}
	
	public boolean mouseDrag(double mouseX, double mouseY, int button, double deltaX, double deltaY);
	
	public boolean mouseScroll(double mouseX, double mouseY, double scrollX, double scrollY);
	
	default boolean isHovered(double mouseX, double mouseY) {
		return mouseX >= getX() && mouseX <= (getX() + getWidth()) && mouseY >= getY() && mouseY <= (getY() + getHeight());
	}
	
	public boolean keyPress(int keyCode);
	
	public boolean keyRelease(int keyCode);
	
	public boolean typeChar(char chr);
	
	public boolean isDraggingMouse();
	
	public void setDraggingMouse(boolean dragging);
	
	public void onRemoved();
	
	public boolean isFocused();
	
	public void setFocused(boolean focused);
	
	public void tick();
	
	public int getX();
	
	public void setX(int x);
	
	public int getY();
	
	public void setY(int y);
	
	public int getWidth();
	
	public int getHeight();
	
	public boolean isVisible();
	
	public void setVisible(boolean visible);
	
	default Tooltip getTooltip(int mouseX, int mouseY) {
		return Tooltip.EMPTY;
	}
	
	public void update();
	
}
