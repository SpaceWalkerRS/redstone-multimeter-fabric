package redstone.multimeter.client.gui.element;

public abstract class AbstractElement extends RenderHelper2D implements IElement {
	
	private int x;
	private int y;
	private int width;
	private int height;
	private boolean focused;
	private boolean dragging;
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
	public boolean isDraggingMouse() {
		return dragging;
	}
	
	@Override
	public void setDraggingMouse(boolean dragging) {
		this.dragging = dragging;
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
	
	protected void setWidth(int width) {
		this.width = width;
	}
	
	protected void setHeight(int height) {
		this.height = height;
	}
}
