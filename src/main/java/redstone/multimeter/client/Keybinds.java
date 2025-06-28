package redstone.multimeter.client;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import net.minecraft.client.options.KeyBinding;

import redstone.multimeter.RedstoneMultimeterMod;
import redstone.multimeter.common.meter.event.EventType;

public class Keybinds {

	private static final String FILE_NAME = "hotkeys-legacy.txt";

	private static final Set<String> CATEGORIES = new LinkedHashSet<>();
	private static final Map<String, KeyBinding> KEYBINDS = new LinkedHashMap<>();

//	public static final String MAIN;
//	public static final String EVENT_TYPES;

	public static final KeyBinding TOGGLE_METER;
	public static final KeyBinding RESET_METER;
	public static final KeyBinding LOAD_METER_GROUP;
	public static final KeyBinding SAVE_METER_GROUP;
	public static final KeyBinding PAUSE_METERS;
	public static final KeyBinding TOGGLE_FOCUS_MODE;
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
/*
	private static String registerCategory(String category) {
		if (!CATEGORIES.add(category)) {
			throw new IllegalStateException("Cannot register multiple keybind categories with the same name!");
		}

		return category;
	}
*/
	private static KeyBinding registerKeybind(KeyBinding keybind) {
		if (KEYBINDS.putIfAbsent(keybind.name, keybind) != null) {
			throw new IllegalStateException("Cannot register multiple keybinds with the same name!");
		}

		return keybind;
	}

	public static Collection<String> getCategories() {
		return Collections.unmodifiableSet(CATEGORIES);
	}

	public static Collection<KeyBinding> getKeybinds() {
		return Collections.unmodifiableCollection(KEYBINDS.values());
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
				String key = args[1];

				KeyBinding keybind = KEYBINDS.get(name);

				if (keybind != null) {
					keybind.keyCode = Integer.parseInt(key);
				}
			}
		} catch (IOException e) {
			RedstoneMultimeterMod.LOGGER.warn("exception while loading keybinds", e);
		}
	}

	public static void save(Path dir) {
		if (!Files.exists(dir)) {
			try {
				Files.createDirectories(dir);
			} catch (IOException e) {
				throw new RuntimeException("unable to create parent directories of keybinds file", e);
			}
		}

		Path file = dir.resolve(FILE_NAME);

		try (BufferedWriter bw = Files.newBufferedWriter(file)) {
			for (KeyBinding keybind : KEYBINDS.values()) {
				String name = keybind.name;
				int key = keybind.keyCode;

				bw.write(name + "=" + key);
				bw.newLine();
			}
		} catch (IOException e) {
			RedstoneMultimeterMod.LOGGER.warn("exception while saving keybinds", e);
		}
	}

	public static boolean matchesButton(KeyBinding keybind, int button) {
		return button == keybind.keyCode + MOUSE_BUTTON_OFFSET;
	}

	public static boolean matchesKey(KeyBinding keybind, int key) {
		return key == keybind.keyCode;
	}

	public static boolean isPressed(KeyBinding keybind) {
		int key = keybind.keyCode;

		if (key < 0) {
			return Mouse.isButtonDown(key + MOUSE_BUTTON_OFFSET);
		} else {
			return Keyboard.isKeyDown(key);
		}
	}

	static {

//		MAIN        = registerCategory(RedstoneMultimeterMod.MOD_NAME);
//		EVENT_TYPES = registerCategory("Event Types");

		TOGGLE_METER           = registerKeybind(new KeyBinding("Toggle Meter"          , Keyboard.KEY_M));
		RESET_METER            = registerKeybind(new KeyBinding("Reset Meter"           , Keyboard.KEY_B));
		LOAD_METER_GROUP       = registerKeybind(new KeyBinding("Load Meter Group"      , Keyboard.KEY_LBRACKET));
		SAVE_METER_GROUP       = registerKeybind(new KeyBinding("Save Meter Group"      , Keyboard.KEY_RBRACKET));
		PAUSE_METERS           = registerKeybind(new KeyBinding("Pause Meters"          , Keyboard.KEY_N));
		TOGGLE_FOCUS_MODE      = registerKeybind(new KeyBinding("Toggle Focus Mode"     , Keyboard.KEY_F));
		TOGGLE_MARKER          = registerKeybind(new KeyBinding("Toggle Tick Marker"    , Keyboard.KEY_Y));
		STEP_BACKWARD          = registerKeybind(new KeyBinding("Step Backward"         , Keyboard.KEY_COMMA));
		STEP_FORWARD           = registerKeybind(new KeyBinding("Step Forward"          , Keyboard.KEY_PERIOD));
		SCROLL_HUD             = registerKeybind(new KeyBinding("Scroll HUD"            , Keyboard.KEY_LMENU));
		TOGGLE_HUD             = registerKeybind(new KeyBinding("Toggle HUD"            , Keyboard.KEY_H));
		OPEN_MULTIMETER_SCREEN = registerKeybind(new KeyBinding("Open Multimeter Screen", Keyboard.KEY_G));
		OPEN_METER_CONTROLS    = registerKeybind(new KeyBinding("Open Meter Controls"   , Keyboard.KEY_I));
		OPEN_OPTIONS_MENU      = registerKeybind(new KeyBinding("Open Options Menu"     , Keyboard.KEY_O));
		VIEW_TICK_PHASE_TREE   = registerKeybind(new KeyBinding("View Tick Phases"      , Keyboard.KEY_U));
		PRINT_LOGS             = registerKeybind(new KeyBinding("Print Logs To File"    , Keyboard.KEY_P));

		TOGGLE_EVENT_TYPES = new KeyBinding[EventType.ALL.length];

		for (int index = 0; index < EventType.ALL.length; index++) {
			TOGGLE_EVENT_TYPES[index] = registerKeybind(new KeyBinding(String.format("Toggle \'%s\'", EventType.byIndex(index).getName()), Keyboard.KEY_NONE));
		}
	}
}
