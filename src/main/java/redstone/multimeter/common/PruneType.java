package redstone.multimeter.common;

public enum PruneType {

	NONE   (0),
	BRANCH (1),
	SIBLING(2),
	TREE   (3);

	private final int level;

	private PruneType(int level) {
		this.level = level;
	}

	public boolean is(PruneType type) {
		return level >= type.level;
	}
}
