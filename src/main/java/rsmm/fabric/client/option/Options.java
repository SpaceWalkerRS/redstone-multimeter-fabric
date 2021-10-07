package rsmm.fabric.client.option;

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

public class Options {
	
	public static class RedstoneMultimeter {
		
		public static final BooleanOption NUMBERED_NAMES  = new BooleanOption("Numbered Meter Names", "Add a number at the end of meter names that increments with each meter you add.", true);
		
	}
	
	public static class HUD {
		
		public static final IntegerOption HISTORY         = new IntegerOption("History", "The number of ticks displayed in the primary overview.", 60, 1, 10001);
		public static final IntegerOption SELECTED_COLUMN = new IntegerOption("Selected Column", "The column of the main overview that highlights the tick that is selected for showing sub-tick events in the secondary overview.", 44, 0, 10000);
		public static final IntegerOption OPACITY         = new IntegerOption("Opacity", "", 100, 0, 100);
		
	}
	
	public static class Miscellaneous {
		
		public static final IntegerOption SCROLL_SPEED    = new IntegerOption("Scroll Speed", "The scroll speed in Redstone Multimeter related GUIs.", 7, 1, 69);
		public static final BooleanOption VERSION_WARNING = new BooleanOption("Version Warning", "Send a warning message in chat when you join a server that has a different version of Redstone Multimeter installed.", true);
		
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
		int history = HUD.HISTORY.get();
		
		if (history < HUD.SELECTED_COLUMN.get()) {
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
		
		register("Redstone Multimeter",
			RedstoneMultimeter.NUMBERED_NAMES
		);
		register("HUD",
			HUD.HISTORY,
			HUD.SELECTED_COLUMN,
			HUD.OPACITY
		);
		register("Miscellaneous",
			Miscellaneous.SCROLL_SPEED,
			Miscellaneous.VERSION_WARNING
		);
	}
}
