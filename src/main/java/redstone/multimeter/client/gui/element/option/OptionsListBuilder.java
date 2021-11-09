package redstone.multimeter.client.gui.element.option;

import java.util.Collection;

import redstone.multimeter.client.MultimeterClient;
import redstone.multimeter.client.gui.element.controls.ControlsListBuilder;
import redstone.multimeter.client.option.IOption;

public class OptionsListBuilder extends ControlsListBuilder {
	
	public OptionsListBuilder(MultimeterClient client, int width) {
		super(client, width);
	}
	
	public void addOption(String category, IOption option) {
		addControl(category, (client, midpoint, controlWidth) -> new OptionElement(client, midpoint, controlWidth, option));
		updateMidPoint(option);
	}
	
	public void addOptions(String category, Collection<IOption> options) {
		for (IOption option : options) {
			addOption(category, option);
		}
	}
	
	private void updateMidPoint(IOption option) {
		int width = font.getWidth(option.getDisplayName());
		
		if (width > midpoint) {
			midpoint = width;
		}
	}
}
