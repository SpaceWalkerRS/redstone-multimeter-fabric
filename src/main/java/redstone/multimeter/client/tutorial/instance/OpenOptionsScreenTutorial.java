package redstone.multimeter.client.tutorial.instance;

import redstone.multimeter.client.Keybinds;
import redstone.multimeter.client.gui.element.tutorial.TutorialToast;
import redstone.multimeter.client.gui.screen.OptionsScreen;
import redstone.multimeter.client.gui.screen.RSMMScreen;
import redstone.multimeter.client.gui.text.Texts;
import redstone.multimeter.client.tutorial.TutorialStep;

public class OpenOptionsScreenTutorial implements TutorialInstance {

	private boolean completed;

	@Override
	public TutorialToast createToast() {
		return new TutorialToast(
			TutorialStep.OPEN_OPTIONS_SCREEN.getName(),
			TutorialStep.OPEN_OPTIONS_SCREEN.getDescription(
				Texts.keybind(Keybinds.OPEN_OPTIONS_MENU)
			)
		);
	}

	@Override
	public void onScreenOpened(RSMMScreen screen) {
		if (screen instanceof OptionsScreen) {
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
		return TutorialStep.JOIN_METER_GROUP;
	}
}
