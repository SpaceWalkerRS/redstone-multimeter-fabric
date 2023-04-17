package redstone.multimeter.client.gui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;

public class Tooltip {

	public static final Tooltip EMPTY = new Tooltip();

	private final List<Component> lines;

	public Tooltip(Component... lines) {
		this.lines = new ArrayList<>();

		if (lines != null && lines.length > 0) {
			for (Component line : lines) {
				this.lines.add(line);
			}
		}
	}

	public boolean isEmpty() {
		return this == EMPTY || lines.isEmpty();
	}

	public List<Component> getLines() {
		return Collections.unmodifiableList(lines);
	}

	public Tooltip add(String line) {
		add(new TextComponent(line));
		return this;
	}

	public Tooltip add(Component line) {
		if (this == EMPTY) {
			throw new UnsupportedOperationException("cannot add more lines to the EMPTY tooltip!");
		}
		lines.add(line);
		return this;
	}

	public static Tooltip of(String... strings) {
		if (strings == null || strings.length == 0) {
			return EMPTY;
		}

		Component[] lines = new Component[strings.length];

		for (int index = 0; index < strings.length; index++) {
			lines[index] = new TextComponent(strings[index]);
		}

		return new Tooltip(lines);
	}

	public static Tooltip of(Component... lines) {
		if (lines == null || lines.length == 0) {
			return EMPTY;
		}

		return new Tooltip(lines);
	}

	public static Tooltip of(List<Component> components) {
		if (components == null || components.isEmpty()) {
			return EMPTY;
		}

		Component[] lines = new Component[components.size()];

		for (int index = 0; index < components.size(); index++) {
			lines[index] = components.get(index);
		}

		return new Tooltip(lines);
	}
}
