package redstone.multimeter.client.gui.element.action;

import redstone.multimeter.client.gui.element.IElement;

public interface MouseRelease<T extends IElement> {
	
	public boolean release(T element);
	
}
