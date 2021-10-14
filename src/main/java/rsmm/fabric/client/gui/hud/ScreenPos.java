package rsmm.fabric.client.gui.hud;

import rsmm.fabric.client.option.Cyclable;

public enum ScreenPos implements Cyclable<ScreenPos> {
	
	TOP_LEFT(0, "Top Left"),
	TOP_RIGHT(1, "Top Right");
	
	private static final ScreenPos[] ALL;
	
	static {
		ALL = new ScreenPos[values().length];
		
		for (ScreenPos pos : values()) {
			ALL[pos.index] = pos;
		}
	}
	
	private final int index;
	private final String name;
	
	private ScreenPos(int index, String name) {
		this.index = index;
		this.name = name;
	}
	
	@Override
	public String getName() {
		return name;
	}
	
	@Override
	public ScreenPos next() {
		return fromIndex(index + 1);
	}
	
	@Override
	public ScreenPos prev() {
		return fromIndex(index - 1);
	}
	
	public int getIndex() {
		return index;
	}
	
	public static ScreenPos fromIndex(int index) {
		if (index < 0) {
			index = ALL.length - 1;
		}
		if (index >= ALL.length) {
			index = 0;
		}
		
		return ALL[index];
	}
}
