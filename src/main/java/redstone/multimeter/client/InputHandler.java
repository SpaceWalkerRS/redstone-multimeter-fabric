package redstone.multimeter.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.options.KeyBinding;

import redstone.multimeter.client.gui.MultimeterScreen;
import redstone.multimeter.client.gui.OptionsScreen;
import redstone.multimeter.client.gui.element.RSMMScreen;
import redstone.multimeter.common.meter.event.EventType;

public class InputHandler {
	
	private final MultimeterClient client;
	
	public InputHandler(MultimeterClient client) {
		this.client = client;
	}
	
	
	// Methods for handling keybindings in-game
	
	public void handleKeyBindings() {
		if (!client.isConnected()) {
			return;
		}
		
		while (KeyBindings.OPEN_MULTIMETER_SCREEN.wasPressed()) {
			client.openScreen(new MultimeterScreen(client));
		}
		while (KeyBindings.OPEN_OPTIONS_MENU.wasPressed()) {
			client.openScreen(new OptionsScreen(client));
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
		while (KeyBindings.OPEN_METER_CONTROLS.wasPressed()) {
			client.openMeterControls();
		}
		for (int index = 0; index < KeyBindings.TOGGLE_EVENT_TYPES.length; index++) {
			KeyBinding keyBinding = KeyBindings.TOGGLE_EVENT_TYPES[index];
			
			while (keyBinding.wasPressed()) {
				client.toggleEventType(EventType.fromIndex(index));
			}
		}
	}
	
	public boolean handleMouseScroll(double scrollX, double scrollY) {
		if (KeyBindings.SCROLL_HUD.isPressed() && client.isHudActive()) {
			client.getHUD().stepBackward((int)Math.round(scrollY));
		} else {
			return false;
		}
		
		return true;
	}
	
	
	// Methods for handling keybindings while the client has a screen open
	
	public boolean mouseClick(RSMMScreen screen, double mouseX, double mouseY, int button) {
		if (KeyBindings.isBoundToButton(KeyBindings.OPEN_MULTIMETER_SCREEN, button)) {
			if (screen instanceof MultimeterScreen) {
				screen.close();
			} else {
				MinecraftClient minecraftClient = client.getMinecraftClient();
				
				if (minecraftClient.field_3805 != null) {
					client.openScreen(new MultimeterScreen(client));
				}
			}
		} else
		if (KeyBindings.isBoundToButton(KeyBindings.OPEN_OPTIONS_MENU, button)) {
			if (screen instanceof OptionsScreen) {
				screen.close();
			} else {
				client.openScreen(new OptionsScreen(client));
			}
		} else
		if (screen instanceof MultimeterScreen) {
			if (KeyBindings.isBoundToButton(KeyBindings.PAUSE_METERS, button)) {
				client.getHUD().pause();
			} else
			if (KeyBindings.isBoundToButton(KeyBindings.STEP_BACKWARD, button)) {
				client.getHUD().stepBackward(Screen.hasControlDown() ? 10 : 1);
			} else
			if (KeyBindings.isBoundToButton(KeyBindings.STEP_FORWARD, button)) {
				client.getHUD().stepForward(Screen.hasControlDown() ? 10 : 1);
			} else {
				return false;
			}
		} else {
			return false;
		}
		
		return true;
	}
	
	public boolean keyPress(RSMMScreen screen, int key) {
		if (KeyBindings.isBoundToKey(KeyBindings.OPEN_MULTIMETER_SCREEN, key)) {
			if (screen instanceof MultimeterScreen) {
				screen.close();
			} else {
				MinecraftClient minecraftClient = client.getMinecraftClient();
				
				if (minecraftClient.field_3805 != null) {
					client.openScreen(new MultimeterScreen(client));
				}
			}
		} else
		if (KeyBindings.isBoundToKey(KeyBindings.OPEN_OPTIONS_MENU, key)) {
			if (screen instanceof OptionsScreen) {
				screen.close();
			} else {
				client.openScreen(new OptionsScreen(client));
			}
		} else
		if (screen instanceof MultimeterScreen) {
			if (KeyBindings.isBoundToKey(KeyBindings.PAUSE_METERS, key)) {
				client.getHUD().pause();
			} else
			if (KeyBindings.isBoundToKey(KeyBindings.STEP_BACKWARD, key)) {
				client.getHUD().stepBackward(Screen.hasControlDown() ? 10 : 1);
			} else
			if (KeyBindings.isBoundToKey(KeyBindings.STEP_FORWARD, key)) {
				client.getHUD().stepForward(Screen.hasControlDown() ? 10 : 1);
			} else {
				return false;
			}
		} else {
			return false;
		}
		
		return true;
	}
	
	public boolean mouseScroll(RSMMScreen screen, double scrollX, double scrollY) {
		if (screen instanceof MultimeterScreen) {
			if (KeyBindings.isPressed(KeyBindings.SCROLL_HUD)) {
				client.getHUD().stepBackward((int)Math.round(scrollY));
			} else {
				return false;
			}
		} else {
			return false;
		}
		
		return true;
	}
}
