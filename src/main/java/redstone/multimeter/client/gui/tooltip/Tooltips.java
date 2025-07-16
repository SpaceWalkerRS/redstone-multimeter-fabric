package redstone.multimeter.client.gui.tooltip;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import net.minecraft.client.options.KeyBinding;

import redstone.multimeter.client.gui.FontRenderer;
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

	public static Tooltip line(Text line) {
		return lines(Arrays.asList(line));
	}

	public static Tooltip line(String line) {
		return line(Texts.literal(line));
	}

	public static Tooltip line(String line, Object... args) {
		return line(Texts.literal(line, args));
	}

	public static Tooltip translatable(String line) {
		return line(Texts.translatable(line));
	}

	public static Tooltip translatable(String line, Object... args) {
		return line(Texts.translatable(line, args));
	}

	public static Tooltip split(FontRenderer font, String text) {
		return split(font, text, 200);
	}

	public static Tooltip split(FontRenderer font, String text, int width) {
		return lines(font.split(text, width));
	}

	public static Tooltip split(FontRenderer font, Text text) {
		return split(font, text, 200);
	}

	public static Tooltip split(FontRenderer font, Text text, int width) {
		return lines(font.split(text, width));
	}

	public static Tooltip keybind(KeyBinding keybind) {
		return keybind(Texts.translatable(keybind.name), keybind);
	}

	public static Tooltip keybind(KeyBinding keybind, Object... keys) {
		return keybind(Texts.translatable(keybind.name), keys);
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
