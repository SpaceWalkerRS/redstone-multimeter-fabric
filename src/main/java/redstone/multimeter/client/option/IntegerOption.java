package redstone.multimeter.client.option;

import net.minecraft.text.LiteralText;

import redstone.multimeter.client.MultimeterClient;
import redstone.multimeter.client.gui.Tooltip;
import redstone.multimeter.client.gui.element.button.IButton;
import redstone.multimeter.client.gui.element.button.Slider;
import redstone.multimeter.client.gui.element.button.TextField;

public class IntegerOption extends Option<Integer> {

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
	public IButton createControl(MultimeterClient client, int width, int height) {
		if (range > 1000) {
			return new TextField(client, 0, 0, width, height, () -> Tooltip.EMPTY, text -> {
				setFromString(text);
			}, () -> {
				return get().toString();
			});
		}

		return new Slider(client, 0, 0, width, height, () -> {
			return new LiteralText(get().toString());
		}, () -> Tooltip.EMPTY, value -> {
			set(min + (int)Math.round(range * value));
		}, () -> {
			return (double)(get() - min) / range;
		}, range);
	}
}
