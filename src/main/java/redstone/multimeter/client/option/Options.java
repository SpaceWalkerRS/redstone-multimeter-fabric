package redstone.multimeter.client.option;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import redstone.multimeter.RedstoneMultimeterMod;
import redstone.multimeter.client.gui.hud.Orientation;
import redstone.multimeter.client.render.MeterHighlightMode;
import redstone.multimeter.client.render.MeterNameTagMode;
import redstone.multimeter.common.meter.ColorPicker;
import redstone.multimeter.common.meter.MeterGroup;
import redstone.multimeter.util.ColorUtils;

public class Options {

	public static class RedstoneMultimeter {

		public static final BooleanOption                  NUMBERED_NAMES       = new BooleanOption("redstoneMultimeter.numberedMeterNames", "Numbered Meter Names", true);
		public static final EnumOption<ColorPicker>        COLOR_PICKER         = new EnumOption<> ("redstoneMultimeter.colorPicker", "Color Picker", ColorPicker.class, ColorPicker.RANDOM);
		public static final BooleanOption                  SHIFTY_METERS        = new BooleanOption("redstoneMultimeter.shiftyMeters", "Shifty Meters", true);
		public static final BooleanOption                  AUTO_RANDOM_TICKS    = new BooleanOption("redstoneMultimeter.autoRandomTicks", "Auto Random Ticks", true);
		public static final EnumOption<MeterHighlightMode> RENDER_METERS        = new EnumOption<> ("redstoneMultimeter.meterHighlights", null, MeterHighlightMode.class, MeterHighlightMode.ALWAYS);
		public static final IntegerOption                  METER_RANGE          = new IntegerOption("redstoneMultimeter.meterHighlightsRange", null, 64, -1, 1024);
		public static final EnumOption<MeterNameTagMode>   RENDER_METER_NAMES   = new EnumOption<> ("redstoneMultimeter.meterNameTags", "Render Meter Names", MeterNameTagMode.class, MeterNameTagMode.IN_FOCUS_MODE);
		public static final IntegerOption                  METER_NAME_RANGE     = new IntegerOption("redstoneMultimeter.meterNameTagsRange", "Meter Name Range", 16, 0, 64);
		public static final BooleanOption                  CREATE_GROUP_ON_JOIN = new BooleanOption("redstoneMultimeter.createMeterGroupOnJoin", "Create Group On Join", true);
		public static final StringOption                   DEFAULT_METER_GROUP  = new StringOption ("redstoneMultimeter.defaultMeterGroup", "Default Meter Group", "", MeterGroup.getMaxNameLength());
		public static final BooleanOption                  PREVIEW_METER_GROUPS = new BooleanOption("redstoneMultimeter.previewMeterGroups", "Preview Meter Groups", true);
		public static final BooleanOption                  BYPASS_WARNINGS      = new BooleanOption("redstoneMultimeter.bypassMeterGroupWarnings", "Bypass Meter Group Warnings", false);

	}

	public static class HUD {

