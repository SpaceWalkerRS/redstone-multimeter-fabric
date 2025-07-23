package redstone.multimeter.client;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
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
	private static final Map<String, KeyBinding> LEGACY_KEYBINDS = new HashMap<>();

	public static final KeyBinding[] TOGGLE_EVENT_TYPES = new KeyBinding[EventType.ALL.length];

	public static final String MAIN;
	public static final String METER_EVENT_TYPES;

	public static final KeyBinding TOGGLE_METER;
	public static final KeyBinding RESET_METER;
	public static final KeyBinding LOAD_METER_GROUP;
	public static final KeyBinding SAVE_METER_GROUP;
	public static final KeyBinding PAUSE_TIMELINE;
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

	private static final int MOUSE_BUTTON_OFFSET = 100;

	private static String registerCategory(String category) {
		category = "rsmm.keybind.category." + category;

		if (!CATEGORIES.add(category)) {
			throw new IllegalStateException("Cannot register multiple keybind categories with the same name! (" + category + ")");
		}

		return category;
	}

	private static KeyBinding registerKeybind(String name, String legacyName, String category, int defaultKey) {
		name = "rsmm.keybind." + name;

		if (KEYBINDS.containsKey(name)) {
			throw new IllegalStateException("Cannot register multiple keybinds with the same name! (" + name + ")");
		}

		KeyBinding keybind = new KeyBinding(name, defaultKey/*, category*/);

		KEYBINDS.put(name, keybind);
		if (legacyName != null) {
			LEGACY_KEYBINDS.put(legacyName, keybind);
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
				if (keybind == null) {
					keybind = LEGACY_KEYBINDS.get(name);
				}

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
		MAIN              = registerCategory("redstoneMultimeter");
		METER_EVENT_TYPES = registerCategory("meterEventTypes");

		TOGGLE_METER           = registerKeybind("toggleMeter"            , "Toggle Meter"          , MAIN, Keyboard.KEY_M);
		RESET_METER            = registerKeybind("resetMeter"             , "Reset Meter"           , MAIN, Keyboard.KEY_B);
		LOAD_METER_GROUP       = registerKeybind("loadMeterGroup"         , "Load Meter Group"      , MAIN, Keyboard.KEY_LBRACKET);
		SAVE_METER_GROUP       = registerKeybind("saveMeterGroup"         , "Save Meter Group"      , MAIN, Keyboard.KEY_RBRACKET);
		PAUSE_TIMELINE         = registerKeybind("pauseTimeline"          , "Pause Meters"          , MAIN, Keyboard.KEY_N);
		STEP_BACKWARD          = registerKeybind("stepBackwardTimeline"   , "Step Backward"         , MAIN, Keyboard.KEY_COMMA);
		STEP_FORWARD           = registerKeybind("stepForwardTimeline"    , "Step Forward"          , MAIN, Keyboard.KEY_PERIOD);
		SCROLL_HUD             = registerKeybind("scrollTimeline"         , "Scroll HUD"            , MAIN, Keyboard.KEY_LMENU);
		TOGGLE_HUD             = registerKeybind("toggleHud"              , "Toggle HUD"            , MAIN, Keyboard.KEY_H);
		TOGGLE_MARKER          = registerKeybind("toggleTickMarker"       , "Toggle Tick Marker"    , MAIN, Keyboard.KEY_Y);
		TOGGLE_FOCUS_MODE      = registerKeybind("toggleFocusMode"        , "Toggle Focus Mode"     , MAIN, Keyboard.KEY_F);
		OPEN_MULTIMETER_SCREEN = registerKeybind("openMultimeterScreen"   , "Open Multimeter Screen", MAIN, Keyboard.KEY_G);
		OPEN_METER_CONTROLS    = registerKeybind("openMeterControlsScreen", "Open Meter Controls"   , MAIN, Keyboard.KEY_I);
		OPEN_OPTIONS_MENU      = registerKeybind("openOptionsScreen"      , "Open Options Menu"     , MAIN, Keyboard.KEY_O);
		VIEW_TICK_PHASE_TREE   = registerKeybind("openTickPhaseTreeScreen", "View Tick Phases"      , MAIN, Keyboard.KEY_U);
		PRINT_LOGS             = registerKeybind("toggleLogPrinter"       , "Print Logs To File"    , MAIN, Keyboard.KEY_P);

		for (int index = 0; index < EventType.ALL.length; index++) {
			TOGGLE_EVENT_TYPES[index] = registerKeybind("toggleMeterEventType." + EventType.byId(index).getKey(), String.format("Toggle \'%s\'", EventType.byId(index).getLegacyKey()), METER_EVENT_TYPES, Keyboard.KEY_NONE);
		}
	}
}
