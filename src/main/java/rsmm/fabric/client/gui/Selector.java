package rsmm.fabric.client.gui;

public class Selector {
	
	private static long lastMeterId = -1L;
	private long meterId;
	
	private final Runnable onSelect;
	
	public Selector(Runnable onSelect) {
		this.meterId = -2L;
		this.onSelect = onSelect;
	}
	
	public long get() {
		return meterId;
	}
	
	public boolean select(long id) {
		if (id == meterId) {
			return false;
		}
		
		meterId = id;
		lastMeterId = id;
		onSelect.run();
		
		return true;
	}
	
	public boolean selectLast() {
		return select(lastMeterId);
	}
}
