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

import org.lwjgl.glfw.GLFW;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.InputConstants.Key;

import net.minecraft.client.KeyMapping;

import redstone.multimeter.RedstoneMultimeterMod;
import redstone.multimeter.common.meter.event.EventType;
import redstone.multimeter.interfaces.mixin.IKeyMapping;

public class Keybinds {

	private static final String FILE_NAME = "hotkeys.txt";

	private static final Set<String> CATEGORIES = new LinkedHashSet<>();
	private static final Map<String, KeyMapping> KEYBINDS = new LinkedHashMap<>();
	// used for parsing keybinds from before RSMM 1.16
	private static final Map<String, KeyMapping> LEGACY_KEYBINDS = new HashMap<>();

	public static final KeyMapping[] TOGGLE_EVENT_TYPES = new KeyMapping[EventType.ALL.length];

	public static final String MAIN;
	public static final String METER_EVENT_TYPES;

	public static final KeyMapping TOGGLE_METER;
	public static final KeyMapping RESET_METER;
	public static final KeyMapping LOAD_METER_GROUP;
	public static final KeyMapping SAVE_METER_GROUP;
	public static final KeyMapping PAUSE_TIMELINE;
	public static final KeyMapping TOGGLE_FOCUS_MODE;
	public static final KeyMapping TOGGLE_MARKER;
	public static final KeyMapping STEP_BACKWARD;
	public static final KeyMapping STEP_FORWARD;
	public static final KeyMapping SCROLL_HUD;
	public static final KeyMapping TOGGLE_HUD;
	public static final KeyMapping OPEN_MULTIMETER_SCREEN;
	public static final KeyMapping OPEN_METER_CONTROLS;
	public static final KeyMapping OPEN_OPTIONS_MENU;
	public static final KeyMapping VIEW_TICK_PHASE_TREE;
	public static final KeyMapping PRINT_LOGS;

	private static String registerCategory(String category) {
		category = "rsmm.keybind.category." + category;

		if (!CATEGORIES.add(category)) {
			throw new IllegalStateException("Cannot register multiple keybind categories with the same name! (" + category + ")");
		}

		return category;
	}

	private static KeyMapping registerKeybind(String name, String legacyName, String category, int defaultKey) {
		name = "rsmm.keybind." + name;

		if (KEYBINDS.containsKey(name)) {
			throw new IllegalStateException("Cannot register multiple keybinds with the same name! (" + name + ")");
		}

		KeyMapping keybind = new KeyMapping(name, defaultKey, category);

		KEYBINDS.put(name, keybind);
		if (legacyName != null) {
			LEGACY_KEYBINDS.put(legacyName, keybind);
		}

		return keybind;
	}

	public static Collection<String> getCategories() {
		return Collections.unmodifiableSet(CATEGORIES);
	}

	public static Collection<KeyMapping> getKeybinds() {
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

				KeyMapping keybind = KEYBINDS.get(name);
				if (keybind == null) {
					keybind = LEGACY_KEYBINDS.get(name);
				}

				if (keybind != null) {
					keybind.setKey(InputConstants.getKey(key));
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
			for (KeyMapping keybind : KEYBINDS.values()) {
				String name = keybind.getName();
				String key = keybind.saveString();

				bw.write(name + "=" + key);
				bw.newLine();
			}
		} catch (IOException e) {
			RedstoneMultimeterMod.LOGGER.warn("exception while saving keybinds", e);
		}
	}

	public static boolean isPressed(KeyMapping keybind) {
		Key key = ((IKeyMapping)keybind).rsmm$getKey();
		return key != null && GLFW.glfwGetKey(MultimeterClient.MINECRAFT.getWindow().getWindow(), key.getValue()) == GLFW.GLFW_PRESS;
	}

	static {
		MAIN              = registerCategory("redstoneMultimeter");
		METER_EVENT_TYPES = registerCategory("meterEventTypes");

		TOGGLE_METER           = registerKeybind("toggleMeter"            , "Toggle Meter"          , MAIN, GLFW.GLFW_KEY_M);
		RESET_METER            = registerKeybind("resetMeter"             , "Reset Meter"           , MAIN, GLFW.GLFW_KEY_B);
		LOAD_METER_GROUP       = registerKeybind("loadMeterGroup"         , "Load Meter Group"      , MAIN, GLFW.GLFW_KEY_LEFT_BRACKET);
		SAVE_METER_GROUP       = registerKeybind("saveMeterGroup"         , "Save Meter Group"      , MAIN, GLFW.GLFW_KEY_RIGHT_BRACKET);
		PAUSE_TIMELINE         = registerKeybind("pauseTimeline"          , "Pause Meters"          , MAIN, GLFW.GLFW_KEY_N);
		STEP_BACKWARD          = registerKeybind("stepBackwardTimeline"   , "Step Backward"         , MAIN, GLFW.GLFW_KEY_COMMA);
		STEP_FORWARD           = registerKeybind("stepForwardTimeline"    , "Step Forward"          , MAIN, GLFW.GLFW_KEY_PERIOD);
		SCROLL_HUD             = registerKeybind("scrollTimeline"         , "Scroll HUD"            , MAIN, GLFW.GLFW_KEY_LEFT_ALT);
		TOGGLE_HUD             = registerKeybind("toggleHud"              , "Toggle HUD"            , MAIN, GLFW.GLFW_KEY_H);
		TOGGLE_MARKER          = registerKeybind("toggleTickMarker"       , "Toggle Tick Marker"    , MAIN, GLFW.GLFW_KEY_Y);
		TOGGLE_FOCUS_MODE      = registerKeybind("toggleFocusMode"        , "Toggle Focus Mode"     , MAIN, GLFW.GLFW_KEY_F);
		OPEN_MULTIMETER_SCREEN = registerKeybind("openMultimeterScreen"   , "Open Multimeter Screen", MAIN, GLFW.GLFW_KEY_G);
		OPEN_METER_CONTROLS    = registerKeybind("openMeterControlsScreen", "Open Meter Controls"   , MAIN, GLFW.GLFW_KEY_I);
		OPEN_OPTIONS_MENU      = registerKeybind("openOptionsScreen"      , "Open Options Menu"     , MAIN, GLFW.GLFW_KEY_O);
		VIEW_TICK_PHASE_TREE   = registerKeybind("openTickPhaseTreeScreen", "View Tick Phases"      , MAIN, GLFW.GLFW_KEY_U);
		PRINT_LOGS             = registerKeybind("toggleLogPrinter"       , "Print Logs To File"    , MAIN, GLFW.GLFW_KEY_P);

		for (int index = 0; index < EventType.ALL.length; index++) {
			TOGGLE_EVENT_TYPES[index] = registerKeybind("toggleMeterEventType." + EventType.byId(index).getKey(), String.format("Toggle \'%s\'", EventType.byId(index).getLegacyKey()), METER_EVENT_TYPES, GLFW.GLFW_KEY_UNKNOWN);
		}
	}
}
