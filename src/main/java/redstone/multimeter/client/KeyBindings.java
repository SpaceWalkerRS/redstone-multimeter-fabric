package redstone.multimeter.client;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import net.minecraft.client.settings.KeyBinding;

import redstone.multimeter.RedstoneMultimeterMod;
import redstone.multimeter.common.meter.event.EventType;

public class KeyBindings {
	
	private static final String FILE_NAME = "hotkeys-legacy.txt";
	
	private static final Set<String> CATEGORIES = new LinkedHashSet<>();
	private static final Map<String, KeyBinding> KEY_BINDINGS = new LinkedHashMap<>();
	
	public static final String MAIN;
	public static final String EVENT_TYPES;
	
	public static final KeyBinding TOGGLE_METER;
	public static final KeyBinding RESET_METER;
	public static final KeyBinding PAUSE_METERS;
	public static final KeyBinding TOGGLE_MARKER;
	public static final KeyBinding STEP_BACKWARD;
	public static final KeyBinding STEP_FORWARD;
	public static final KeyBinding SCROLL_HUD;
	public static final KeyBinding TOGGLE_HUD;
	public static final KeyBinding OPEN_MULTIMETER_SCREEN;
	public static final KeyBinding OPEN_METER_CONTROLS;
	public static final KeyBinding OPEN_OPTIONS_MENU;
	public static final KeyBinding VIEW_TICK_PHASE_TREE;
	public static final KeyBinding PRINT_LOGS;
	
	public static final KeyBinding[] TOGGLE_EVENT_TYPES;
	
	private static final int MOUSE_BUTTON_OFFSET = 100;
	
	private static String registerCategory(String category) {
		if (!CATEGORIES.add(category)) {
			throw new IllegalStateException("Cannot register multiple keybind categories with the same name!");
		}
		
		return category;
	}
	
	private static KeyBinding registerKeyBinding(KeyBinding keyBinding) {
		if (KEY_BINDINGS.putIfAbsent(keyBinding.getKeyDescription(), keyBinding) != null) {
			throw new IllegalStateException("Cannot register multiple keybinds with the same name!");
		}
		
		return keyBinding;
	}
	
	public static Collection<String> getCategories() {
		return Collections.unmodifiableSet(CATEGORIES);
	}
	
	public static Collection<KeyBinding> getKeyBindings() {
		return Collections.unmodifiableCollection(KEY_BINDINGS.values());
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
				String key = args[1];
				
				KeyBinding keyBinding = KEY_BINDINGS.get(name);
				
				if (keyBinding != null) {
					keyBinding.setKeyCode(Integer.parseInt(key));
				}
			}
		} catch (IOException e) {
			
		}
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
			for (KeyBinding keyBinding : KEY_BINDINGS.values()) {
				String name = keyBinding.getKeyDescription();
				int keyCode = keyBinding.getKeyCode();
				
				bw.write(name + "=" + keyCode);
				bw.newLine();
			}
		} catch (IOException e) {
			
		}
	}
	
	public static boolean isBoundToButton(KeyBinding keyBinding, int button) {
		return keyBinding.getKeyCode() + MOUSE_BUTTON_OFFSET == button;
	}
	
	public static boolean isBoundToKey(KeyBinding keyBinding, int key) {
		return keyBinding.getKeyCode() == key;
	}
	
	public static boolean isPressed(KeyBinding keyBinding) {
		int keyCode = keyBinding.getKeyCode();
		
		if (keyCode < 0) {
			return Mouse.isButtonDown(keyCode + MOUSE_BUTTON_OFFSET);
		} else {
			return Keyboard.isKeyDown(keyCode);
		}
	}
	
	static {
		
		MAIN        = registerCategory(RedstoneMultimeterMod.MOD_NAME);
		EVENT_TYPES = registerCategory("Event Types");
		
		TOGGLE_METER           = registerKeyBinding(new KeyBinding("Toggle Meter"          , Keyboard.KEY_M       , MAIN));
		RESET_METER            = registerKeyBinding(new KeyBinding("Reset Meter"           , Keyboard.KEY_B       , MAIN));
		PAUSE_METERS           = registerKeyBinding(new KeyBinding("Pause Meters"          , Keyboard.KEY_N       , MAIN));
		TOGGLE_MARKER          = registerKeyBinding(new KeyBinding("Toggle Tick Marker"    , Keyboard.KEY_Y       , MAIN));
		STEP_BACKWARD          = registerKeyBinding(new KeyBinding("Step Backward"         , Keyboard.KEY_COMMA   , MAIN));
		STEP_FORWARD           = registerKeyBinding(new KeyBinding("Step Forward"          , Keyboard.KEY_PERIOD  , MAIN));
		SCROLL_HUD             = registerKeyBinding(new KeyBinding("Scroll HUD"            , Keyboard.KEY_LMENU   , MAIN));
		TOGGLE_HUD             = registerKeyBinding(new KeyBinding("Toggle HUD"            , Keyboard.KEY_H       , MAIN));
		OPEN_MULTIMETER_SCREEN = registerKeyBinding(new KeyBinding("Open Multimeter Screen", Keyboard.KEY_G       , MAIN));
		OPEN_METER_CONTROLS    = registerKeyBinding(new KeyBinding("Open Meter Controls"   , Keyboard.KEY_I       , MAIN));
		OPEN_OPTIONS_MENU      = registerKeyBinding(new KeyBinding("Open Options Menu"     , Keyboard.KEY_O       , MAIN));
		VIEW_TICK_PHASE_TREE   = registerKeyBinding(new KeyBinding("View Tick Phase Tree"  , Keyboard.KEY_U       , MAIN));
		PRINT_LOGS             = registerKeyBinding(new KeyBinding("Print Logs To File"    , Keyboard.KEY_P       , MAIN));
		
		TOGGLE_EVENT_TYPES = new KeyBinding[EventType.ALL.length];
		
		for (int index = 0; index < EventType.ALL.length; index++) {
			TOGGLE_EVENT_TYPES[index] = registerKeyBinding(new KeyBinding(String.format("Toggle \'%s\'", EventType.fromIndex(index).getName()), Keyboard.KEY_NONE, EVENT_TYPES));
		}
	}
}
