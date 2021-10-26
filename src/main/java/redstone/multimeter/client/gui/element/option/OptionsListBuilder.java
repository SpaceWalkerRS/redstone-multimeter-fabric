package redstone.multimeter.client.gui.element.option;

import java.util.Collection;

import net.minecraft.text.Text;

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
		Text name = option.getDisplayName();
		int width = font.getWidth(name);
		
		if (width > midpoint) {
			midpoint = width;
		}
	}
}
