package redstone.multimeter.util;

public enum Direction {

	DOWN (Axis.Y, 0, 1,  0, -1,  0,  "down"),
	UP   (Axis.Y, 1, 0,  0,  1,  0,    "up"),
	NORTH(Axis.Z, 2, 3,  0,  0, -1, "north"),
	SOUTH(Axis.Z, 3, 2,  0,  0,  1, "south"),
	WEST (Axis.X, 4, 5, -1,  0,  0,  "west"),
	EAST (Axis.X, 5, 4,  1,  0,  0,  "east");

	public static final Direction[] ALL;

	static {

		ALL = new Direction[values().length];

		for (Direction dir : values()) {
			ALL[dir.index] = dir;
		}
	}

	private final Axis axis;
	private final int index;
	private final int opposite;
	private final int offsetX;
	private final int offsetY;
	private final int offsetZ;
	private final String name;

	private Direction(Axis axis, int index, int opposite, int offsetX, int offsetY, int offsetZ, String name) {
		this.axis = axis;
		this.index = index;
		this.opposite = opposite;
		this.offsetX = offsetX;
		this.offsetY = offsetY;
		this.offsetZ = offsetZ;
		this.name = name;
	}

	public Axis getAxis() {
		return axis;
	}

	public int getIndex() {
		return index;
	}

	public static Direction fromIndex(int index) {
		if (index < 0 && index >= ALL.length) {
			return null;
		}

		return ALL[index];
	}

	public Direction getOpposite() {
		return ALL[opposite];
	}

	public int getOffsetX() {
		return offsetX;
	}

	public int getOffsetY() {
		return offsetY;
	}

	public int getOffsetZ() {
		return offsetZ;
	}

	public String getName() {
		return name;
	}

	public static enum Axis {

		X("x") { public int choose(int x, int y, int z) { return x; } },
		Y("y") { public int choose(int x, int y, int z) { return y; } },
		Z("z") { public int choose(int x, int y, int z) { return z; } };

		private final String name;

		private Axis(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}

		public abstract int choose(int x, int y, int z);

	}
}
