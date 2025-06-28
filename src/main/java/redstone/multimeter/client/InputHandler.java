package redstone.multimeter.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.options.KeyBinding;

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
		if (!Keybinds.LOAD_METER_GROUP.pressed && !Keybinds.SAVE_METER_GROUP.pressed) {
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
			KeyBinding keybind = Keybinds.TOGGLE_EVENT_TYPES[index];

			while (keybind.consumeClick()) {
				client.toggleEventType(EventType.byIndex(index));
			}
		}

		if (!client.isHudActive()) {
			return;
		}

		while (Keybinds.PAUSE_METERS.consumeClick()) {
			client.getHud().togglePaused();
		}
		while (Keybinds.TOGGLE_FOCUS_MODE.consumeClick()) {
			client.getHud().toggleFocusMode();
		}
		while (Keybinds.TOGGLE_MARKER.consumeClick()) {
			client.getHud().toggleTickMarker(Screen.isControlDown());
		}
		while (Keybinds.STEP_BACKWARD.consumeClick()) {
			client.getHud().stepBackward(Screen.isControlDown());
		}
		while (Keybinds.STEP_FORWARD.consumeClick()) {
			client.getHud().stepForward(Screen.isControlDown());
		}
	}

	public boolean handleHotbarKeybinds(int slot) {
		if (!client.isConnected()) {
			return false;
		}

		slot++; // slots are 1-indexed

		if (Keybinds.LOAD_METER_GROUP.pressed) {
			return client.getSavedMeterGroupsManager().loadSlot(slot);
		}
		if (Keybinds.SAVE_METER_GROUP.pressed) {
			return client.getSavedMeterGroupsManager().saveSlot(slot);
		}

		return false;
	}

	public boolean handleMouseScroll(double scrollX, double scrollY) {
		if (Keybinds.SCROLL_HUD.pressed && client.isHudActive() && client.getHud().isPaused()) {
			client.getHud().scroll((int)Math.round(scrollY), true);
		} else {
			return false;
		}

		return true;
	}

	// Methods for handling keybinds while the client has a screen open

	public boolean mouseClick(RSMMScreen screen, double mouseX, double mouseY, int button) {
		if (Keybinds.matchesButton(Keybinds.OPEN_MULTIMETER_SCREEN, button)) {
			if (screen instanceof MultimeterScreen) {
				screen.close();
			} else {
				Minecraft minecraft = client.getMinecraft();

				if (minecraft.player != null) {
					client.openScreen(new MultimeterScreen());
				}
			}
		} else if (Keybinds.matchesButton(Keybinds.OPEN_OPTIONS_MENU, button)) {
			if (screen instanceof OptionsScreen) {
				screen.close();
			} else {
				client.openScreen(new OptionsScreen());
			}
		} else if (Keybinds.matchesButton(Keybinds.VIEW_TICK_PHASE_TREE, button)) {
			if (!client.isConnected()) {
				return false;
			} else if (screen instanceof TickPhaseTreeScreen) {
				screen.close();
			} else {
				client.openScreen(new TickPhaseTreeScreen());
			}
		} else if (screen instanceof MultimeterScreen) {
			if (Keybinds.matchesButton(Keybinds.PAUSE_METERS, button)) {
				client.getHud().togglePaused();
			} else if (Keybinds.matchesButton(Keybinds.TOGGLE_FOCUS_MODE, button)) {
				client.getHud().toggleFocusMode();
			} else if (Keybinds.matchesButton(Keybinds.TOGGLE_MARKER, button)) {
				client.getHud().toggleTickMarker(Screen.isControlDown());
			} else if (Keybinds.matchesButton(Keybinds.STEP_BACKWARD, button)) {
				client.getHud().stepBackward(Screen.isControlDown());
			} else if (Keybinds.matchesButton(Keybinds.STEP_FORWARD, button)) {
				client.getHud().stepForward(Screen.isControlDown());
			} else {
				return false;
			}
		} else {
			return false;
		}

		return true;
	}

	public boolean keyPress(RSMMScreen screen, int keyCode) {
		if (Keybinds.matchesKey(Keybinds.OPEN_MULTIMETER_SCREEN, keyCode)) {
			if (screen instanceof MultimeterScreen) {
				screen.close();
			} else {
				Minecraft minecraft = client.getMinecraft();

				if (minecraft.player != null) {
					client.openScreen(new MultimeterScreen());
				}
			}
		} else if (Keybinds.matchesKey(Keybinds.OPEN_OPTIONS_MENU, keyCode)) {
			if (screen instanceof OptionsScreen) {
				screen.close();
			} else {
				client.openScreen(new OptionsScreen());
			}
		} else if (Keybinds.matchesKey(Keybinds.VIEW_TICK_PHASE_TREE, keyCode)) {
			if (!client.isConnected()) {
				return false;
			} else if (screen instanceof TickPhaseTreeScreen) {
				screen.close();
			} else {
				client.openScreen(new TickPhaseTreeScreen());
			}
		} else if (screen instanceof MultimeterScreen) {
			if (Keybinds.matchesKey(Keybinds.PAUSE_METERS, keyCode)) {
				client.getHud().togglePaused();
			} else if (Keybinds.matchesKey(Keybinds.TOGGLE_FOCUS_MODE, keyCode)) {
				client.getHud().toggleFocusMode();
			} else if (Keybinds.matchesKey(Keybinds.TOGGLE_MARKER, keyCode)) {
				client.getHud().toggleTickMarker(Screen.isControlDown());
			} else if (Keybinds.matchesKey(Keybinds.STEP_BACKWARD, keyCode)) {
				client.getHud().stepBackward(Screen.isControlDown());
			} else if (Keybinds.matchesKey(Keybinds.STEP_FORWARD, keyCode)) {
				client.getHud().stepForward(Screen.isControlDown());
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

	private boolean isPressed(KeyBinding keybind) {
		return Keybinds.isPressed(keybind);
	}
}
