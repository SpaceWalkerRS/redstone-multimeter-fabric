package redstone.multimeter.client.gui.widget;

import redstone.multimeter.client.MultimeterClient;

public interface ButtonFactory {
	
	public IButton create(MultimeterClient client, int width, int height);
	
}
