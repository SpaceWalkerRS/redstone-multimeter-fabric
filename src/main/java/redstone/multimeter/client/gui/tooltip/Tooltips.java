package redstone.multimeter.client.gui.tooltip;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import net.minecraft.client.KeyMapping;

import redstone.multimeter.client.MultimeterClient;
import redstone.multimeter.client.gui.text.Text;
import redstone.multimeter.client.gui.text.Texts;

public class Tooltips {

	public static final Tooltip EMPTY = Tooltip.EMPTY;

	public static Tooltip empty() {
		return EMPTY;
	}

	public static Tooltip lines(Collection<Text> lines) {
		return lines.isEmpty() ? Tooltip.EMPTY : new Tooltip(new ArrayList<>(lines));
	}

	public static Tooltip lines(List<String> lines) {
		return lines(lines.stream().map(Texts::literal).collect(Collectors.toList()));
	}

	public static Tooltip lines(Object... lines) {
		return lines(Stream.of(lines).map(Texts::of).collect(Collectors.toList()));
	}

	public static Tooltip split(Text text) {
		return split(text, 200);
	}

	public static Tooltip split(Text text, int width) {
		return lines(MultimeterClient.INSTANCE.getFontRenderer().split(text, width));
	}

	public static Tooltip literal(String text) {
		return literal(text, 200);
	}

	public static Tooltip literal(String text, int width) {
		return split(Texts.literal(text), width);
	}

	public static Tooltip translatable(String text) {
		return translatable(text, 200);
	}

	public static Tooltip translatable(String text, int width) {
		return split(Texts.translatable(text), width);
	}

	public static Tooltip keybind(KeyMapping keybind) {
		return keybind(Texts.translatable(keybind.getName()), keybind);
	}

	public static Tooltip keybind(KeyMapping keybind, Object... keys) {
		return keybind(Texts.translatable(keybind.getName()), keys);
	}

	public static Tooltip keybind(Text keybind, Object... keys) {
		return lines(
			keybind,
			Texts.keyValue(
				Texts.translatable("rsmm.keybind"),
				Texts.keybinds(keys)
			)
		);
	}

	public static TooltipBuilder builder() {
		return new TooltipBuilder();
	}
}
