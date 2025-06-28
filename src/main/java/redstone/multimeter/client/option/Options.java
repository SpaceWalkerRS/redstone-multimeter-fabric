package redstone.multimeter.client.option;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import redstone.multimeter.RedstoneMultimeterMod;
import redstone.multimeter.client.gui.hud.Directionality;
import redstone.multimeter.client.render.MeterNameMode;
import redstone.multimeter.common.meter.ColorPicker;
import redstone.multimeter.common.meter.MeterGroup;
import redstone.multimeter.common.meter.event.EventType;
import redstone.multimeter.util.ColorUtils;

public class Options {

	public static class RedstoneMultimeter {

		public static final BooleanOption                NUMBERED_NAMES       = new BooleanOption("Numbered Meter Names", "Add a number at the end of meter names that increments with each meter you add.", true);
		public static final EnumOption<ColorPicker>      COLOR_PICKER         = new EnumOption<>("Color Picker", "The algorithm used to pick colors for new meters", ColorPicker.class, ColorPicker.RANDOM);
		public static final BooleanOption                SHIFTY_METERS        = new BooleanOption("Shifty Meters", "Use the shift key to control whether a new meter is movable or not.", true);
		public static final BooleanOption                AUTO_RANDOM_TICKS    = new BooleanOption("Auto Random Ticks", String.format("Automatically enable the \'%s\' event type when placing a meter on a block that accepts random ticks.", EventType.RANDOM_TICK.getName()), true);
		public static final EnumOption<MeterNameMode>    RENDER_METER_NAMES   = new EnumOption<>("Render Meter Names", "Render meter name tags inside the meter highlights in the world.", MeterNameMode.class, MeterNameMode.IN_FOCUS_MODE);
		public static final IntegerOption                METER_NAME_RANGE     = new IntegerOption("Meter Name Range", "The range within which meter names will be rendered.", 16, 0, 64);
		public static final BooleanOption                CREATE_GROUP_ON_JOIN = new BooleanOption("Create Group On Join", "Automatically create a new meter group upon joining a world or server.", true);
		public static final StringOption                 DEFAULT_METER_GROUP  = new StringOption("Default Meter Group", "The name of the meter group that is created upon joining a world or server. If this field is left blank your username is used instead.", "", MeterGroup.getMaxNameLength());
		public static final BooleanOption                PREVIEW_METER_GROUPS = new BooleanOption("Preview Meter Groups", "Preview meter groups before loading them from the saved meter group slot. This preview will appear as long as the Load Meter Group keybind is pressed. Pressing the slot key again will stop previewing and load the meter group from that slot.", true);
		public static final BooleanOption                BYPASS_WARNINGS      = new BooleanOption("Bypass Meter Group Warnings", "Bypass warnings when trying to load a meter group from an empty slot or trying to save a meter group to a slot when not subscribed to one. Otherwise you will have to press the keybind again to confirm the action.", false);

	}

	public static class HUD {

		public static final IntegerOption                SCREEN_POS_X         = new IntegerOption("Horizontal Screen Position", "The horizontal position of the HUD on the screen, as a percentage of the screen width.", 0, 0, 100);
		public static final IntegerOption                SCREEN_POS_Y         = new IntegerOption("Vertical Screen Position", "The vertical position of the HUD on the screen, as a percantage of the screen height.", 0, 0, 100);
		public static final EnumOption<Directionality.X> DIRECTIONALITY_X     = new EnumOption<>("Horizontal Directionality", "The direction along which the events are drawn.", Directionality.X.class, Directionality.X.LEFT_TO_RIGHT);
		public static final EnumOption<Directionality.Y> DIRECTIONALITY_Y     = new EnumOption<>("Vertical Directionality", "The direction along which meters are listed.", Directionality.Y.class, Directionality.Y.TOP_TO_BOTTOM);
		public static final IntegerOption                COLUMN_COUNT         = new IntegerOption("History", "The number of ticks displayed in the primary overview.", 60, 1, 10001);
		public static final IntegerOption                SELECTED_COLUMN      = new IntegerOption("Selected Column", "The column of the primary overview that highlights the tick that is selected for showing sub-tick events in the secondary overview.", 44, 0, 10000);
		public static final IntegerOption                CELL_SCALE           = new IntegerOption("Cell Scale", "The scale of cells in the primary and secondary overviews.", 1, 1, 10);
		public static final IntegerOption                COLUMN_WIDTH         = new IntegerOption("Column Width", "The width of a column of the primary and secondary overviews.", 3, 1, 50);
		public static final IntegerOption                ROW_HEIGHT           = new IntegerOption("Row Height", "The height of a row in the HUD.", 9, 1, 50);
		public static final IntegerOption                GRID_SIZE            = new IntegerOption("Grid Size", "The thickness of the gridlines in the HUD.", 1, 1, 10);
		public static final StringOption                 TICK_MARKER_COLOR    = new StringOption("Tick Marker Color", "The color of the highlight around the tick marker, in RRGGBB format.", "FF0000", 6);
		public static final BooleanOption                AUTO_REMOVE_MARKER   = new BooleanOption("Auto Remove Tick Marker", "Automatically remove the tick marker when unpausing the HUD.", false);
		public static final BooleanOption                HIDE_HIGHLIGHT       = new BooleanOption("Hide Highlight", "Hide the highlight around the selected tick when the HUD is not paused.", true);
		public static final BooleanOption                PAUSE_INDICATOR      = new BooleanOption("Pause Indicator", "Display a little play/pause indicator underneath the HUD.", false);
		public static final IntegerOption                OPACITY              = new IntegerOption("Opacity", "", 100, 0, 100);
		public static final BooleanOption                AUTO_PAUSE           = new BooleanOption("Auto Pause", "Automatically pause the HUD when opening the Multimeter screen.", true);
		public static final BooleanOption                AUTO_UNPAUSE         = new BooleanOption("Auto Unpause", "Automatically unpause the HUD when closing the Multimeter screen.", true);

	}