		public static final IntegerOption                SCREEN_POS_X         = new IntegerOption("hud.horizontalScreenPosition", "Horizontal Screen Position", 0, 0, 100);
		public static final IntegerOption                SCREEN_POS_Y         = new IntegerOption("hud.verticalScreenPosition", "Vertical Screen Position", 0, 0, 100);
		public static final EnumOption<Orientation.X>    ORIENTATION_X        = new EnumOption<> ("hud.horizontalOrientation", "Horizontal Directionality", Orientation.X.class, Orientation.X.LEFT_TO_RIGHT);
		public static final EnumOption<Orientation.Y>    ORIENTATION_Y        = new EnumOption<> ("hud.verticalOrientation", "Vertical Directionality", Orientation.Y.class, Orientation.Y.TOP_TO_BOTTOM);
		public static final IntegerOption                COLUMN_COUNT         = new IntegerOption("hud.history", "History", 60, 1, 10001);
		public static final IntegerOption                SELECTED_COLUMN      = new IntegerOption("hud.selectedColumn", "Selected Column", 44, 0, 10000);
		public static final IntegerOption                CELL_SCALE           = new IntegerOption("hud.cellScale", null, 1, 1, 10);
		public static final IntegerOption                COLUMN_WIDTH         = new IntegerOption("hud.columnWidth", "Column Width", 3, 1, 50);
		public static final IntegerOption                ROW_HEIGHT           = new IntegerOption("hud.rowHeight", "Row Height", 9, 1, 50);
		public static final IntegerOption                GRID_SIZE            = new IntegerOption("hud.gridSize", "Grid Size", 1, 1, 10);
		public static final StringOption                 TICK_MARKER_COLOR    = new StringOption ("hud.tickMarkerColor", "Tick Marker Color", "FF0000", 6);
		public static final BooleanOption                AUTO_REMOVE_MARKER   = new BooleanOption("hud.autoRemoveTickMarker", "Auto Remove Tick Marker", false);
		public static final BooleanOption                HIDE_HIGHLIGHT       = new BooleanOption("hud.hideHighlight", "Hide Highlight", true);
		public static final BooleanOption                PAUSE_INDICATOR      = new BooleanOption("hud.pauseIndicator", "Pause Indicator", false);
		public static final IntegerOption                OPACITY              = new IntegerOption("hud.opacity", "Opacity", 100, 0, 100);
		public static final BooleanOption                AUTO_PAUSE           = new BooleanOption("hud.autoPause", "Auto Pause", true);
		public static final BooleanOption                AUTO_UNPAUSE         = new BooleanOption("hud.autoUnpause", "Auto Unpause", true);

	}

	public static class LogPrinter {

		public static final BooleanOption                PRINT_OLD_LOGS       = new BooleanOption("logPrinter.printOldLogs", "Print Old Logs", false);
		public static final IntegerOption                MAX_RUNTIME          = new IntegerOption("logPrinter.maxRuntime", "Maximum Runtime", -1, -1, Integer.MAX_VALUE);

	}

	public static class Miscellaneous {

		public static final IntegerOption                SCROLL_SPEED         = new IntegerOption("miscellaneous.scrollSpeed", "Scroll Speed", 7, 1, 69);
		public static final IntegerOption                DOUBLE_CLICK_TIME    = new IntegerOption("miscellaneous.doubleClickTime", "Double Click Time", 5, 1, 500);
		public static final BooleanOption                VERSION_WARNING      = new BooleanOption("miscellaneous.serverVersionWarning", "Version Warning", true);

	}

	private static final Map<String, Option> OPTIONS;
	// used for parsing options from before RSMM 1.16
	private static final Map<String, Option> LEGACY_OPTIONS;
	private static final Map<String, List<Option>> OPTIONS_BY_CATEGORY;

	private static final String FILE_NAME = "options.txt";

	public static Collection<Option> all() {
		return Collections.unmodifiableCollection(OPTIONS.values());
	}

	public static Map<String, List<Option>> byCategory() {
		return Collections.unmodifiableMap(OPTIONS_BY_CATEGORY);
	}

	public static Collection<Option> ofCategory(String category) {
		return Collections.unmodifiableCollection(OPTIONS_BY_CATEGORY.getOrDefault(category, Collections.emptyList()));
	}

	public static void validate() {
		int history = HUD.COLUMN_COUNT.get();

		if (HUD.SELECTED_COLUMN.get() >= history) {
			HUD.SELECTED_COLUMN.set(history - 1);
		}

		try {
			String rawColor = Options.HUD.TICK_MARKER_COLOR.get();
			ColorUtils.fromRGBString(rawColor);
		} catch (NumberFormatException e) {
			Options.HUD.TICK_MARKER_COLOR.reset();
		}
	}

