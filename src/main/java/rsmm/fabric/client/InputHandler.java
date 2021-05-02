package rsmm.fabric.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;

import rsmm.fabric.client.gui.MultimeterScreen;

public class InputHandler {
	
	private final MultimeterClient client;
	
	public InputHandler(MultimeterClient client) {
		this.client = client;
	}
	
	public void handleInputEvents() {
		if (KeyBindings.TOGGLE_METER.wasPressed()) {
			client.toggleMeter();
		}
		if (KeyBindings.PAUSE_METERS.wasPressed()) {
			client.getHudRenderer().pause();
		}
		if (KeyBindings.STEP_FORWARD.wasPressed()) {
			client.getHudRenderer().stepForward(Screen.hasControlDown() ? 10 : 1);
		}
		if (KeyBindings.STEP_BACKWARD.wasPressed()) {
			client.getHudRenderer().stepBackward(Screen.hasControlDown() ? 10 : 1);
		}
		if (KeyBindings.TOGGLE_HUD.wasPressed()) {
			client.toggleHud();
		}
		if (KeyBindings.OPEN_MULTIMETER_SCREEN.wasPressed()) {
			MinecraftClient minecraftClient = client.getMinecraftClient();
			
			if (minecraftClient.currentScreen == null) {
				minecraftClient.openScreen(new MultimeterScreen(client));
			}
		}
	}
	
	
	// Methods for handling keybindings while the client has a screen open
	
	public boolean mouseClick(double mouseX, double mouseY, int button) {
		if (KeyBindings.PAUSE_METERS.matchesMouse(button)) {
			client.getHudRenderer().pause();
		} else
		if (KeyBindings.STEP_FORWARD.matchesMouse(button)) {
			client.getHudRenderer().stepForward(Screen.hasControlDown() ? 10 : 1);
		} else
		if (KeyBindings.STEP_BACKWARD.matchesMouse(button)) {
			client.getHudRenderer().stepBackward(Screen.hasControlDown() ? 10 : 1);
		} else
		if (KeyBindings.TOGGLE_HUD.matchesMouse(button)) {
			client.toggleHud();
		} else
		if (KeyBindings.OPEN_MULTIMETER_SCREEN.matchesMouse(button)) {
			MinecraftClient minecraftClient = client.getMinecraftClient();
			Screen screen = minecraftClient.currentScreen;
			
			if (screen != null && screen instanceof MultimeterScreen) {
				minecraftClient.openScreen(null);
			}
		} else {
			return false;
		}
		
		return true;
	}
	
	public boolean keyPress(int keyCode, int scanCode, int modifiers) {
		if (KeyBindings.PAUSE_METERS.matchesKey(keyCode, scanCode)) {
			client.getHudRenderer().pause();
		} else
		if (KeyBindings.STEP_FORWARD.matchesKey(keyCode, scanCode)) {
			client.getHudRenderer().stepForward(Screen.hasControlDown() ? 10 : 1);
		} else
		if (KeyBindings.STEP_BACKWARD.matchesKey(keyCode, scanCode)) {
			client.getHudRenderer().stepBackward(Screen.hasControlDown() ? 10 : 1);
		} else
		if (KeyBindings.TOGGLE_HUD.matchesKey(keyCode, scanCode)) {
			client.toggleHud();
		} else
		if (KeyBindings.OPEN_MULTIMETER_SCREEN.matchesKey(keyCode, scanCode)) {
			MinecraftClient minecraftClient = client.getMinecraftClient();
			Screen screen = minecraftClient.currentScreen;
			
			if (screen != null && screen instanceof MultimeterScreen) {
				minecraftClient.openScreen(null);
			}
		} else {
			return false;
		}
		
		return true;
	}
}
