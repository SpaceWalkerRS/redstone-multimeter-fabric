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
			client.openScreen(new MultimeterScreen());
		}
		while (Keybinds.OPEN_OPTIONS_MENU.consumeClick()) {
			client.openScreen(new OptionsScreen());
		}
		while (Keybinds.VIEW_TICK_PHASE_TREE.consumeClick()) {
			client.openScreen(new TickPhaseTreeScreen());
		}
		while (Keybinds.LOAD_METER_GROUP.consumeClick()) {
			client.getSavedMeterGroupsManager().setLoading();
		}
		while (Keybinds.SAVE_METER_GROUP.consumeClick()) {
			client.getSavedMeterGroupsManager().setSaving();
		}
		if (!Keybinds.LOAD_METER_GROUP.isDown() && !Keybinds.SAVE_METER_GROUP.isDown()) {
			client.getSavedMeterGroupsManager().setIdle();
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
				client.toggleEventType(EventType.byId(index));
			}
		}

		if (!client.isHudActive()) {
			return;
		}

		while (Keybinds.PAUSE_TIMELINE.consumeClick()) {
			client.getHud().togglePaused();
		}
		while (Keybinds.TOGGLE_FOCUS_MODE.consumeClick()) {
			client.getHud().toggleFocusMode();
		}
		while (Keybinds.TOGGLE_MARKER.consumeClick()) {
			client.getHud().toggleTickMarker(Screen.hasControlDown());
		}
		while (Keybinds.STEP_BACKWARD.consumeClick()) {
			client.getHud().stepBackward(Screen.hasControlDown());
		}
		while (Keybinds.STEP_FORWARD.consumeClick()) {
			client.getHud().stepForward(Screen.hasControlDown());
		}
	}

	public boolean handleHotbarKeybinds(int slot) {
		if (!client.isConnected()) {
			return false;
		}

		slot++; // slots are 1-indexed

		if (Keybinds.LOAD_METER_GROUP.isDown()) {
			return client.getSavedMeterGroupsManager().loadSlot(slot);
		}
		if (Keybinds.SAVE_METER_GROUP.isDown()) {
			return client.getSavedMeterGroupsManager().saveSlot(slot);
		}

		return false;
	}

	public boolean handleMouseScroll(double scrollX, double scrollY) {
		if (Keybinds.SCROLL_HUD.isDown() && client.isHudActive() && client.getHud().isPaused()) {
			client.getHud().scroll((int)Math.round(scrollY), true);
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
					client.openScreen(new MultimeterScreen());
				}
			}
		} else if (Keybinds.OPEN_OPTIONS_MENU.matchesMouse(button)) {
			if (screen instanceof OptionsScreen) {
				screen.close();
			} else {
				client.openScreen(new OptionsScreen());
			}
		} else if (Keybinds.VIEW_TICK_PHASE_TREE.matchesMouse(button)) {
			if (!client.isConnected()) {
				return false;
			} else if (screen instanceof TickPhaseTreeScreen) {
				screen.close();
			} else {
				client.openScreen(new TickPhaseTreeScreen());
			}
		} else if (screen instanceof MultimeterScreen) {
			if (Keybinds.PAUSE_TIMELINE.matchesMouse(button)) {
				client.getHud().togglePaused();
			} else if (Keybinds.TOGGLE_FOCUS_MODE.matchesMouse(button)) {
				client.getHud().toggleFocusMode();
			} else if (Keybinds.TOGGLE_MARKER.matchesMouse(button)) {
				client.getHud().toggleTickMarker(Screen.hasControlDown());
			} else if (Keybinds.STEP_BACKWARD.matchesMouse(button)) {
				client.getHud().stepBackward(Screen.hasControlDown());
			} else if (Keybinds.STEP_FORWARD.matchesMouse(button)) {
				client.getHud().stepForward(Screen.hasControlDown());
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
					client.openScreen(new MultimeterScreen());
				}
			}
		} else if (Keybinds.OPEN_OPTIONS_MENU.matches(keyCode, scanCode)) {
			if (screen instanceof OptionsScreen) {
				screen.close();
			} else {
				client.openScreen(new OptionsScreen());
			}
		} else if (Keybinds.VIEW_TICK_PHASE_TREE.matches(keyCode, scanCode)) {
			if (!client.isConnected()) {
				return false;
			} else if (screen instanceof TickPhaseTreeScreen) {
				screen.close();
			} else {
				client.openScreen(new TickPhaseTreeScreen());
			}
		} else if (screen instanceof MultimeterScreen) {
			if (Keybinds.PAUSE_TIMELINE.matches(keyCode, scanCode)) {
				client.getHud().togglePaused();
			} else if (Keybinds.TOGGLE_FOCUS_MODE.matches(keyCode, scanCode)) {
				client.getHud().toggleFocusMode();
			} else if (Keybinds.TOGGLE_MARKER.matches(keyCode, scanCode)) {
				client.getHud().toggleTickMarker(Screen.hasControlDown());
			} else if (Keybinds.STEP_BACKWARD.matches(keyCode, scanCode)) {
				client.getHud().stepBackward(Screen.hasControlDown());
			} else if (Keybinds.STEP_FORWARD.matches(keyCode, scanCode)) {
				client.getHud().stepForward(Screen.hasControlDown());
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
				client.getHud().scroll((int)Math.round(scrollY), true);
			} else {
				return false;
			}
		} else {
			return false;
		}

		return true;
	}

	private boolean isPressed(KeyMapping keybind) {
		return Keybinds.isPressed(keybind);
	}
}
