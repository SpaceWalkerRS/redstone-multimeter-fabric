package redstone.multimeter.client.gui.element.action;

import redstone.multimeter.client.gui.element.IElement;

public interface MousePress<T extends IElement> {
	
	public boolean accept(T element);
	
}
