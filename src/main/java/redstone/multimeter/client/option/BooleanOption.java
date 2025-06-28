package redstone.multimeter.client.option;

import redstone.multimeter.client.gui.element.button.Button;
import redstone.multimeter.client.gui.element.button.ToggleButton;

public class BooleanOption extends BaseOption<Boolean> {

	public BooleanOption(String name, String description, Boolean defaultValue) {
		super(name, description, defaultValue);
	}

	@Override
	public void setFromString(String value) {
		set(Boolean.valueOf(value));
	}

	@Override
	public Button createControl(int width, int height) {
		return new ToggleButton(0, 0, width, height, () -> get(), button -> toggle());
	}

	public void toggle() {
		set(!get());
	}
}
