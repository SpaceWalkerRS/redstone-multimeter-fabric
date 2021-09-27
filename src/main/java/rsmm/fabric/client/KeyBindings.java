package rsmm.fabric.client;

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

import org.lwjgl.glfw.GLFW;

import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;

import rsmm.fabric.common.event.EventType;

public class KeyBindings {
	
	private static final String FILE_NAME = "hotkeys.txt";
	
	private static final Set<String> CATEGORIES = new LinkedHashSet<>();
	private static final Map<String, KeyBinding> KEY_BINDINGS = new LinkedHashMap<>();
	
	public static final String MAIN;
	public static final String EVENT_TYPES;
	
	public static final KeyBinding TOGGLE_METER;
	public static final KeyBinding RESET_METER;
	public static final KeyBinding PAUSE_METERS;
	public static final KeyBinding STEP_FORWARD;
	public static final KeyBinding STEP_BACKWARD;
	public static final KeyBinding TOGGLE_HUD;
	public static final KeyBinding OPEN_MULTIMETER_SCREEN;
	
	public static final KeyBinding[] TOGGLE_EVENT_TYPES;
	
	private static String registerCategory(String category) {
		if (!CATEGORIES.add(category)) {
			throw new IllegalStateException("Cannot register multiple keybind categories with the same name!");
		}
		
		return category;
	}
	
	private static KeyBinding registerKeyBinding(KeyBinding keyBinding) {
		if (KEY_BINDINGS.putIfAbsent(keyBinding.getTranslationKey(), keyBinding) != null) {
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
					keyBinding.setBoundKey(InputUtil.fromTranslationKey(key));
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
				String name = keyBinding.getTranslationKey();
				String key = keyBinding.getBoundKeyTranslationKey();
				
				bw.write(name + "=" + key);
				bw.newLine();
			}
		} catch (IOException e) {
			
		}
	}
	
	static {
		
		MAIN        = registerCategory("Redstone Multimeter");
		EVENT_TYPES = registerCategory("Event Types");
		
		TOGGLE_METER           = registerKeyBinding(new KeyBinding("Toggle Meter"          , GLFW.GLFW_KEY_M     , MAIN));
		RESET_METER            = registerKeyBinding(new KeyBinding("Reset Meter"           , GLFW.GLFW_KEY_B     , MAIN));
		PAUSE_METERS           = registerKeyBinding(new KeyBinding("Pause Meters"          , GLFW.GLFW_KEY_N     , MAIN));
		STEP_FORWARD           = registerKeyBinding(new KeyBinding("Step Forward"          , GLFW.GLFW_KEY_COMMA , MAIN));
		STEP_BACKWARD          = registerKeyBinding(new KeyBinding("Step Backward"         , GLFW.GLFW_KEY_PERIOD, MAIN));
		TOGGLE_HUD             = registerKeyBinding(new KeyBinding("Toggle HUD"            , GLFW.GLFW_KEY_H     , MAIN));
		OPEN_MULTIMETER_SCREEN = registerKeyBinding(new KeyBinding("Open Multimeter Screen", GLFW.GLFW_KEY_G     , MAIN));
		
		TOGGLE_EVENT_TYPES = new KeyBinding[EventType.ALL.length];
		
		for (int index = 0; index < EventType.ALL.length; index++) {
			TOGGLE_EVENT_TYPES[index] = registerKeyBinding(new KeyBinding(String.format("Toggle \'%s\'", EventType.fromIndex(index).getName()), GLFW.GLFW_KEY_UNKNOWN, EVENT_TYPES));
		}
	}
}
