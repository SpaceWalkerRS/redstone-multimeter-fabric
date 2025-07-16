package redstone.multimeter.client.option;

import redstone.multimeter.client.gui.element.button.Button;
import redstone.multimeter.client.gui.element.button.ToggleButton;
import redstone.multimeter.client.gui.text.Text;
import redstone.multimeter.client.gui.text.Texts;

public class BooleanOption extends BaseOption<Boolean> {

	public BooleanOption(String key, String legacyKey, Boolean defaultValue) {
		super(key, legacyKey, defaultValue);
	}

	@Override
	public Text getDisplayValue() {
		return Texts.translatable("rsmm.option.value." + this.getAsString());
	}

	@Override
	public void setFromString(String value) {
		this.set(Boolean.valueOf(value));
	}

	@Override
	public Button createControl(int width, int height) {
		return new ToggleButton(0, 0, width, height, this::get, button -> this.toggle());
	}

	public void toggle() {
		this.set(!this.get());
	}
}