	public static void load(Path dir) {
		Path file = dir.resolve(FILE_NAME);

		if (!Files.exists(file)) {
			save(dir);
			return;
		}

		try (BufferedReader br = Files.newBufferedReader(file)) {
			String line;

			while ((line = br.readLine()) != null) {
				String[] args = line.split("=", 2);

				if (args.length < 2) {
					continue;
				}

				String key = args[0];
				String value = args[1];

				Option option = OPTIONS.get(key);
				if (option == null) {
					option = LEGACY_OPTIONS.get(key);
				}

				if (option != null) {
					option.setFromString(value);
				}
			}
		} catch (IOException e) {
			RedstoneMultimeterMod.LOGGER.warn("exception while loading options", e);
		}

		validate();
	}

	public static void save(Path dir) {
		if (!Files.exists(dir)) {
			try {
				Files.createDirectories(dir);
			} catch (IOException e) {
				throw new RuntimeException("unable to create parent directories of options file");
			}
		}

		Path file = dir.resolve(FILE_NAME);

		try (BufferedWriter bw = Files.newBufferedWriter(file)) {
			for (Entry<String, Option> entry : OPTIONS.entrySet()) {
				String key = entry.getKey();
				String value = entry.getValue().getAsString();

				bw.write(key + "=" + value);
				bw.newLine();
			}
		} catch (IOException e) {
			RedstoneMultimeterMod.LOGGER.warn("exception while saving options", e);
		}
	}

	private static void register(String category, Option... options) {
		category = "rsmm.option." + category;

		if (category != null) {
			if (OPTIONS_BY_CATEGORY.containsKey(category)) {
				throw new IllegalStateException("Cannot register a category multiple times!");
			}

			OPTIONS_BY_CATEGORY.put(category, Arrays.asList(options));
		}

		for (Option option : options) {
			String key = option.key();
			String legacyKey = option.legacyKey();

			if (OPTIONS.containsKey(key)) {
				throw new IllegalStateException("Cannot register multiple options with the same name!");
			}

			OPTIONS.put(key, option);
			if (legacyKey != null) {
				LEGACY_OPTIONS.put(legacyKey, option);
			}
		}
	}

	static {

		OPTIONS = new LinkedHashMap<>();
		LEGACY_OPTIONS = new HashMap<>();
		OPTIONS_BY_CATEGORY = new LinkedHashMap<>();

		register("redstoneMultimeter",
			RedstoneMultimeter.NUMBERED_NAMES,
			RedstoneMultimeter.COLOR_PICKER,
			RedstoneMultimeter.SHIFTY_METERS,
			RedstoneMultimeter.AUTO_RANDOM_TICKS,
			RedstoneMultimeter.RENDER_METERS,
			RedstoneMultimeter.METER_RANGE,
			RedstoneMultimeter.RENDER_METER_NAMES,
			RedstoneMultimeter.METER_NAME_RANGE,
			RedstoneMultimeter.CREATE_GROUP_ON_JOIN,
			RedstoneMultimeter.DEFAULT_METER_GROUP,
			RedstoneMultimeter.PREVIEW_METER_GROUPS,
			RedstoneMultimeter.BYPASS_WARNINGS
		);
		register("hud",
			HUD.SCREEN_POS_X,
			HUD.SCREEN_POS_Y,
			HUD.ORIENTATION_X,
			HUD.ORIENTATION_Y,
			HUD.COLUMN_COUNT,
			HUD.SELECTED_COLUMN,
			HUD.CELL_SCALE,
			HUD.COLUMN_WIDTH,
			HUD.ROW_HEIGHT,
			HUD.GRID_SIZE,
			HUD.TICK_MARKER_COLOR,
			HUD.AUTO_REMOVE_MARKER,
			HUD.HIDE_HIGHLIGHT,
			HUD.PAUSE_INDICATOR,
			HUD.OPACITY,
			HUD.AUTO_PAUSE,
			HUD.AUTO_UNPAUSE
		);
		register("logPrinter",
			LogPrinter.PRINT_OLD_LOGS,
			LogPrinter.MAX_RUNTIME
		);
		register("miscellaneous",
			Miscellaneous.SCROLL_SPEED,
			Miscellaneous.DOUBLE_CLICK_TIME,
			Miscellaneous.VERSION_WARNING
		);
	}
}
