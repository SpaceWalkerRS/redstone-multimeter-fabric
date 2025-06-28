package redstone.multimeter.client.gui.tooltip;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import redstone.multimeter.client.gui.text.Text;

public class Tooltip implements Iterable<Text> {

	static final Tooltip EMPTY = new Tooltip(Collections.emptyList());

	private final List<Text> lines;

	Tooltip(List<Text> lines) {
		this.lines = lines;
	}

	@Override
	public Iterator<Text> iterator() {
		return this.lines.iterator();
	}

	public boolean isEmpty() {
		return this == EMPTY;
	}
}
