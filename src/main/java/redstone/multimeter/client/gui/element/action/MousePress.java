package redstone.multimeter.client.gui.element.action;

import redstone.multimeter.client.gui.element.Element;

public interface MousePress<T extends Element> {

	boolean accept(T element);

	static <T extends Element> boolean pass(T element) {
		return false;
	}

	static <T extends Element> boolean consume(T element) {
		return true;
	}
}
