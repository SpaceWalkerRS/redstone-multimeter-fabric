package redstone.multimeter.client.option;

import redstone.multimeter.client.gui.element.button.Button;
import redstone.multimeter.client.gui.element.button.TextField;
import redstone.multimeter.client.gui.tooltip.Tooltips;

public class StringOption extends BaseOption<String> {

	protected final int maxLength;

	public StringOption(String name, String description, String defaultValue, int maxLength) {
		super(name, description, defaultValue);

		this.maxLength = maxLength;
	}

	@Override
	public void setFromString(String value) {
		set(value);
	}

	@Override
	public Button createControl(int width, int height) {
		TextField textField = new TextField(0, 0, width, height, Tooltips::empty, text -> set(text), () -> get());
		textField.setMaxLength(maxLength);

		return textField;
	}

	@Override
	public void set(String value) {
		if (value.length() <= maxLength) {
			super.set(value);
		}
	}

	public int getMaxLength() {
		return maxLength;
	}
}
