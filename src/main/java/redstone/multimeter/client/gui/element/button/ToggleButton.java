package redstone.multimeter.client.gui.element.button;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import net.minecraft.text.Formatting;
import net.minecraft.text.Text;

import redstone.multimeter.client.MultimeterClient;
import redstone.multimeter.client.gui.Tooltip;

public class ToggleButton extends Button {

	public ToggleButton(MultimeterClient client, int x, int y, int width, int height, Supplier<Boolean> getter, Consumer<Button> toggle) {
		this(client, x, y, width, height, on -> Text.literal(String.valueOf(on)).setFormatting(on ? Formatting.GREEN : Formatting.RED), getter, toggle);
	}

	public ToggleButton(MultimeterClient client, int x, int y, int width, int height, Function<Boolean, Text> text, Supplier<Boolean> getter, Consumer<Button> toggle) {
		super(client, x, y, width, height, () -> text.apply(getter.get()), () -> Tooltip.EMPTY, button -> { toggle.accept(button); return true; });
	}
}
