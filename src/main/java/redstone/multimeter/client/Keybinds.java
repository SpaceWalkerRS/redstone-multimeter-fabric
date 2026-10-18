package redstone.multimeter.client;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.lwjgl.sdl.SDLKeyboard;
import org.lwjgl.sdl.SDLScancode;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.InputConstants.Key;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.KeyMapping.Category;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.input.MouseButtonInfo;
import net.minecraft.resources.Identifier;

import redstone.multimeter.RedstoneMultimeterMod;
import redstone.multimeter.client.gui.element.input.KeyEvent;
import redstone.multimeter.client.gui.element.input.MouseEvent;
import redstone.multimeter.common.meter.event.EventType;
import redstone.multimeter.interfaces.mixin.IKeyMapping;

public class Keybinds {

	private static final String FILE_NAME = "hotkeys.txt";

	private static final Map<String, Category> CATEGORIES = new LinkedHashMap<>();
	private static final Map<String, KeyMapping> KEYBINDS = new LinkedHashMap<>();
	// used for parsing keybinds from before RSMM 1.16
	private static final Map<String, KeyMapping> LEGACY_KEYBINDS = new HashMap<>();

	public static final KeyMapping[] TOGGLE_EVENT_TYPES = new KeyMapping[EventType.ALL.length];

	public static final Category MAIN;
	public static final Category METER_EVENT_TYPES;

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

	private static Category registerCategory(String name) {
		if (CATEGORIES.containsKey(name)) {
			throw new IllegalStateException("Cannot register multiple keybind categories with the same name! (" + name + ")");
		}

		Identifier id = Identifier.fromNamespaceAndPath(RedstoneMultimeterMod.NAMESPACE, name);
		Category category = Category.register(id);

		CATEGORIES.put(name, category);

		return category;
	}

	private static KeyMapping registerKeybind(String name, String legacyName, Category category, int defaultKey) {
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

	public static Collection<Category> getCategories() {
		return Collections.unmodifiableCollection(CATEGORIES.values());
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
		ByteBuffer state = SDLKeyboard.SDL_GetKeyboardState();
		return key != null && state != null && state.get(key.getValue()) != 0;
	}

	public static boolean matches(KeyMapping keybind, MouseEvent event) {
		return keybind.matchesMouse(new MouseButtonEvent(event.mouseX(), event.mouseY(), new MouseButtonInfo(event.button(), -1)));
	}

	public static boolean matches(KeyMapping keybind, KeyEvent event) {
		return keybind.matches(new net.minecraft.client.input.KeyEvent(event.scanCode(), event.keyCode(), event.modifiers()));
	}

	static {
		MAIN              = registerCategory("redstone_multimeter");
		METER_EVENT_TYPES = registerCategory("meter_event_types");

		TOGGLE_METER           = registerKeybind("toggleMeter"            , "Toggle Meter"          , MAIN, SDLScancode.SDL_SCANCODE_M);
		RESET_METER            = registerKeybind("resetMeter"             , "Reset Meter"           , MAIN, SDLScancode.SDL_SCANCODE_B);
		LOAD_METER_GROUP       = registerKeybind("loadMeterGroup"         , "Load Meter Group"      , MAIN, SDLScancode.SDL_SCANCODE_LEFTBRACKET);
		SAVE_METER_GROUP       = registerKeybind("saveMeterGroup"         , "Save Meter Group"      , MAIN, SDLScancode.SDL_SCANCODE_RIGHTBRACKET);
		PAUSE_TIMELINE         = registerKeybind("pauseTimeline"          , "Pause Meters"          , MAIN, SDLScancode.SDL_SCANCODE_N);
		STEP_BACKWARD          = registerKeybind("stepBackwardTimeline"   , "Step Backward"         , MAIN, SDLScancode.SDL_SCANCODE_COMMA);
		STEP_FORWARD           = registerKeybind("stepForwardTimeline"    , "Step Forward"          , MAIN, SDLScancode.SDL_SCANCODE_PERIOD);
		SCROLL_HUD             = registerKeybind("scrollTimeline"         , "Scroll HUD"            , MAIN, SDLScancode.SDL_SCANCODE_LALT);
		TOGGLE_HUD             = registerKeybind("toggleHud"              , "Toggle HUD"            , MAIN, SDLScancode.SDL_SCANCODE_H);
		TOGGLE_MARKER          = registerKeybind("toggleTickMarker"       , "Toggle Tick Marker"    , MAIN, SDLScancode.SDL_SCANCODE_Y);
		TOGGLE_FOCUS_MODE      = registerKeybind("toggleFocusMode"        , "Toggle Focus Mode"     , MAIN, SDLScancode.SDL_SCANCODE_F);
		OPEN_MULTIMETER_SCREEN = registerKeybind("openMultimeterScreen"   , "Open Multimeter Screen", MAIN, SDLScancode.SDL_SCANCODE_G);
		OPEN_METER_CONTROLS    = registerKeybind("openMeterControlsScreen", "Open Meter Controls"   , MAIN, SDLScancode.SDL_SCANCODE_I);
		OPEN_OPTIONS_MENU      = registerKeybind("openOptionsScreen"      , "Open Options Menu"     , MAIN, SDLScancode.SDL_SCANCODE_O);
		VIEW_TICK_PHASE_TREE   = registerKeybind("openTickPhaseTreeScreen", "View Tick Phases"      , MAIN, SDLScancode.SDL_SCANCODE_U);
		PRINT_LOGS             = registerKeybind("toggleLogPrinter"       , "Print Logs To File"    , MAIN, SDLScancode.SDL_SCANCODE_P);

		for (int index = 0; index < EventType.ALL.length; index++) {
			TOGGLE_EVENT_TYPES[index] = registerKeybind("toggleMeterEventType." + EventType.byId(index).getKey(), String.format("Toggle \'%s\'", EventType.byId(index).getLegacyKey()), METER_EVENT_TYPES, SDLScancode.SDL_SCANCODE_UNKNOWN);
		}
	}
}
