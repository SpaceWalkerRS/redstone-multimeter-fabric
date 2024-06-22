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

import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;

import redstone.multimeter.RedstoneMultimeterMod;
import redstone.multimeter.common.meter.event.EventType;
import redstone.multimeter.interfaces.mixin.IKeyMapping;

public class Keybinds {

	private static final String FILE_NAME = "hotkeys.txt";

	private static final Set<String> CATEGORIES = new LinkedHashSet<>();
	private static final Map<String, KeyMapping> KEYBINDS = new LinkedHashMap<>();

	public static final String MAIN;
	public static final String EVENT_TYPES;

	public static final KeyMapping TOGGLE_METER;
	public static final KeyMapping RESET_METER;
	public static final KeyMapping PAUSE_METERS;
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

	public static final KeyMapping[] TOGGLE_EVENT_TYPES;

	private static String registerCategory(String category) {
		if (!CATEGORIES.add(category)) {
			throw new IllegalStateException("Cannot register multiple keybind categories with the same name!");
		}

		return category;
	}

	private static KeyMapping registerKeybind(KeyMapping keybind) {
		if (KEYBINDS.putIfAbsent(keybind.getName(), keybind) != null) {
			throw new IllegalStateException("Cannot register multiple keybinds with the same name!");
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

	public static boolean isPressed(Minecraft minecraft, KeyMapping keybind) {
		Key key = ((IKeyMapping)keybind).rsmm$getKey();
		return key != null && GLFW.glfwGetKey(minecraft.getWindow().getWindow(), key.getValue()) == GLFW.GLFW_PRESS;
	}

	static {

		MAIN        = registerCategory(RedstoneMultimeterMod.MOD_NAME);
		EVENT_TYPES = registerCategory("Event Types");

		TOGGLE_METER           = registerKeybind(new KeyMapping("Toggle Meter"          , GLFW.GLFW_KEY_M       , MAIN));
		RESET_METER            = registerKeybind(new KeyMapping("Reset Meter"           , GLFW.GLFW_KEY_B       , MAIN));
		PAUSE_METERS           = registerKeybind(new KeyMapping("Pause Meters"          , GLFW.GLFW_KEY_N       , MAIN));
		TOGGLE_MARKER          = registerKeybind(new KeyMapping("Toggle Tick Marker"    , GLFW.GLFW_KEY_Y       , MAIN));
		STEP_BACKWARD          = registerKeybind(new KeyMapping("Step Backward"         , GLFW.GLFW_KEY_COMMA   , MAIN));
		STEP_FORWARD           = registerKeybind(new KeyMapping("Step Forward"          , GLFW.GLFW_KEY_PERIOD  , MAIN));
		SCROLL_HUD             = registerKeybind(new KeyMapping("Scroll HUD"            , GLFW.GLFW_KEY_LEFT_ALT, MAIN));
		TOGGLE_HUD             = registerKeybind(new KeyMapping("Toggle HUD"            , GLFW.GLFW_KEY_H       , MAIN));
		OPEN_MULTIMETER_SCREEN = registerKeybind(new KeyMapping("Open Multimeter Screen", GLFW.GLFW_KEY_G       , MAIN));
		OPEN_METER_CONTROLS    = registerKeybind(new KeyMapping("Open Meter Controls"   , GLFW.GLFW_KEY_I       , MAIN));
		OPEN_OPTIONS_MENU      = registerKeybind(new KeyMapping("Open Options Menu"     , GLFW.GLFW_KEY_O       , MAIN));
		VIEW_TICK_PHASE_TREE   = registerKeybind(new KeyMapping("View Tick Phases"      , GLFW.GLFW_KEY_U       , MAIN));
		PRINT_LOGS             = registerKeybind(new KeyMapping("Print Logs To File"    , GLFW.GLFW_KEY_P       , MAIN));

		TOGGLE_EVENT_TYPES = new KeyMapping[EventType.ALL.length];

		for (int index = 0; index < EventType.ALL.length; index++) {
			TOGGLE_EVENT_TYPES[index] = registerKeybind(new KeyMapping(String.format("Toggle \'%s\'", EventType.byIndex(index).getName()), GLFW.GLFW_KEY_UNKNOWN, EVENT_TYPES));
		}
	}
}
