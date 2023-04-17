package redstone.multimeter.client.gui.element.action;

import redstone.multimeter.client.gui.element.Element;

public interface MousePress<T extends Element> {

	public boolean accept(T element);

}