	public static class LogPrinter {

		public static final BooleanOption                PRINT_OLD_LOGS       = new BooleanOption("Print Old Logs", "Print old logs when activating the printer.", false);
		public static final IntegerOption                MAX_RUNTIME          = new IntegerOption("Maximum Runtime", "The limit of how long the printer can run, in ticks. The printer will automatically stop when reaching this number. Entering a value of -1 will remove the limit entirely.", -1, -1, Integer.MAX_VALUE);

	}

	public static class Miscellaneous {

		public static final IntegerOption                SCROLL_SPEED         = new IntegerOption("Scroll Speed", "The scroll speed in Redstone Multimeter related GUIs.", 7, 1, 69);
		public static final IntegerOption                DOUBLE_CLICK_TIME    = new IntegerOption("Double Click Time", "The double click time in Redstone Multimeter related GUIs.", 5, 1, 500);
		public static final BooleanOption                VERSION_WARNING      = new BooleanOption("Version Warning", "Send a warning message in chat when you join a server that has a different version of Redstone Multimeter installed.", true);

	}

	private static final Map<String, Option> BY_NAME;
	private static final Map<String, List<Option>> BY_CATEGORY;

	private static final String FILE_NAME = "options.txt";

	public static Collection<Option> all() {
		return Collections.unmodifiableCollection(BY_NAME.values());
	}

	public static Map<String, List<Option>> byCategory() {
		return Collections.unmodifiableMap(BY_CATEGORY);
	}

	public static Collection<Option> ofCategory(String category) {
		return Collections.unmodifiableCollection(BY_CATEGORY.getOrDefault(category, Collections.emptyList()));
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

				String name = args[0];
				String value = args[1];

				Option option = BY_NAME.get(name);

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
			for (Entry<String, Option> entry : BY_NAME.entrySet()) {
				String name = entry.getKey();
				String value = entry.getValue().getAsString();

				bw.write(name + "=" + value);
				bw.newLine();
			}
		} catch (IOException e) {
			RedstoneMultimeterMod.LOGGER.warn("exception while saving options", e);
		}
	}

	private static void register(String category, Option... options) {
		if (category != null) {
			if (BY_CATEGORY.containsKey(category)) {
				throw new IllegalStateException("Cannot register a category multiple times!");
			}

			BY_CATEGORY.put(category, Arrays.asList(options));
		}

		for (Option option : options) {
			if (BY_NAME.containsKey(option.getName())) {
				throw new IllegalStateException("Cannot register multiple options with the same name!");
			}

			BY_NAME.put(option.getName(), option);
		}
	}

	static {

		BY_NAME = new LinkedHashMap<>();
		BY_CATEGORY = new LinkedHashMap<>();

		register(RedstoneMultimeterMod.MOD_NAME,
			RedstoneMultimeter.NUMBERED_NAMES,
			RedstoneMultimeter.COLOR_PICKER,
			RedstoneMultimeter.SHIFTY_METERS,
			RedstoneMultimeter.AUTO_RANDOM_TICKS,
			RedstoneMultimeter.RENDER_METER_NAMES,
			RedstoneMultimeter.METER_NAME_RANGE,
			RedstoneMultimeter.CREATE_GROUP_ON_JOIN,
			RedstoneMultimeter.DEFAULT_METER_GROUP,
			RedstoneMultimeter.PREVIEW_METER_GROUPS,
			RedstoneMultimeter.BYPASS_WARNINGS
		);
		register("HUD",
			HUD.SCREEN_POS_X,
			HUD.SCREEN_POS_Y,
			HUD.DIRECTIONALITY_X,
			HUD.DIRECTIONALITY_Y,
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
		register("Log Printer",
			LogPrinter.PRINT_OLD_LOGS,
			LogPrinter.MAX_RUNTIME
		);
		register("Miscellaneous",
			Miscellaneous.SCROLL_SPEED,
			Miscellaneous.DOUBLE_CLICK_TIME,
			Miscellaneous.VERSION_WARNING
		);
	}
}
