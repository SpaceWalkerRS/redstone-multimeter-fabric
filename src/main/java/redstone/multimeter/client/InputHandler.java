package redstone.multimeter.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.settings.KeyBinding;

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
		
		while (KeyBindings.OPEN_MULTIMETER_SCREEN.isPressed()) {
			client.openScreen(new MultimeterScreen(client));
		}
		while (KeyBindings.OPEN_OPTIONS_MENU.isPressed()) {
			client.openScreen(new OptionsScreen(client));
		}
		while (KeyBindings.VIEW_TICK_PHASE_TREE.isPressed()) {
			client.openScreen(new TickPhaseTreeScreen(client));
		}
		
		if (!client.hasSubscription()) {
			return;
		}
		
		while (KeyBindings.TOGGLE_METER.isPressed()) {
			client.toggleMeter();
		}
		while (KeyBindings.RESET_METER.isPressed()) {
			client.resetMeter();
		}
		while (KeyBindings.TOGGLE_HUD.isPressed()) {
			client.toggleHud();
		}
		while (KeyBindings.PRINT_LOGS.isPressed()) {
			client.togglePrinter();
		}
		while (KeyBindings.OPEN_METER_CONTROLS.isPressed()) {
			client.openMeterControls();
		}
		for (int index = 0; index < KeyBindings.TOGGLE_EVENT_TYPES.length; index++) {
			KeyBinding keyBinding = KeyBindings.TOGGLE_EVENT_TYPES[index];
			
			while (keyBinding.isPressed()) {
				client.toggleEventType(EventType.fromIndex(index));
			}
		}
		
		if (!client.isHudActive()) {
			return;
		}
		
		while (KeyBindings.PAUSE_METERS.isPressed()) {
			client.getHUD().pause();
		}
		while (KeyBindings.TOGGLE_MARKER.isPressed()) {
			client.getHUD().toggleTickMarker(GuiScreen.isCtrlKeyDown());
		}
		while (KeyBindings.STEP_BACKWARD.isPressed()) {
			client.getHUD().stepBackward(GuiScreen.isCtrlKeyDown() ? 10 : 1);
		}
		while (KeyBindings.STEP_FORWARD.isPressed()) {
			client.getHUD().stepForward(GuiScreen.isCtrlKeyDown() ? 10 : 1);
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
				Minecraft minecraftClient = client.getMinecraftClient();
				
				if (minecraftClient.player != null) {
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
		if (KeyBindings.isBoundToButton(KeyBindings.VIEW_TICK_PHASE_TREE, button)) {
			if (!client.isConnected()) {
				return false;
			} else if (screen instanceof TickPhaseTreeScreen) {
				screen.close();
			} else {
				client.openScreen(new TickPhaseTreeScreen(client));
			}
		} else
		if (screen instanceof MultimeterScreen) {
			if (KeyBindings.isBoundToButton(KeyBindings.PAUSE_METERS, button)) {
				client.getHUD().pause();
			} else
			if (KeyBindings.isBoundToButton(KeyBindings.TOGGLE_MARKER, button)) {
				client.getHUD().toggleTickMarker(GuiScreen.isCtrlKeyDown());
			} else
			if (KeyBindings.isBoundToButton(KeyBindings.STEP_BACKWARD, button)) {
				client.getHUD().stepBackward(GuiScreen.isCtrlKeyDown() ? 10 : 1);
			} else
			if (KeyBindings.isBoundToButton(KeyBindings.STEP_FORWARD, button)) {
				client.getHUD().stepForward(GuiScreen.isCtrlKeyDown() ? 10 : 1);
			} else {
				return false;
			}
		} else {
			return false;
		}
		
		return true;
	}
	
	public boolean keyPress(RSMMScreen screen, int keyCode) {
		if (KeyBindings.isBoundToKey(KeyBindings.OPEN_MULTIMETER_SCREEN, keyCode)) {
			if (screen instanceof MultimeterScreen) {
				screen.close();
			} else {
				Minecraft minecraftClient = client.getMinecraftClient();
				
				if (minecraftClient.player != null) {
					client.openScreen(new MultimeterScreen(client));
				}
			}
		} else
		if (KeyBindings.isBoundToKey(KeyBindings.OPEN_OPTIONS_MENU, keyCode)) {
			if (screen instanceof OptionsScreen) {
				screen.close();
			} else {
				client.openScreen(new OptionsScreen(client));
			}
		} else
		if (KeyBindings.isBoundToKey(KeyBindings.VIEW_TICK_PHASE_TREE, keyCode)) {
			if (!client.isConnected()) {
				return false;
			} else if (screen instanceof TickPhaseTreeScreen) {
				screen.close();
			} else {
				client.openScreen(new TickPhaseTreeScreen(client));
			}
		} else
		if (screen instanceof MultimeterScreen) {
			if (KeyBindings.isBoundToKey(KeyBindings.PAUSE_METERS, keyCode)) {
				client.getHUD().pause();
			} else
			if (KeyBindings.isBoundToKey(KeyBindings.TOGGLE_MARKER, keyCode)) {
				client.getHUD().toggleTickMarker(GuiScreen.isCtrlKeyDown());
			} else
			if (KeyBindings.isBoundToKey(KeyBindings.STEP_BACKWARD, keyCode)) {
				client.getHUD().stepBackward(GuiScreen.isCtrlKeyDown() ? 10 : 1);
			} else
			if (KeyBindings.isBoundToKey(KeyBindings.STEP_FORWARD, keyCode)) {
				client.getHUD().stepForward(GuiScreen.isCtrlKeyDown() ? 10 : 1);
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
