package redstone.multimeter.client.tutorial.instance;

import redstone.multimeter.client.gui.element.tutorial.TutorialToast;
import redstone.multimeter.client.tutorial.Tutorial;
import redstone.multimeter.client.tutorial.TutorialStep;

public class CompletedTutorial extends TutorialInstance {

	public CompletedTutorial(Tutorial tutorial) {
		super(tutorial);

		this.completed = true;
	}

	@Override
	protected TutorialToast createToast() {
		return null;
	}

	@Override
	public void tick() {
	}

	@Override
	public TutorialStep getNextStep() {
		return TutorialStep.NONE;
	}
}
