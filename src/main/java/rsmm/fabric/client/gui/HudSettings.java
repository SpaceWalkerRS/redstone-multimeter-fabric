package rsmm.fabric.client.gui;

import rsmm.fabric.util.ColorUtils;

public class HudSettings {
	
	public static final int COLUMN_WIDTH = 3;
	public static final int ROW_HEIGHT = 9;
	public static final int GRID_SIZE = 1;
	
	public static int COLUMN_COUNT = 60;
	public static int SELECTED_COLUMN = 44;
	public static int ROW_COUNT;
	
	public static boolean IGNORE_HIDDEN_METERS = true;
	public static boolean FORCE_FULL_OPACITY = false;
	
	public static final int NAMES_TICKS_SPACING = 3;
	public static final int TICKS_SUBTICKS_GAP = 3;
	
	public static int OPACITY = 0x6F;
	public static final int BACKGROUND_COLOR = 0x202020;
	public static final int BACKGROUND_COLOR_TRANSPARENT = 0xDD202020;
	public static final int MAIN_GRID_COLOR = 0x404040;
	public static final int INTERVAL_GRID_COLOR = 0x606060;
	public static final int MARKER_GRID_COLOR = 0xC0C0C0;
	public static final int SELECTION_INDICATOR_COLOR = 0xFFFFFF;
	
	public static final int POWERED_TEXT_COLOR = 0x000000;
	public static final int UNPOWERED_TEXT_COLOR = 0x707070;
	public static final int METER_GROUP_NAME_COLOR_DARK = 0x202020;
	public static final int METER_GROUP_NAME_COLOR_LIGHT = 0xD0D0D0;
	
	
	public static int namesTableWidth(int namesWidth) {
		return namesWidth + NAMES_TICKS_SPACING;
	}
	
	public static int ticksOverviewWidth() {
		return COLUMN_COUNT * (COLUMN_WIDTH + GRID_SIZE) + GRID_SIZE;
	}
	
	public static int subticksOverviewWidth(int subtickCount) {
		return subtickCount * (COLUMN_WIDTH + GRID_SIZE) + GRID_SIZE;
	}
	
	public static int height() {
		return ROW_COUNT * (ROW_HEIGHT + GRID_SIZE) + GRID_SIZE;
	}
	
	
	public static int opacity() {
		return FORCE_FULL_OPACITY ? 0xFF : OPACITY;
	}
	
	public static int backgroundColor() {
		return ColorUtils.fromARGB(opacity(), BACKGROUND_COLOR);
	}
	
	public static int mainGridColor() {
		return ColorUtils.fromARGB(opacity(), MAIN_GRID_COLOR);
	}
	
	public static int intervalGridColor() {
		return ColorUtils.fromARGB(opacity(), INTERVAL_GRID_COLOR);
	}
	
	public static int markerGridColor() {
		return ColorUtils.fromARGB(opacity(), MARKER_GRID_COLOR);
	}
	
	public static int selectionIndicatorColor() {
		return ColorUtils.fromARGB(opacity(), SELECTION_INDICATOR_COLOR);
	}
	
	public static int poweredTextColor() {
		return ColorUtils.fromARGB(opacity(), POWERED_TEXT_COLOR);
	}
	
	public static int unpoweredTextColor() {
		return ColorUtils.fromARGB(opacity(), UNPOWERED_TEXT_COLOR);
	}
	
	public static int meterGroupNameColor(boolean light) {
		int rgb = light ? METER_GROUP_NAME_COLOR_LIGHT : METER_GROUP_NAME_COLOR_DARK;
		return ColorUtils.fromARGB(opacity(), rgb);
	}
}
