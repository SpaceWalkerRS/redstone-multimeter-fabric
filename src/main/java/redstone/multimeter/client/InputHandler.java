package redstone.multimeter.client;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.options.KeyBinding;

import redstone.multimeter.client.gui.MultimeterScreen;
import redstone.multimeter.common.meter.event.EventType;

public class InputHandler {
	
	private final MultimeterClient client;
	
	public InputHandler(MultimeterClient client) {
		this.client = client;
	}
	
	public void handleInputEvents() {
		if (!client.isConnected()) {
			return;
		}
		
		while (KeyBindings.OPEN_MULTIMETER_SCREEN.wasPressed()) {
			client.openScreen(new MultimeterScreen(client));
		}
		
		if (!client.hasSubscription()) {
			return;
		}
		
		while (KeyBindings.TOGGLE_METER.wasPressed()) {
			client.toggleMeter();
		}
		while (KeyBindings.RESET_METER.wasPressed()) {
			client.resetMeter();
		}
		while (KeyBindings.PAUSE_METERS.wasPressed()) {
			client.getHUD().pause();
		}
		while (KeyBindings.STEP_BACKWARD.wasPressed()) {
			client.getHUD().stepBackward(Screen.hasControlDown() ? 10 : 1);
		}
		while (KeyBindings.STEP_FORWARD.wasPressed()) {
			client.getHUD().stepForward(Screen.hasControlDown() ? 10 : 1);
		}
		while (KeyBindings.TOGGLE_HUD.wasPressed()) {
			client.toggleHud();
		}
		while (KeyBindings.PRINT_LOGS.wasPressed()) {
			client.togglePrinter();
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
			client.getHUD().pause();
		} else
		if (KeyBindings.STEP_BACKWARD.matchesMouse(button)) {
			client.getHUD().stepBackward(Screen.hasControlDown() ? 10 : 1);
		} else
		if (KeyBindings.STEP_FORWARD.matchesMouse(button)) {
			client.getHUD().stepForward(Screen.hasControlDown() ? 10 : 1);
		} else
		if (KeyBindings.OPEN_MULTIMETER_SCREEN.matchesMouse(button)) {
			if (client.hasMultimeterScreenOpen()) {
				client.getScreen().close();
			}
		} else {
			return false;
		}
		
		return true;
	}
	
	public boolean keyPress(int keyCode, int scanCode, int modifiers) {
		if (KeyBindings.PAUSE_METERS.matchesKey(keyCode, scanCode)) {
			client.getHUD().pause();
		} else
		if (KeyBindings.STEP_BACKWARD.matchesKey(keyCode, scanCode)) {
			client.getHUD().stepBackward(Screen.hasControlDown() ? 10 : 1);
		} else
		if (KeyBindings.STEP_FORWARD.matchesKey(keyCode, scanCode)) {
			client.getHUD().stepForward(Screen.hasControlDown() ? 10 : 1);
		} else
		if (KeyBindings.OPEN_MULTIMETER_SCREEN.matchesKey(keyCode, scanCode)) {
			if (client.hasMultimeterScreenOpen()) {
				client.getScreen().close();
			}
		} else {
			return false;
		}
		
		return true;
	}
}
