package redstone.multimeter.client.gui.element.button;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import redstone.multimeter.client.gui.text.Formatting;
import redstone.multimeter.client.gui.text.Text;
import redstone.multimeter.client.gui.text.Texts;
import redstone.multimeter.client.gui.tooltip.Tooltips;

public class ToggleButton extends BasicButton {

	public ToggleButton(int x, int y, int width, int height, Supplier<Boolean> getter, Consumer<BasicButton> toggle) {
		this(x, y, width, height, on -> Texts.of(on).format(on ? Formatting.GREEN : Formatting.RED), getter, toggle);
	}

	public ToggleButton(int x, int y, int width, int height, Function<Boolean, Text> text, Supplier<Boolean> getter, Consumer<BasicButton> toggle) {
		super(x, y, width, height, () -> text.apply(getter.get()), Tooltips::empty, (button, event) -> { toggle.accept(button); return true; });
	}
}
