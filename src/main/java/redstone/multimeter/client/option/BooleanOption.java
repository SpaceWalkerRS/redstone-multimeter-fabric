package redstone.multimeter.client.option;

import redstone.multimeter.client.MultimeterClient;
import redstone.multimeter.client.gui.element.button.IButton;
import redstone.multimeter.client.gui.element.button.ToggleButton;

public class BooleanOption extends Option<Boolean> {

	public BooleanOption(String name, String description, Boolean defaultValue) {
		super(name, description, defaultValue);
	}

	@Override
	public void setFromString(String value) {
		set(Boolean.valueOf(value));
	}

	@Override
	public IButton createControl(MultimeterClient client, int width, int height) {
		return new ToggleButton(client, 0, 0, width, height, () -> get(), button -> toggle());
	}

	public void toggle() {
		set(!get());
	}
}
