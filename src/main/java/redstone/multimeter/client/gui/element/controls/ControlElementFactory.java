package redstone.multimeter.client.gui.element.controls;

import redstone.multimeter.client.MultimeterClient;

public interface ControlElementFactory {
	
	public ControlElement create(MultimeterClient client, int midpoint, int controlWidth);
	
}
