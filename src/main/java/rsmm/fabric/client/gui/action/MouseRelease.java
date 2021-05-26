package rsmm.fabric.client.gui.action;

import rsmm.fabric.client.gui.element.IElement;

public interface MouseRelease<T extends IElement> {
	
	public boolean release(T element);
	
}
