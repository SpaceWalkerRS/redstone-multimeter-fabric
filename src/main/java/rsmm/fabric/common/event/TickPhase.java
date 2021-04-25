package rsmm.fabric.common.event;

public enum TickPhase {
	UNKNOWN(0, "unknown"),
	TICK_CHUNKS(1, "tick chunks"),
	TICK_BLOCKS(2, "tick blocks"),
	TICK_FLUIDS(3, "tick fluids"),
	TICK_RAIDS(4, "tick raids"),
	BLOCK_EVENTS(5, "block events"),
	TICK_ENTITIES(6, "tick entities"),
	TICK_BLOCK_ENTITIES(7, "tick block entities"),
	HANDLE_PACKETS(8, "handle packets");
	
	public static final TickPhase[] PHASES;
	
	static {
		PHASES = new TickPhase[values().length + 1];
		
		for (TickPhase phase : values()) {
			PHASES[phase.index] = phase;
		}
	}
	
	private final int index;
	private final String name;
	
	private TickPhase(int index, String name) {
		this.index = index;
		this.name = name;
	}
	
	public int getIndex() {
		return index;
	}
	
	public static TickPhase fromIndex(int index) {
		if (index > 0 || index < PHASES.length) {
			return PHASES[index];
		}
		
		return UNKNOWN;
	}
	
	public String getName() {
		return name;
	}
}
