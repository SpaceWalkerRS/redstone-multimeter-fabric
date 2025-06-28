package redstone.multimeter.client.option;

import redstone.multimeter.client.gui.element.button.Button;
import redstone.multimeter.client.gui.element.button.Slider;
import redstone.multimeter.client.gui.element.button.TextField;
import redstone.multimeter.client.gui.text.Texts;
import redstone.multimeter.client.gui.tooltip.Tooltips;

public class IntegerOption extends BaseOption<Integer> {

	protected final int min;
	protected final int max;
	protected final long range;

	public IntegerOption(String name, String description, int defaultValue, int minValue, int maxValue) {
		super(name, description, defaultValue);

		this.min = minValue;
		this.max = maxValue;
		this.range = (long)this.max - (long)this.min;
	}

	@Override
	public void set(Integer value) {
		if (value >= min && value <= max) {
			super.set(value);
		}
	}

	@Override
	public void setFromString(String value) {
		try {
			set(Integer.valueOf(value));
		} catch (NumberFormatException e) {

		}
	}

	@Override
	public Button createControl(int width, int height) {
		if (range > 1000) {
			return new TextField(0, 0, width, height, Tooltips::empty, text -> {
				setFromString(text);
			}, () -> {
				return get().toString();
			});
		}

		return new Slider(0, 0, width, height, () -> {
			return Texts.literal(get().toString());
		}, Tooltips::empty, value -> {
			set(min + (int)Math.round(range * value));
		}, () -> {
			return (double)(get() - min) / range;
		}, range);
	}
}
