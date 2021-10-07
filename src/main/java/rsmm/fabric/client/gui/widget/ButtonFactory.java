package rsmm.fabric.client.gui.widget;

import rsmm.fabric.client.MultimeterClient;

public interface ButtonFactory {
	
	public IButton create(MultimeterClient client, int width, int height);
	
}
