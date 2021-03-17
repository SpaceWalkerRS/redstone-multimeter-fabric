package rsmm.fabric.client;

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
			client.pauseMeters();
		}
		if (KeyBindings.STEP_FORWARD.wasPressed()) {
			client.stepForward();
		}
		if (KeyBindings.STEP_BACKWARD.wasPressed()) {
			client.stepBackward();
		}
		if (KeyBindings.TOGGLE_HUD.wasPressed()) {
			client.toggleHud();
		}
	}
}
