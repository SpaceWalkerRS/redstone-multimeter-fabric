package redstone.multimeter.client.gui.element.action;

import redstone.multimeter.client.gui.element.Element;

public interface MouseRelease<T extends Element> {

	public boolean release(T element);

}
