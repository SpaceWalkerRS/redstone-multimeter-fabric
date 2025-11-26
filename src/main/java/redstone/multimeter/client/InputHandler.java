package redstone.multimeter.client;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;

import redstone.multimeter.client.gui.element.input.KeyEvent;
import redstone.multimeter.client.gui.element.input.MouseEvent;
import redstone.multimeter.client.gui.screen.MultimeterScreen;
import redstone.multimeter.client.gui.screen.OptionsScreen;
import redstone.multimeter.client.gui.screen.RSMMScreen;
import redstone.multimeter.client.gui.screen.TickPhaseTreeScreen;
import redstone.multimeter.common.meter.event.EventType;

public class InputHandler {

	public static boolean isControlDown() {
		return MultimeterClient.MINECRAFT.hasControlDown();
	}

	public static boolean isShiftDown() {
		return MultimeterClient.MINECRAFT.hasShiftDown();
	}

	public static boolean isAltDown() {
		return MultimeterClient.MINECRAFT.hasAltDown();
	}

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
			client.getHud().toggleTickMarker(isControlDown());
		}
		while (Keybinds.STEP_BACKWARD.consumeClick()) {
			client.getHud().stepBackward(isControlDown());
		}
		while (Keybinds.STEP_FORWARD.consumeClick()) {
			client.getHud().stepForward(isControlDown());
		}
	}

	public boolean handleHotbarKeybinds(int slot) {
		if (!client.isConnected()) {
			return false;
		}

		slot++; // slots are 1-indexed

		if (Keybinds.LOAD_METER_GROUP.isDown()) {
			boolean success = client.getSavedMeterGroupsManager().loadSlot(slot);

			if (success) {
				client.getTutorial().onMeterGroupLoaded(slot);
			}

			return success;
		}
		if (Keybinds.SAVE_METER_GROUP.isDown()) {
			boolean success = client.getSavedMeterGroupsManager().saveSlot(slot);

			if (success) {
				client.getTutorial().onMeterGroupSaved(slot);
			}

			return success;
		}

		return false;
	}

	public boolean handleMouseScroll(double scrollX, double scrollY) {
		if (Keybinds.SCROLL_HUD.isDown() && client.isHudActive() && client.getHud().isPaused()) {
			int scroll = (int) Math.round(scrollY);
			boolean forward = (scroll < 0);

			if (scroll != 0) {
				client.getHud().scroll(Math.abs(scroll), forward);
			}
		} else {
			return false;
		}

		return true;
	}

	// Methods for handling keybinds while the client has a screen open

	public boolean mouseClick(RSMMScreen screen, MouseEvent.Click event) {
		if (Keybinds.matches(Keybinds.OPEN_MULTIMETER_SCREEN, event)) {
			if (screen instanceof MultimeterScreen) {
				screen.close();
			} else {
				Minecraft minecraft = client.getMinecraft();

				if (minecraft.player != null) {
					client.openScreen(new MultimeterScreen());
				}
			}
		} else if (Keybinds.matches(Keybinds.OPEN_OPTIONS_MENU, event)) {
			if (screen instanceof OptionsScreen) {
				screen.close();
			} else {
				client.openScreen(new OptionsScreen());
			}
		} else if (Keybinds.matches(Keybinds.VIEW_TICK_PHASE_TREE, event)) {
			if (!client.isConnected()) {
				return false;
			} else if (screen instanceof TickPhaseTreeScreen) {
				screen.close();
			} else {
				client.openScreen(new TickPhaseTreeScreen());
			}
		} else if (screen instanceof MultimeterScreen) {
			if (Keybinds.matches(Keybinds.PAUSE_TIMELINE, event)) {
				client.getHud().togglePaused();
			} else if (Keybinds.matches(Keybinds.TOGGLE_FOCUS_MODE, event)) {
				client.getHud().toggleFocusMode();
			} else if (Keybinds.matches(Keybinds.TOGGLE_MARKER, event)) {
				client.getHud().toggleTickMarker(isControlDown());
			} else if (Keybinds.matches(Keybinds.STEP_BACKWARD, event)) {
				client.getHud().stepBackward(isControlDown());
			} else if (Keybinds.matches(Keybinds.STEP_FORWARD, event)) {
				client.getHud().stepForward(isControlDown());
			} else {
				return false;
			}
		} else {
			return false;
		}

		return true;
	}

	public boolean keyPress(RSMMScreen screen, KeyEvent.Press event) {
		if (Keybinds.matches(Keybinds.OPEN_MULTIMETER_SCREEN, event)) {
			if (screen instanceof MultimeterScreen) {
				screen.close();
			} else {
				Minecraft minecraft = client.getMinecraft();

				if (minecraft.player != null) {
					client.openScreen(new MultimeterScreen());
				}
			}
		} else if (Keybinds.matches(Keybinds.OPEN_OPTIONS_MENU, event)) {
			if (screen instanceof OptionsScreen) {
				screen.close();
			} else {
				client.openScreen(new OptionsScreen());
			}
		} else if (Keybinds.matches(Keybinds.VIEW_TICK_PHASE_TREE, event)) {
			if (!client.isConnected()) {
				return false;
			} else if (screen instanceof TickPhaseTreeScreen) {
				screen.close();
			} else {
				client.openScreen(new TickPhaseTreeScreen());
			}
		} else if (screen instanceof MultimeterScreen) {
			if (Keybinds.matches(Keybinds.PAUSE_TIMELINE, event)) {
				client.getHud().togglePaused();
			} else if (Keybinds.matches(Keybinds.TOGGLE_FOCUS_MODE, event)) {
				client.getHud().toggleFocusMode();
			} else if (Keybinds.matches(Keybinds.TOGGLE_MARKER, event)) {
				client.getHud().toggleTickMarker(isControlDown());
			} else if (Keybinds.matches(Keybinds.STEP_BACKWARD, event)) {
				client.getHud().stepBackward(isControlDown());
			} else if (Keybinds.matches(Keybinds.STEP_FORWARD, event)) {
				client.getHud().stepForward(isControlDown());
			} else {
				return false;
			}
		} else {
			return false;
		}

		return true;
	}

	public boolean mouseScroll(RSMMScreen screen, MouseEvent.Scroll event) {
		if (screen instanceof MultimeterScreen) {
			if (Keybinds.isPressed(Keybinds.SCROLL_HUD)) {
				client.getHud().scroll((int)Math.round(event.scrollY()), true);
			} else {
				return false;
			}
		} else {
			return false;
		}

		return true;
	}
}
