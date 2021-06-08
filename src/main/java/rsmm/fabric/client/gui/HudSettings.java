package rsmm.fabric.client.gui;

public class HudSettings {
	
	/** The width of a column in the ticks and subticks tables */
	public static final int COLUMN_WIDTH = 3;
	/** The height of a row */
	public static final int ROW_HEIGHT = 9;
	/** The thickness of the grid lines */
	public static final int GRID_SIZE = 1;
	
	public static final int COLUMN_COUNT = 60;
	public static final int SELECTED_COLUMN = 44;
	public static int ROW_COUNT;
	public static boolean IGNORE_HIDDEN_METERS = true;
	
	/** The width of the ticks table */
	public static final int TICKS_TABLE_WIDTH = COLUMN_COUNT * (COLUMN_WIDTH + GRID_SIZE) + GRID_SIZE;
	/** The spacing between the names table and the ticks table */
	public static final int NAMES_TICKS_SPACING = 3;
	/** The gap between the ticks table and the subticks table */
	public static final int TICKS_SUB_TICKS_GAP = 3;
	
	public static final int BACKGROUND_COLOR = 0xFF202020;
	public static final int BACKGROUND_COLOR_TRANSPARENT = 0xDD202020;
	public static final int MAIN_GRID_COLOR = 0xFF404040;
	public static final int INTERVAL_GRID_COLOR = 0xFF606060;
	public static final int MARKER_GRID_COLOR = 0xFFC0C0C0;
	public static final int SELECTION_INDICATOR_COLOR = 0xFFFFFFFF;
	
	public static final int POWERED_TEXT_COLOR = 0xFF000000;
	public static final int UNPOWERED_TEXT_COLOR = 0xFF707070;
	public static final int METER_GROUP_NAME_COLOR_DARK = 0xFF202020;
	public static final int METER_GROUP_NAME_COLOR_LIGHT = 0xFFD0D0D0;
	
}
