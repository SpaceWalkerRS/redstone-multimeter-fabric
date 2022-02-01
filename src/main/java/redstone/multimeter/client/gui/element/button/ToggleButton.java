package redstone.multimeter.client.gui.element.button;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;

import redstone.multimeter.client.MultimeterClient;
import redstone.multimeter.client.gui.Tooltip;

public class ToggleButton extends Button {
	
	public ToggleButton(MultimeterClient client, int x, int y, int width, int height, Supplier<Boolean> getter, Consumer<Button> toggle) {
		this(client, x, y, width, height, on -> new TextComponentString(String.valueOf(on)).setStyle(new Style().setColor(on ? TextFormatting.GREEN : TextFormatting.RED)), getter, toggle);
	}
	
	public ToggleButton(MultimeterClient client, int x, int y, int width, int height, Function<Boolean, ITextComponent> text, Supplier<Boolean> getter, Consumer<Button> toggle) {
		super(client, x, y, width, height, () -> text.apply(getter.get()), () -> Tooltip.EMPTY, button -> { toggle.accept(button); return true; });
	}
}
