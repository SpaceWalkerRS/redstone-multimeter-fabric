package rsmm.fabric.client;

import net.minecraft.client.gui.screen.Screen;
import rsmm.fabric.common.log.MeterLogs;

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
		if (KeyBindings.PRINT.wasPressed()) {
			MeterLogs.TEST();
		}
	}
}
