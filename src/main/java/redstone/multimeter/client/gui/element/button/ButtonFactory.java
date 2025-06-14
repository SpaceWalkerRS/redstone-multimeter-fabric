package redstone.multimeter.client.gui.element.button;

import redstone.multimeter.client.MultimeterClient;

public interface ButtonFactory {

	IButton create(MultimeterClient client, int width, int height);

}
