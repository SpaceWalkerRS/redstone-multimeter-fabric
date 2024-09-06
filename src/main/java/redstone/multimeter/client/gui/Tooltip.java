package redstone.multimeter.client.gui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.minecraft.client.render.TextRenderer;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;

public class Tooltip {

	public static final Tooltip EMPTY = new Tooltip();

	private final List<Text> lines;

	public Tooltip(Text... lines) {
		this.lines = new ArrayList<>();

		if (lines != null && lines.length > 0) {
			for (Text line : lines) {
				this.lines.add(line);
			}
		}
	}

	public boolean isEmpty() {
		return this == EMPTY || lines.isEmpty();
	}

	public List<Text> getLines() {
		return Collections.unmodifiableList(lines);
	}

	public Tooltip add(String line) {
		add(new LiteralText(line));
		return this;
	}

	public Tooltip add(Text line) {
		if (this == EMPTY) {
			throw new UnsupportedOperationException("cannot add more lines to the EMPTY tooltip!");
		}
		lines.add(line);
		return this;
	}

	public int getWidth(TextRenderer textRenderer) {
		int width = 0;

		for (int index = 0; index < lines.size(); index++) {
			Text text = lines.get(index);
			int lineWidth = textRenderer.getWidth(text.getFormattedString());

			if (lineWidth > width) {
				width = lineWidth;
			}
		}

		return width;
	}

	public int getHeight(TextRenderer textRenderer) {
		return (lines.size() - 1) * (textRenderer.fontHeight + 1) + textRenderer.fontHeight;
	}

	public static Tooltip of(String... strings) {
		if (strings == null || strings.length == 0) {
			return EMPTY;
		}

		Text[] lines = new Text[strings.length];

		for (int index = 0; index < strings.length; index++) {
			lines[index] = new LiteralText(strings[index]);
		}

		return new Tooltip(lines);
	}

	public static Tooltip of(Text... lines) {
		if (lines == null || lines.length == 0) {
			return EMPTY;
		}

		return new Tooltip(lines);
	}

	public static Tooltip of(List<Text> text) {
		if (text == null || text.isEmpty()) {
			return EMPTY;
		}

		Text[] lines = new Text[text.size()];

		for (int index = 0; index < text.size(); index++) {
			lines[index] = text.get(index);
		}

		return new Tooltip(lines);
	}
}
