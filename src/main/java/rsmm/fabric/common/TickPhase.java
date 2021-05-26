package rsmm.fabric.common;

public enum TickPhase {
	UNKNOWN(0, "unknown"),
	TICK_WEATHER(1, "tick weather"),
	TICK_CHUNKS(2, "tick chunks"),
	TICK_BLOCKS(3, "tick blocks"),
	TICK_FLUIDS(4, "tick fluids"),
	TICK_RAIDS(5, "tick raids"),
	PROCESS_BLOCK_EVENTS(6, "process block events"),
	TICK_ENTITIES(7, "tick entities"),
	TICK_BLOCK_ENTITIES(8, "tick block entities"),
	HANDLE_PACKETS(9, "handle packets");
	
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
		if (index > 0 && index < PHASES.length) {
			return PHASES[index];
		}
		
		return UNKNOWN;
	}
	
	public String getName() {
		return name;
	}
}
