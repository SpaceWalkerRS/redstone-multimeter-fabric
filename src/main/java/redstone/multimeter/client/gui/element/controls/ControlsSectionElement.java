package redstone.multimeter.client.gui.element.controls;

import redstone.multimeter.client.MultimeterClient;
import redstone.multimeter.client.gui.element.AbstractParentElement;
import redstone.multimeter.client.gui.element.SimpleListElement;
import redstone.multimeter.client.gui.element.TextElement;

public class ControlsSectionElement extends AbstractParentElement {
	
	protected final MultimeterClient client;
	protected final TextElement category;
	protected final SimpleListElement controls;
	protected final int margin;
	protected final int midPoint;
	
	public ControlsSectionElement(MultimeterClient client, int width, int margin, int midpoint, TextElement category) {
		super(0, 0, width, 0);
		
		this.client = client;
		this.category = category;
		this.controls = new SimpleListElement(client, width - margin);
		this.margin = margin;
		this.midPoint = midpoint;
		
		addChild(this.category);
		addChild(this.controls);
	}
	
	@Override
	public void focus() {
		
	}
	
	@Override
	public int getHeight() {
		return controls.getY() + controls.getHeight() - getY();
	}
	
	@Override
	protected void onChangedX(int x) {
		category.setX(x + margin + midPoint - (category.getWidth() / 2));
		controls.setX(x + margin);
	}
	
	@Override
	protected void onChangedY(int y) {
		category.setY(y);
		controls.setY(y + category.getHeight() + 6);
	}
	
	public void addControl(ControlElement control) {
		controls.add(control);
	}
}
