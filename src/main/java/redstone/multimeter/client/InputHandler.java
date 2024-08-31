package redstone.multimeter.client;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;

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

	// Methods for handling keybinds in-game

	public void handleKeybinds() {
		if (!client.isConnected()) {
			return;
		}

		while (Keybinds.OPEN_MULTIMETER_SCREEN.consumeClick()) {
			client.openScreen(new MultimeterScreen(client));
		}
		while (Keybinds.OPEN_OPTIONS_MENU.consumeClick()) {
			client.openScreen(new OptionsScreen(client));
		}
		while (Keybinds.VIEW_TICK_PHASE_TREE.consumeClick()) {
			client.openScreen(new TickPhaseTreeScreen(client));
		}

		if (!client.hasSubscription()) {
			return;
		}

		while (Keybinds.TOGGLE_METER.consumeClick()) {
			client.toggleMeter();
		}
		while (Keybinds.RESET_METER.consumeClick()) {
			client.resetMeter();
		}
		while (Keybinds.TOGGLE_HUD.consumeClick()) {
			client.toggleHud();
		}
		while (Keybinds.PRINT_LOGS.consumeClick()) {
			client.togglePrinter();
		}
		while (Keybinds.OPEN_METER_CONTROLS.consumeClick()) {
			client.openMeterControls();
		}
		for (int index = 0; index < Keybinds.TOGGLE_EVENT_TYPES.length; index++) {
			KeyMapping keybind = Keybinds.TOGGLE_EVENT_TYPES[index];

			while (keybind.consumeClick()) {
				client.toggleEventType(EventType.byIndex(index));
			}
		}

		if (!client.isHudActive()) {
			return;
		}

		while (Keybinds.PAUSE_METERS.consumeClick()) {
			client.getHud().pause();
		}
		while (Keybinds.TOGGLE_MARKER.consumeClick()) {
			client.getHud().toggleTickMarker(Screen.hasControlDown());
		}
		while (Keybinds.STEP_BACKWARD.consumeClick()) {
			client.getHud().stepBackward(Screen.hasControlDown() ? 10 : 1);
		}
		while (Keybinds.STEP_FORWARD.consumeClick()) {
			client.getHud().stepForward(Screen.hasControlDown() ? 10 : 1);
		}
	}

	public boolean handleHotbarKeybinds(int slot) {
		if (!client.isConnected()) {
			return false;
		}

		slot++; // slots are 1-indexed

		if (Keybinds.LOAD_METER_GROUP.isDown()) {
			client.getSavedMeterGroupsManager().loadSlot(slot);
			return true;
		} else if (Keybinds.SAVE_METER_GROUP.isDown()) {
			client.getSavedMeterGroupsManager().saveSlot(slot);
			return true;
		}

		return false;
	}

	public boolean handleMouseScroll(double scrollX, double scrollY) {
		if (Keybinds.SCROLL_HUD.isDown() && client.isHudActive()) {
			client.getHud().stepBackward((int)Math.round(scrollY));
		} else {
			return false;
		}

		return true;
	}

	// Methods for handling keybinds while the client has a screen open

	public boolean mouseClick(RSMMScreen screen, double mouseX, double mouseY, int button) {
		if (Keybinds.OPEN_MULTIMETER_SCREEN.matchesMouse(button)) {
			if (screen instanceof MultimeterScreen) {
				screen.close();
			} else {
				Minecraft minecraft = client.getMinecraft();

				if (minecraft.player != null) {
					client.openScreen(new MultimeterScreen(client));
				}
			}
		} else if (Keybinds.OPEN_OPTIONS_MENU.matchesMouse(button)) {
			if (screen instanceof OptionsScreen) {
				screen.close();
			} else {
				client.openScreen(new OptionsScreen(client));
			}
		} else if (Keybinds.VIEW_TICK_PHASE_TREE.matchesMouse(button)) {
			if (!client.isConnected()) {
				return false;
			} else if (screen instanceof TickPhaseTreeScreen) {
				screen.close();
			} else {
				client.openScreen(new TickPhaseTreeScreen(client));
			}
		} else if (screen instanceof MultimeterScreen) {
			if (Keybinds.PAUSE_METERS.matchesMouse(button)) {
				client.getHud().pause();
			} else if (Keybinds.TOGGLE_MARKER.matchesMouse(button)) {
				client.getHud().toggleTickMarker(Screen.hasControlDown());
			} else if (Keybinds.STEP_BACKWARD.matchesMouse(button)) {
				client.getHud().stepBackward(Screen.hasControlDown() ? 10 : 1);
			} else if (Keybinds.STEP_FORWARD.matchesMouse(button)) {
				client.getHud().stepForward(Screen.hasControlDown() ? 10 : 1);
			} else {
				return false;
			}
		} else {
			return false;
		}

		return true;
	}

	public boolean keyPress(RSMMScreen screen, int keyCode, int scanCode, int modifiers) {
		if (Keybinds.OPEN_MULTIMETER_SCREEN.matches(keyCode, scanCode)) {
			if (screen instanceof MultimeterScreen) {
				screen.close();
			} else {
				Minecraft minecraft = client.getMinecraft();

				if (minecraft.player != null) {
					client.openScreen(new MultimeterScreen(client));
				}
			}
		} else if (Keybinds.OPEN_OPTIONS_MENU.matches(keyCode, scanCode)) {
			if (screen instanceof OptionsScreen) {
				screen.close();
			} else {
				client.openScreen(new OptionsScreen(client));
			}
		} else if (Keybinds.VIEW_TICK_PHASE_TREE.matches(keyCode, scanCode)) {
			if (!client.isConnected()) {
				return false;
			} else if (screen instanceof TickPhaseTreeScreen) {
				screen.close();
			} else {
				client.openScreen(new TickPhaseTreeScreen(client));
			}
		} else if (screen instanceof MultimeterScreen) {
			if (Keybinds.PAUSE_METERS.matches(keyCode, scanCode)) {
				client.getHud().pause();
			} else if (Keybinds.TOGGLE_MARKER.matches(keyCode, scanCode)) {
				client.getHud().toggleTickMarker(Screen.hasControlDown());
			} else if (Keybinds.STEP_BACKWARD.matches(keyCode, scanCode)) {
				client.getHud().stepBackward(Screen.hasControlDown() ? 10 : 1);
			} else if (Keybinds.STEP_FORWARD.matches(keyCode, scanCode)) {
				client.getHud().stepForward(Screen.hasControlDown() ? 10 : 1);
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
			if (isPressed(Keybinds.SCROLL_HUD)) {
				client.getHud().stepBackward((int)Math.round(scrollY));
			} else {
				return false;
			}
		} else {
			return false;
		}

		return true;
	}

	private boolean isPressed(KeyMapping keybind) {
		return Keybinds.isPressed(client.getMinecraft(), keybind);
	}
}
