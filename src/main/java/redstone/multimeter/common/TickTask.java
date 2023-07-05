package redstone.multimeter.common;

public enum TickTask {

	UNKNOWN              ( 0, "unknown"              , PruneType.NONE),
	TICK                 ( 1, "tick"                 , PruneType.TREE),
	COMMAND_FUNCTIONS    ( 2, "command functions"    , PruneType.TREE),
	LEVELS               ( 3, "levels"               , PruneType.TREE),
	TICK_LEVEL           ( 4, "tick level"           , PruneType.BRANCH),
	WORLD_BORDER         ( 5, "world border"         , PruneType.BRANCH),
	WEATHER              ( 6, "weather"              , PruneType.BRANCH),
	WAKE_SLEEPING_PLAYERS( 7, "wake sleeping players", PruneType.BRANCH),
	CHUNK_SOURCE         ( 8, "chunk source"         , PruneType.BRANCH),
	PURGE_UNLOADED_CHUNKS( 9, "purge unloaded chunks", PruneType.BRANCH),
	TICK_CHUNKS          (10, "tick chunks"          , PruneType.BRANCH),
	MOB_SPAWNING         (11, "mob spawning"         , PruneType.SIBLING),
	TICK_CHUNK           (12, "tick chunk"           , PruneType.SIBLING),
	THUNDER              (13, "thunder"              , PruneType.SIBLING),
	PRECIPITATION        (14, "precipitation"        , PruneType.SIBLING),
	RANDOM_TICKS         (15, "random ticks"         , PruneType.SIBLING),
	CUSTOM_MOB_SPAWNING  (16, "custom mob spawning"  , PruneType.BRANCH),
	BROADCAST_CHUNKS     (17, "broadcast chunks"     , PruneType.SIBLING),
	UNLOAD_CHUNKS        (18, "unload chunks"        , PruneType.BRANCH),
	CHUNK_MAP            (19, "chunk map"            , PruneType.BRANCH),
	TICK_TIME            (20, "tick time"            , PruneType.BRANCH),
	SCHEDULED_TICKS      (21, "scheduled ticks"      , PruneType.BRANCH),
	BLOCK_TICKS          (22, "block ticks"          , PruneType.BRANCH),
	FLUID_TICKS          (23, "fluid ticks"          , PruneType.BRANCH),
	VILLAGES             (24, "villages"             , PruneType.BRANCH),
	RAIDS                (25, "raids"                , PruneType.BRANCH),
	PORTALS              (26, "portals"              , PruneType.BRANCH),
	BLOCK_EVENTS         (27, "block events"         , PruneType.BRANCH),
	ENTITIES             (28, "entities"             , PruneType.BRANCH),
	REGULAR_ENTITIES     (29, "regular entities"     , PruneType.BRANCH),
	GLOBAL_ENTITIES      (30, "global entities"      , PruneType.BRANCH),
	PLAYERS              (31, "players"              , PruneType.BRANCH),
	DRAGON_FIGHT         (32, "dragon fight"         , PruneType.BRANCH),
	BLOCK_ENTITIES       (33, "block entities"       , PruneType.BRANCH),
	ENTITY_MANAGEMENT    (34, "entity management"    , PruneType.BRANCH),
	CONNECTIONS          (35, "connections"          , PruneType.TREE),
	PLAYER_PING          (36, "player ping"          , PruneType.TREE),
	SERVER_GUI           (37, "server gui"           , PruneType.TREE),
	AUTOSAVE             (38, "autosave"             , PruneType.TREE),
	PACKETS              (39, "packets"              , PruneType.TREE);

	public static final TickTask[] ALL;

	static {

		ALL = new TickTask[values().length];

		for (TickTask task : values()) {
			ALL[task.index] = task;
		}
	}

	private final int index;
	private final String name;
	private final PruneType pruneType;

	private TickTask(int index, String name, PruneType pruneType) {
		this.index = index;
		this.name = name;
		this.pruneType = pruneType;
	}

	public int getIndex() {
		return index;
	}

	public static TickTask byIndex(int index) {
		if (index > 0 && index < ALL.length) {
			return ALL[index];
		}

		return UNKNOWN;
	}

	public String getName() {
		return name;
	}

	public PruneType getPruneType() {
		return pruneType;
	}
}
