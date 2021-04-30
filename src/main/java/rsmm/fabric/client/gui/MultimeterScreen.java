package rsmm.fabric.client.gui;

import org.lwjgl.glfw.GLFW;

import rsmm.fabric.client.KeyBindings;
import rsmm.fabric.client.MultimeterClient;
import rsmm.fabric.client.gui.element.HudElement;
import rsmm.fabric.client.gui.element.RSMMScreen;

public class MultimeterScreen extends RSMMScreen {
	
	public MultimeterScreen(MultimeterClient client) {
		super(client);
	}
	
	@Override
	public boolean keyPress(int keyCode, int scanCode, int modifiers) {
		boolean success = super.keyPress(keyCode, scanCode, modifiers);
		
		if (!success) {
			if (keyCode == GLFW.GLFW_KEY_ESCAPE || KeyBindings.OPEN_MULTIMETER_SCREEN.matchesKey(keyCode, scanCode)) {
				onClose();
				return true;
			}
		}
		
		return success;
	}
	
	@Override
	protected void init() {
		addContent(new HudElement(multimeterClient.getHudRenderer(), 0, 0, width));
	}
	
	@Override
	public void onClose() {
		super.onClose();
	}
	
	@Override
	public void tick() {
		
	}
	
	@Override
	public boolean isPauseScreen() {
		return false;
	}
}
