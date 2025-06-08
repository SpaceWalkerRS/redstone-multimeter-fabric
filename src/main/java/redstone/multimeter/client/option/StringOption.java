package redstone.multimeter.client.option;

import redstone.multimeter.client.MultimeterClient;
import redstone.multimeter.client.gui.Tooltip;
import redstone.multimeter.client.gui.element.button.IButton;
import redstone.multimeter.client.gui.element.button.SuggestionsProvider;
import redstone.multimeter.client.gui.element.button.TextField;

public class StringOption extends Option<String> {

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
	public IButton createControl(MultimeterClient client, int width, int height) {
		TextField textField = new TextField(client, 0, 0, width, height, () -> Tooltip.EMPTY, text -> set(text), () -> get(), SuggestionsProvider.none());
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
