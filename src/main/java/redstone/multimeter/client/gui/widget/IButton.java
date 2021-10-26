package redstone.multimeter.client.gui.widget;

import redstone.multimeter.client.gui.element.IElement;

public interface IButton extends IElement {
	
	public static final int DEFAULT_WIDTH = 150;
	public static final int DEFAULT_HEIGHT = 20;
	
	public boolean isActive();
	
	public void setActive(boolean active);
	
}
