package rsmm.fabric.client;

import org.lwjgl.glfw.GLFW;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.options.KeyBinding;

@Environment(EnvType.CLIENT)
public class KeyBindings {
	
	public static final String CATEGORY = "Redstone Multimeter";
	
	public static final KeyBinding TOGGLE_METER = new KeyBinding("Toggle Meter", GLFW.GLFW_KEY_M, CATEGORY);
	public static final KeyBinding PAUSE_METERS = new KeyBinding("Pause Meters", GLFW.GLFW_KEY_N, CATEGORY);
	public static final KeyBinding STEP_FORWARD = new KeyBinding("Step Forward", GLFW.GLFW_KEY_COMMA, CATEGORY);
	public static final KeyBinding STEP_BACKWARD = new KeyBinding("Step Backward", GLFW.GLFW_KEY_PERIOD, CATEGORY);
	public static final KeyBinding TOGGLE_HUD = new KeyBinding("Toggle HUD", GLFW.GLFW_KEY_H, CATEGORY);
	public static final KeyBinding OPEN_MULTIMETER_SCREEN = new KeyBinding("Open Multimeter Screen", GLFW.GLFW_KEY_G, CATEGORY);
	
}
