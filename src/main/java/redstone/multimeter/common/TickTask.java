package redstone.multimeter.common;

import redstone.multimeter.client.gui.text.Text;
import redstone.multimeter.client.gui.text.Texts;

public enum TickTask {

	UNKNOWN              (-1, "unknown"             , PruneType.NONE),
	RUN_LOOP             ( 0, "runLoop"             , PruneType.TREE),
	TICK                 ( 1, "tick"                , PruneType.TREE),
	COMMAND_FUNCTIONS    ( 2, "commandFunctions"    , PruneType.TREE),
	LEVELS               ( 3, "levels"              , PruneType.TREE),
	TICK_LEVEL           ( 4, "tickLevel"           , PruneType.BRANCH),
	WORLD_BORDER         ( 5, "worldBorder"         , PruneType.BRANCH),
	WEATHER              ( 6, "weather"             , PruneType.BRANCH),
	WAKE_SLEEPING_PLAYERS( 7, "wakeSleepingPlayers" , PruneType.BRANCH),
	CHUNK_SOURCE         ( 8, "chunkSource"         , PruneType.BRANCH),
	PURGE_UNLOADED_CHUNKS( 9, "purgeUnloadedChunks" , PruneType.BRANCH),
	TICK_CHUNKS          (10, "tickChunks"          , PruneType.BRANCH),
	MOB_SPAWNING         (11, "mobSpawning"         , PruneType.SIBLING),
	TICK_CHUNK           (12, "tickChunk"           , PruneType.SIBLING),
	THUNDER              (13, "thunder"             , PruneType.SIBLING),
	PRECIPITATION        (14, "precipitation"       , PruneType.SIBLING),
	RANDOM_TICKS         (15, "randomTicks"         , PruneType.SIBLING),
	CUSTOM_MOB_SPAWNING  (16, "customMobSpawning"   , PruneType.BRANCH),
	BROADCAST_CHUNKS     (17, "broadcastChunks"     , PruneType.SIBLING),
	UNLOAD_CHUNKS        (18, "unloadChunks"        , PruneType.BRANCH),
	CHUNK_MAP            (19, "chunkMap"            , PruneType.BRANCH),
	TICK_TIME            (20, "tickTime"            , PruneType.BRANCH),
	SCHEDULED_TICKS      (21, "scheduledTicks"      , PruneType.BRANCH),
	BLOCK_TICKS          (22, "blockTicks"          , PruneType.BRANCH),
	FLUID_TICKS          (23, "fluidTicks"          , PruneType.BRANCH),
	VILLAGES             (24, "villages"            , PruneType.BRANCH),
	RAIDS                (25, "raids"               , PruneType.BRANCH),
	PORTALS              (26, "portals"             , PruneType.BRANCH),
	BLOCK_EVENTS         (27, "blockEvents"         , PruneType.BRANCH),
	ENTITIES             (28, "entities"            , PruneType.BRANCH),
	REGULAR_ENTITIES     (29, "regularEntities"     , PruneType.BRANCH),
	GLOBAL_ENTITIES      (30, "globalEntities"      , PruneType.BRANCH),
	PLAYERS              (31, "players"             , PruneType.BRANCH),
	DRAGON_FIGHT         (32, "dragonFight"         , PruneType.BRANCH),
	BLOCK_ENTITIES       (33, "blockEntities"       , PruneType.BRANCH),
	ENTITY_MANAGEMENT    (34, "entityManagement"    , PruneType.BRANCH),
	CONNECTIONS          (35, "connections"         , PruneType.TREE),
	PLAYER_PING          (36, "playerPing"          , PruneType.TREE),
	SERVER_GUI           (37, "serverGui"           , PruneType.TREE),
	AUTOSAVE             (38, "autoSave"            , PruneType.TREE),
	PACKETS              (39, "packets"             , PruneType.TREE);

	public static final TickTask[] ALL;

	static {

		ALL = new TickTask[values().length];

		for (TickTask task : values()) {
			if (task != UNKNOWN) {
				ALL[task.id] = task;
			}
		}
	}

	private final int id;
	private final String key;
	private final PruneType pruneType;

	private TickTask(int id, String key, PruneType pruneType) {
		this.id = id;
		this.key = key;
		this.pruneType = pruneType;
	}

	public int getId() {
		return this.id;
	}

	public static TickTask byId(int id) {
		if (id > 0 && id < ALL.length) {
			return ALL[id];
		}

		return UNKNOWN;
	}

	public Text getName() {
		return Texts.translatable("rsmm.tickTask." + this.key);
	}

	public PruneType getPruneType() {
		return this.pruneType;
	}
}
