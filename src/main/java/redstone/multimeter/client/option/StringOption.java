package redstone.multimeter.client.option;

import redstone.multimeter.client.gui.element.button.Button;
import redstone.multimeter.client.gui.element.button.TextField;
import redstone.multimeter.client.gui.text.Text;
import redstone.multimeter.client.gui.text.Texts;
import redstone.multimeter.client.gui.tooltip.Tooltips;

public class StringOption extends BaseOption<String> {

	protected final int maxLength;

	public StringOption(String key, String legacyKey, String defaultValue, int maxLength) {
		super(key, legacyKey, defaultValue);

		this.maxLength = maxLength;
	}

	@Override
	public Text getDisplayValue() {
		return Texts.translatable(this.translationKey() + ".value", this.getAsString());
	}

	@Override
	public void setFromString(String value) {
		this.set(value);
	}

	@Override
	public Button createControl(int width, int height) {
		TextField textField = new TextField(0, 0, width, height, Tooltips::empty, this::set, this::get);
		textField.setMaxLength(this.maxLength);

		return textField;
	}

	@Override
	public void set(String value) {
		if (value.length() <= this.maxLength) {
			super.set(value);
		}
	}

	public int getMaxLength() {
		return this.maxLength;
	}
}
