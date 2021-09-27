package rsmm.fabric.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.option.KeyBinding;

import rsmm.fabric.client.gui.MultimeterScreen;
import rsmm.fabric.common.event.EventType;

public class InputHandler {
	
	private final MultimeterClient client;
	
	public InputHandler(MultimeterClient client) {
		this.client = client;
	}
	
	public void handleInputEvents() {
		if (!client.isConnected()) {
			return;
		}
		
		while (KeyBindings.TOGGLE_METER.wasPressed()) {
			client.toggleMeter();
		}
		while (KeyBindings.RESET_METER.wasPressed()) {
			client.resetMeter();
		}
		while (KeyBindings.PAUSE_METERS.wasPressed()) {
			client.getHudRenderer().pause();
		}
		while (KeyBindings.STEP_FORWARD.wasPressed()) {
			client.getHudRenderer().stepForward(Screen.hasControlDown() ? 10 : 1);
		}
		while (KeyBindings.STEP_BACKWARD.wasPressed()) {
			client.getHudRenderer().stepBackward(Screen.hasControlDown() ? 10 : 1);
		}
		while (KeyBindings.TOGGLE_HUD.wasPressed()) {
			client.toggleHud();
		}
		while (KeyBindings.OPEN_MULTIMETER_SCREEN.wasPressed()) {
			MinecraftClient minecraftClient = client.getMinecraftClient();
			
			if (minecraftClient.currentScreen == null) {
				minecraftClient.setScreen(new MultimeterScreen(client));
			}
		}
		for (int index = 0; index < KeyBindings.TOGGLE_EVENT_TYPES.length; index++) {
			KeyBinding keyBinding = KeyBindings.TOGGLE_EVENT_TYPES[index];
			
			while (keyBinding.wasPressed()) {
				client.toggleEventType(EventType.fromIndex(index));
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
			if (client.hasMultimeterScreenOpen()) {
				client.getMinecraftClient().setScreen(null);
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
			if (client.hasMultimeterScreenOpen()) {
				client.getMinecraftClient().setScreen(null);
			}
		} else {
			return false;
		}
		
		return true;
	}
}
