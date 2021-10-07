package rsmm.fabric.client.gui.element.meter;

import rsmm.fabric.client.MultimeterClient;
import rsmm.fabric.client.gui.element.TextElement;
import rsmm.fabric.client.gui.element.controls.ControlsSectionElement;

public class MeterControlsCategory extends ControlsSectionElement {
	
	public MeterControlsCategory(MultimeterClient client, int width, int margin, int midpoint, TextElement category) {
		super(client, width, margin, midpoint, category);
	}
	
	@Override
	public void focus() {
		
	}
	
	@Override
	protected void onChangedX(int x) {
		category.setX(x + 2);
		controls.setX(x + margin);
	}
	
	@Override
	protected void onChangedY(int y) {
		category.setY(y + 6);
		controls.setY(y);
	}
}
