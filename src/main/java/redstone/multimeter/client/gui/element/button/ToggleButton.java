package redstone.multimeter.client.gui.element.button;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import redstone.multimeter.client.MultimeterClient;
import redstone.multimeter.client.gui.Tooltip;
import redstone.multimeter.util.Formatting;

public class ToggleButton extends Button {

	public ToggleButton(MultimeterClient client, int x, int y, int width, int height, Supplier<Boolean> getter, Consumer<Button> toggle) {
		this(client, x, y, width, height, on -> (on ? Formatting.GREEN : Formatting.RED) + String.valueOf(on), getter, toggle);
	}

	public ToggleButton(MultimeterClient client, int x, int y, int width, int height, Function<Boolean, String> text, Supplier<Boolean> getter, Consumer<Button> toggle) {
		super(client, x, y, width, height, () -> text.apply(getter.get()), () -> Tooltip.EMPTY, button -> { toggle.accept(button); return true; });
	}
}
