package rsmm.fabric.client.gui.action;

import rsmm.fabric.client.gui.element.IElement;

public interface MousePress<T extends IElement> {
	
	public boolean press(T element);
	
}
