package redstone.multimeter.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.option.KeyBinding;

import redstone.multimeter.client.gui.screen.MultimeterScreen;
import redstone.multimeter.client.gui.screen.OptionsScreen;
import redstone.multimeter.client.gui.screen.RSMMScreen;
import redstone.multimeter.client.gui.screen.TickPhaseTreeScreen;
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
		while (KeyBindings.VIEW_TICK_PHASE_TREE.wasPressed()) {
			client.openScreen(new TickPhaseTreeScreen(client));
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
		if (KeyBindings.OPEN_MULTIMETER_SCREEN.matchesMouse(button)) {
			if (screen instanceof MultimeterScreen) {
				screen.close();
			} else {
				MinecraftClient minecraftClient = client.getMinecraftClient();
				
				if (minecraftClient.player != null) {
					client.openScreen(new MultimeterScreen(client));
				}
			}
		} else
		if (KeyBindings.OPEN_OPTIONS_MENU.matchesMouse(button)) {
			if (screen instanceof OptionsScreen) {
				screen.close();
			} else {
				client.openScreen(new OptionsScreen(client));
			}
		} else
		if (KeyBindings.VIEW_TICK_PHASE_TREE.matchesMouse(button)) {
			if (!client.isConnected()) {
				return false;
			} else if (screen instanceof TickPhaseTreeScreen) {
				screen.close();
			} else {
				client.openScreen(new TickPhaseTreeScreen(client));
			}
		} else
		if (screen instanceof MultimeterScreen) {
			if (KeyBindings.PAUSE_METERS.matchesMouse(button)) {
				client.getHUD().pause();
			} else
			if (KeyBindings.STEP_BACKWARD.matchesMouse(button)) {
				client.getHUD().stepBackward(Screen.hasControlDown() ? 10 : 1);
			} else
			if (KeyBindings.STEP_FORWARD.matchesMouse(button)) {
				client.getHUD().stepForward(Screen.hasControlDown() ? 10 : 1);
			} else {
				return false;
			}
		} else {
			return false;
		}
		
		return true;
	}
	
	public boolean keyPress(RSMMScreen screen, int keyCode, int scanCode, int modifiers) {
		if (KeyBindings.OPEN_MULTIMETER_SCREEN.matchesKey(keyCode, scanCode)) {
			if (screen instanceof MultimeterScreen) {
				screen.close();
			} else {
				MinecraftClient minecraftClient = client.getMinecraftClient();
				
				if (minecraftClient.player != null) {
					client.openScreen(new MultimeterScreen(client));
				}
			}
		} else
		if (KeyBindings.OPEN_OPTIONS_MENU.matchesKey(keyCode, scanCode)) {
			if (screen instanceof OptionsScreen) {
				screen.close();
			} else {
				client.openScreen(new OptionsScreen(client));
			}
		} else
		if (KeyBindings.VIEW_TICK_PHASE_TREE.matchesKey(keyCode, scanCode)) {
			if (!client.isConnected()) {
				return false;
			} else if (screen instanceof TickPhaseTreeScreen) {
				screen.close();
			} else {
				client.openScreen(new TickPhaseTreeScreen(client));
			}
		} else
		if (screen instanceof MultimeterScreen) {
			if (KeyBindings.PAUSE_METERS.matchesKey(keyCode, scanCode)) {
				client.getHUD().pause();
			} else
			if (KeyBindings.STEP_BACKWARD.matchesKey(keyCode, scanCode)) {
				client.getHUD().stepBackward(Screen.hasControlDown() ? 10 : 1);
			} else
			if (KeyBindings.STEP_FORWARD.matchesKey(keyCode, scanCode)) {
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
			if (isPressed(KeyBindings.SCROLL_HUD)) {
				client.getHUD().stepBackward((int)Math.round(scrollY));
			} else {
				return false;
			}
		} else {
			return false;
		}
		
		return true;
	}
	
	private boolean isPressed(KeyBinding keyBinding) {
		return KeyBindings.isPressed(client.getMinecraftClient(), keyBinding);
	}
}
