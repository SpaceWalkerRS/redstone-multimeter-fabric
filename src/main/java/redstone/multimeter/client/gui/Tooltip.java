package redstone.multimeter.client.gui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;

public class Tooltip {
	
	public static final Tooltip EMPTY = new Tooltip();
	
	private final List<ITextComponent> lines;
	
	public Tooltip(ITextComponent... lines) {
		this.lines = new ArrayList<>();
		
		if (lines != null && lines.length > 0) {
			for (ITextComponent line : lines) {
				this.lines.add(line);
			}
		}
	}
	
	public boolean isEmpty() {
		return this == EMPTY || lines.isEmpty();
	}
	
	public List<ITextComponent> getLines() {
		return Collections.unmodifiableList(lines);
	}
	
	public void add(String line) {
		add(new TextComponentString(line));
	}
	
	public void add(ITextComponent line) {
		if (this == EMPTY) {
			throw new UnsupportedOperationException("cannot add more lines to the EMPTY tooltip!");
		}
		
		lines.add(line);
	}
	
	public static Tooltip of(String... strings) {
		if (strings == null || strings.length == 0) {
			return EMPTY;
		}
		
		ITextComponent[] lines = new ITextComponent[strings.length];
		
		for (int index = 0; index < strings.length; index++) {
			lines[index] = new TextComponentString(strings[index]);
		}
		
		return new Tooltip(lines);
	}
	
	public static Tooltip of(ITextComponent... lines) {
		if (lines == null || lines.length == 0) {
			return EMPTY;
		}
		
		return new Tooltip(lines);
	}
	
	public static Tooltip of(List<ITextComponent> texts) {
		if (texts == null || texts.isEmpty()) {
			return EMPTY;
		}
		
		ITextComponent[] lines = new ITextComponent[texts.size()];
		
		for (int index = 0; index < texts.size(); index++) {
			lines[index] = texts.get(index);
		}
		
		return new Tooltip(lines);
	}
}
