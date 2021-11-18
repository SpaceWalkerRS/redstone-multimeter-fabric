package redstone.multimeter.common;

public enum TickTask {
	
	UNKNOWN              ( 0, "unknown"),
	WORLD_BORDER         ( 1, "world border"),
	WEATHER              ( 2, "weather"),
	WAKE_SLEEPING_PLAYERS( 3, "wake sleeping players"),
	CHUNK_SOURCE         ( 4, "chunk source"),
	MOB_SPAWNING         ( 5, "mob spawning"),
	CUSTOM_MOB_SPAWNING  ( 6, "custom mob spawning"),
	TICK_CHUNK           ( 7, "tick chunk"),
	THUNDER              ( 8, "thunder"),
	PRECIPITATION        ( 9, "precipitation"),
	RANDOM_TICKS         (10, "random ticks"),
	SCHEDULED_TICKS      (11, "scheduled ticks"),
	BLOCK_TICKS          (12, "block ticks"),
	FLUID_TICKS          (13, "fluid ticks"),
	VILLAGES             (14, "villages"),
	RAIDS                (15, "raids"),
	PORTALS              (16, "portals"),
	BLOCK_EVENTS         (17, "block events"),
	ENTITIES             (18, "entities"),
	REGULAR_ENTITIES     (19, "regular entities"),
	GLOBAL_ENTITIES      (20, "global entities"),
	PLAYERS              (21, "players"),
	DRAGON_FIGHT         (22, "dragon fight"),
	BLOCK_ENTITIES       (23, "block entities"),
	PACKETS              (24, "packets"),
	COMMAND_FUNCTIONS    (25, "command functions");
	
	public static final TickTask[] ALL;
	
	static {
		ALL = new TickTask[values().length];
		
		for (TickTask phase : values()) {
			ALL[phase.index] = phase;
		}
	}
	
	private final int index;
	private final String name;
	
	private TickTask(int index, String name) {
		this.index = index;
		this.name = name;
	}
	
	public int getIndex() {
		return index;
	}
	
	public static TickTask fromIndex(int index) {
		if (index > 0 && index < ALL.length) {
			return ALL[index];
		}
		
		return UNKNOWN;
	}
	
	public String getName() {
		return name;
	}
}
