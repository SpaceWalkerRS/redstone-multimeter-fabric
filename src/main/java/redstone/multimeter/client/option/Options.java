package redstone.multimeter.client.option;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import redstone.multimeter.RedstoneMultimeterMod;
import redstone.multimeter.client.gui.hud.Directionality;
import redstone.multimeter.common.meter.MeterGroup;

public class Options {
	
	public static class RedstoneMultimeter {
		
		public static final BooleanOption                NUMBERED_NAMES       = new BooleanOption("Numbered Meter Names", "Add a number at the end of meter names that increments with each meter you add.", true);
		public static final BooleanOption                SHIFTY_METERS        = new BooleanOption("Shifty Meters", "Use the shift key to control whether a new meter is movable or not.", true);
		public static final BooleanOption                CREATE_GROUP_ON_JOIN = new BooleanOption("Create Group On Join", "Automatically create a new meter group upon joining a world or server.", true);
		public static final StringOption                 DEFAULT_METER_GROUP  = new StringOption("Default Meter Group", "The name of the meter group that is created upon joining a world or server. If this field is left blank your username is used.", "", MeterGroup.getMaxNameLength());
		
	}
	
	public static class HUD {
		
		public static final IntegerOption                SCREEN_POS_X         = new IntegerOption("Horizontal Screen Position", "The horizontal position of the HUD on the screen, as a percentage of the screen width.", 0, 0, 100);
		public static final IntegerOption                SCREEN_POS_Y         = new IntegerOption("Vertical Screen Position", "The vertical position of the HUD on the screen, as a percantage of the screen height.", 0, 0, 100);
		public static final EnumOption<Directionality.X> DIRECTIONALITY_X     = new EnumOption<>("Horizontal Directionality", "The direction along which the events are drawn.", Directionality.X.class, Directionality.X.LEFT_TO_RIGHT);
		public static final EnumOption<Directionality.Y> DIRECTIONALITY_Y     = new EnumOption<>("Vertical Directionality", "The direction along which meters are listed.", Directionality.Y.class, Directionality.Y.TOP_TO_BOTTOM);
		public static final IntegerOption                COLUMN_COUNT         = new IntegerOption("History", "The number of ticks displayed in the primary overview.", 60, 1, 10001);
		public static final IntegerOption                SELECTED_COLUMN      = new IntegerOption("Selected Column", "The column of the main overview that highlights the tick that is selected for showing sub-tick events in the secondary overview.", 44, 0, 10000);
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
		public static final BooleanOption                VERSION_WARNING      = new BooleanOption("Version Warning", "Send a warning message in chat when you join a server that has a different version of Redstone Multimeter installed.", true);
		
	}
	
	private static final Map<String, IOption> BY_NAME;
	private static final Map<String, List<IOption>> BY_CATEGORY;
	
	private static final String FILE_NAME = "options.txt";
	
	public static Collection<IOption> all() {
		return Collections.unmodifiableCollection(BY_NAME.values());
	}
	
	public static Map<String, List<IOption>> byCategory() {
		return Collections.unmodifiableMap(BY_CATEGORY);
	}
	
	public static Collection<IOption> ofCategory(String category) {
		return Collections.unmodifiableCollection(BY_CATEGORY.getOrDefault(category, Collections.emptyList()));
	}
	
	public static void validate() {
		int history = HUD.COLUMN_COUNT.get();
		
		if (HUD.SELECTED_COLUMN.get() >= history) {
			HUD.SELECTED_COLUMN.set(history - 1);
		}
	}
	
	public static void load(File folder) {
		File file = new File(folder, FILE_NAME);
		
		if (!file.exists()) {
			save(folder);
			return;
		}
		
		try (BufferedReader br = new BufferedReader(new FileReader(file))) {
			String line;
			
			while ((line = br.readLine()) != null) {
				String[] args = line.split("=", 2);
				
				if (args.length < 2) {
					continue;
				}
				
				String name = args[0];
				String value = args[1];
				
				IOption option = BY_NAME.get(name);
				
				if (option != null) {
					option.setFromString(value);
				}
			}
		} catch (IOException e) {
			
		}
		
		validate();
	}
	
	public static void save(File folder) {
		if (!folder.exists()) {
			folder.mkdirs();
		}
		
		File file = new File(folder, FILE_NAME);
		
		try {
			file.createNewFile();
		} catch (IOException e) {
			
		}
		
		try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
			for (Entry<String, IOption> entry : BY_NAME.entrySet()) {
				String name = entry.getKey();
				String value = entry.getValue().getAsString();
				
				bw.write(name + "=" + value);
				bw.newLine();
			}
		} catch (IOException e) {
			
		}
	}
	
	private static void register(String category, IOption... options) {
		if (BY_CATEGORY.containsKey(category)) {
			throw new IllegalStateException("Cannot register a category multiple times!");
		}
		
		BY_CATEGORY.put(category, Arrays.asList(options));
		
		for (IOption option : options) {
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
			RedstoneMultimeter.SHIFTY_METERS,
			RedstoneMultimeter.CREATE_GROUP_ON_JOIN,
			RedstoneMultimeter.DEFAULT_METER_GROUP
		);
		register("HUD",
			HUD.SCREEN_POS_X,
			HUD.SCREEN_POS_Y,
			HUD.DIRECTIONALITY_X,
			HUD.DIRECTIONALITY_Y,
			HUD.COLUMN_COUNT,
			HUD.SELECTED_COLUMN,
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
			Miscellaneous.VERSION_WARNING
		);
	}
}
