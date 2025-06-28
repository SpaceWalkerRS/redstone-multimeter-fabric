package redstone.multimeter.client.gui.tooltip;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

import redstone.multimeter.client.gui.text.Text;
import redstone.multimeter.client.gui.text.Texts;

public class TooltipBuilder {

	private final List<Text> lines = new ArrayList<>();

	public TooltipBuilder line(Text line) {
		this.lines.add(line);
		return this;
	}

	public TooltipBuilder line(String line) {
		this.lines.add(Texts.literal(line));
		return this;
	}

	public TooltipBuilder lines(Collection<Text> lines) {
		this.lines.addAll(lines);
		return this;
	}

	public TooltipBuilder lines(Consumer<TooltipBuilder> builder) {
		builder.accept(this);
		return this;
	}

	public Tooltip build() {
		return this.lines.isEmpty() ? Tooltip.EMPTY : new Tooltip(this.lines);
	}
}
