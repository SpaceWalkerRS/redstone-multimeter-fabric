package redstone.multimeter.client.option;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.text.LiteralText;
import redstone.multimeter.client.MultimeterClient;
import redstone.multimeter.client.gui.widget.IButton;
import redstone.multimeter.client.gui.widget.Slider;
import redstone.multimeter.client.gui.widget.TextField;

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
			MinecraftClient minecraftClient = client.getMinecraftClient();
			TextRenderer font = minecraftClient.textRenderer;
			
			return new TextField(font, 0, 0, width, height, () -> {
				return get().toString();
			}, text -> {
				setFromString(text);
			});
		}
		
		return new Slider(0, 0, width, height, () -> {
			return new LiteralText(get().toString());
		}, () -> {
			return (double)(get() - min) / range;
		}, slider -> {
			set(min + (int)(range * slider.getValue()));
		}, fraction -> {
			return (double)(int)(range * fraction) / range;
		});
	}
}
