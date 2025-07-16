package redstone.multimeter.client.option;

import redstone.multimeter.client.gui.element.button.Button;
import redstone.multimeter.client.gui.element.button.Slider;
import redstone.multimeter.client.gui.element.button.TextField;
import redstone.multimeter.client.gui.text.Text;
import redstone.multimeter.client.gui.text.Texts;
import redstone.multimeter.client.gui.tooltip.Tooltips;

public class IntegerOption extends BaseOption<Integer> {

	protected final int min;
	protected final int max;
	protected final long range;

	public IntegerOption(String key, String legacyKey, int defaultValue, int minValue, int maxValue) {
		super(key, legacyKey, defaultValue);

		this.min = minValue;
		this.max = maxValue;
		this.range = (long)this.max - (long)this.min;
	}

	@Override
	public Text getDisplayValue() {
		return Texts.translatable(this.translationKey() + ".value", this.getAsString());
	}

	@Override
	public void set(Integer value) {
		if (value >= this.min && value <= this.max) {
			super.set(value);
		}
	}

	@Override
	public void setFromString(String value) {
		try {
			this.set(Integer.valueOf(value));
		} catch (NumberFormatException e) {
		}
	}

	@Override
	public Button createControl(int width, int height) {
		if (this.range > 1000) {
			return new TextField(0, 0, width, height, Tooltips::empty, this::setFromString, this::getAsString);
		}

		return new Slider(0, 0, width, height, this::getDisplayValue, Tooltips::empty, value -> {
			this.set(this.min + (int)Math.round(this.range * value));
		}, () -> {
			return (double)(this.get() - this.min) / this.range;
		}, this.range);
	}
}
