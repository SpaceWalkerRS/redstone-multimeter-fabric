package redstone.multimeter.client.gui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.minecraft.client.render.TextRenderer;

public class Tooltip {

	public static final Tooltip EMPTY = new Tooltip();

	private final List<String> lines;

	public Tooltip(String... lines) {
		this.lines = new ArrayList<>();

		if (lines != null && lines.length > 0) {
			for (String line : lines) {
				this.lines.add(line);
			}
		}
	}

	public boolean isEmpty() {
		return this == EMPTY || lines.isEmpty();
	}

	public List<String> getLines() {
		return Collections.unmodifiableList(lines);
	}

	public Tooltip add(String line) {
		if (this == EMPTY) {
			throw new UnsupportedOperationException("cannot add more lines to the EMPTY tooltip!");
		}
		lines.add(line);
		return this;
	}

	public int getWidth(TextRenderer textRenderer) {
		int width = 0;

		for (int index = 0; index < lines.size(); index++) {
			String text = lines.get(index);
			int lineWidth = textRenderer.getWidth(text);

			if (lineWidth > width) {
				width = lineWidth;
			}
		}

		return width;
	}

	public int getHeight(TextRenderer textRenderer) {
		return (lines.size() - 1) * (textRenderer.fontHeight + 1) + textRenderer.fontHeight;
	}

	public static Tooltip of(String... lines) {
		if (lines == null || lines.length == 0) {
			return EMPTY;
		}

		return new Tooltip(lines);
	}

	public static Tooltip of(List<String> text) {
		if (text == null || text.isEmpty()) {
			return EMPTY;
		}

		String[] lines = new String[text.size()];

		for (int index = 0; index < text.size(); index++) {
			lines[index] = text.get(index);
		}

		return new Tooltip(lines);
	}
}
