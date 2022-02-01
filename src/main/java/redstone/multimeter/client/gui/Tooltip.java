package redstone.multimeter.client.gui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
	
	public void add(String line) {
		add(new LiteralText(line));
	}
	
	public void add(Text line) {
		if (this == EMPTY) {
			throw new UnsupportedOperationException("cannot add more lines to the EMPTY tooltip!");
		}
		
		lines.add(line);
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
	
	public static Tooltip of(List<Text> texts) {
		if (texts == null || texts.isEmpty()) {
			return EMPTY;
		}
		
		Text[] lines = new Text[texts.size()];
		
		for (int index = 0; index < texts.size(); index++) {
			lines[index] = texts.get(index);
		}
		
		return new Tooltip(lines);
	}
}
