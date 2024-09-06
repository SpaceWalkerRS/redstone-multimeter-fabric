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

import org.lwjgl.glfw.GLFW;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.InputConstants.Key;

import net.minecraft.client.Minecraft;
import net.minecraft.client.options.KeyBinding;

import redstone.multimeter.RedstoneMultimeterMod;
import redstone.multimeter.common.meter.event.EventType;
import redstone.multimeter.interfaces.mixin.IKeyBinding;

public class Keybinds {

	private static final String FILE_NAME = "hotkeys.txt";

	private static final Set<String> CATEGORIES = new LinkedHashSet<>();
	private static final Map<String, KeyBinding> KEYBINDS = new LinkedHashMap<>();

	public static final String MAIN;
	public static final String EVENT_TYPES;

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

	private static String registerCategory(String category) {
		if (!CATEGORIES.add(category)) {
			throw new IllegalStateException("Cannot register multiple keybind categories with the same name!");
		}

		return category;
	}

	private static KeyBinding registerKeybind(KeyBinding keybind) {
		if (KEYBINDS.putIfAbsent(keybind.getName(), keybind) != null) {
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
			for (KeyBinding keybind : KEYBINDS.values()) {
				String name = keybind.getName();
				String key = keybind.saveString();

				bw.write(name + "=" + key);
				bw.newLine();
			}
		} catch (IOException e) {
			RedstoneMultimeterMod.LOGGER.warn("exception while saving keybinds", e);
		}
	}

	public static boolean isPressed(Minecraft minecraft, KeyBinding keybind) {
		Key key = ((IKeyBinding)keybind).rsmm$getKey();
		return key != null && GLFW.glfwGetKey(minecraft.window.getWindow(), key.getValue()) == GLFW.GLFW_PRESS;
	}

	static {

		MAIN        = registerCategory(RedstoneMultimeterMod.MOD_NAME);
		EVENT_TYPES = registerCategory("Event Types");

		TOGGLE_METER           = registerKeybind(new KeyBinding("Toggle Meter"          , GLFW.GLFW_KEY_M            , MAIN));
		RESET_METER            = registerKeybind(new KeyBinding("Reset Meter"           , GLFW.GLFW_KEY_B            , MAIN));
		LOAD_METER_GROUP       = registerKeybind(new KeyBinding("Load Meter Group"      , GLFW.GLFW_KEY_LEFT_BRACKET , MAIN));
		SAVE_METER_GROUP       = registerKeybind(new KeyBinding("Save Meter Group"      , GLFW.GLFW_KEY_RIGHT_BRACKET, MAIN));
		PAUSE_METERS           = registerKeybind(new KeyBinding("Pause Meters"          , GLFW.GLFW_KEY_N            , MAIN));
		TOGGLE_FOCUS_MODE      = registerKeybind(new KeyBinding("Toggle Focus Mode"     , GLFW.GLFW_KEY_F            , MAIN));
		TOGGLE_MARKER          = registerKeybind(new KeyBinding("Toggle Tick Marker"    , GLFW.GLFW_KEY_Y            , MAIN));
		STEP_BACKWARD          = registerKeybind(new KeyBinding("Step Backward"         , GLFW.GLFW_KEY_COMMA        , MAIN));
		STEP_FORWARD           = registerKeybind(new KeyBinding("Step Forward"          , GLFW.GLFW_KEY_PERIOD       , MAIN));
		SCROLL_HUD             = registerKeybind(new KeyBinding("Scroll HUD"            , GLFW.GLFW_KEY_LEFT_ALT     , MAIN));
		TOGGLE_HUD             = registerKeybind(new KeyBinding("Toggle HUD"            , GLFW.GLFW_KEY_H            , MAIN));
		OPEN_MULTIMETER_SCREEN = registerKeybind(new KeyBinding("Open Multimeter Screen", GLFW.GLFW_KEY_G            , MAIN));
		OPEN_METER_CONTROLS    = registerKeybind(new KeyBinding("Open Meter Controls"   , GLFW.GLFW_KEY_I            , MAIN));
		OPEN_OPTIONS_MENU      = registerKeybind(new KeyBinding("Open Options Menu"     , GLFW.GLFW_KEY_O            , MAIN));
		VIEW_TICK_PHASE_TREE   = registerKeybind(new KeyBinding("View Tick Phases"      , GLFW.GLFW_KEY_U            , MAIN));
		PRINT_LOGS             = registerKeybind(new KeyBinding("Print Logs To File"    , GLFW.GLFW_KEY_P            , MAIN));

		TOGGLE_EVENT_TYPES = new KeyBinding[EventType.ALL.length];

		for (int index = 0; index < EventType.ALL.length; index++) {
			TOGGLE_EVENT_TYPES[index] = registerKeybind(new KeyBinding(String.format("Toggle \'%s\'", EventType.byIndex(index).getName()), GLFW.GLFW_KEY_UNKNOWN, EVENT_TYPES));
		}
	}
}
