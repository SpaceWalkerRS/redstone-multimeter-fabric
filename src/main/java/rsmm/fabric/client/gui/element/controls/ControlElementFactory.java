package rsmm.fabric.client.gui.element.controls;

import rsmm.fabric.client.MultimeterClient;

public interface ControlElementFactory {
	
	public ControlElement create(MultimeterClient client, int midpoint, int controlWidth);
	
}
