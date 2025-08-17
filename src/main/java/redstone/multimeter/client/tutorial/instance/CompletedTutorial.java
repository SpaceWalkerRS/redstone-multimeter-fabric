package redstone.multimeter.client.tutorial.instance;

import redstone.multimeter.client.gui.element.tutorial.TutorialToast;
import redstone.multimeter.client.tutorial.TutorialStep;

public class CompletedTutorial implements TutorialInstance {

	@Override
	public TutorialToast createToast() {
		return null;
	}

	@Override
	public void init() {
	}

	@Override
	public void tick() {
	}

	@Override
	public boolean isCompleted() {
		return true;
	}

	@Override
	public TutorialStep nextStep() {
		return TutorialStep.NONE;
	}
}
