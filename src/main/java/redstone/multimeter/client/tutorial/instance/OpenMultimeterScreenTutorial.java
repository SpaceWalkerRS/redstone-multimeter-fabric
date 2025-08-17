package redstone.multimeter.client.tutorial.instance;

import redstone.multimeter.client.Keybinds;
import redstone.multimeter.client.gui.element.tutorial.TutorialToast;
import redstone.multimeter.client.gui.screen.MultimeterScreen;
import redstone.multimeter.client.gui.screen.RSMMScreen;
import redstone.multimeter.client.gui.text.Texts;
import redstone.multimeter.client.tutorial.TutorialStep;

public class OpenMultimeterScreenTutorial implements TutorialInstance {

	private boolean completed;

	@Override
	public TutorialToast createToast() {
		return new TutorialToast(
			TutorialStep.OPEN_MULTIMETER_SCREEN.getName(),
			TutorialStep.OPEN_MULTIMETER_SCREEN.getDescription(
				Texts.keybind(Keybinds.OPEN_MULTIMETER_SCREEN)
			)
		);
	}

	@Override
	public void onScreenOpened(RSMMScreen screen) {
		if (screen instanceof MultimeterScreen) {
			this.completed = true;
		}
	}

	@Override
	public void init() {
	}

	@Override
	public void tick() {
	}

	@Override
	public boolean isCompleted() {
		return this.completed;
	}

	@Override
	public TutorialStep nextStep() {
		return TutorialStep.OPEN_METER_CONTROLS;
	}
}
