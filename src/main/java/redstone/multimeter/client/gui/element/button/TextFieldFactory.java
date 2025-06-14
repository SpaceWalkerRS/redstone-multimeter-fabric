package redstone.multimeter.client.gui.element.button;

import redstone.multimeter.client.MultimeterClient;

public interface TextFieldFactory extends ButtonFactory {

	@Override
	TextField create(MultimeterClient client, int width, int height);

}
