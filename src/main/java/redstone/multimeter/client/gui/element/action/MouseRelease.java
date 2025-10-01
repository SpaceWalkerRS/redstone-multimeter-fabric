package redstone.multimeter.client.gui.element.action;

import redstone.multimeter.client.gui.element.Element;
import redstone.multimeter.client.gui.element.input.MouseEvent;

public interface MouseRelease<T extends Element> {

	boolean accept(T element, MouseEvent.Release event);

	static <T extends Element> boolean pass(T element, MouseEvent.Release event) {
		return false;
	}

	static <T extends Element> boolean consume(T element, MouseEvent.Release event) {
		return true;
	}
}
