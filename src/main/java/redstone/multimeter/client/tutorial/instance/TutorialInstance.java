package redstone.multimeter.client.tutorial.instance;

import redstone.multimeter.client.gui.element.tutorial.TutorialToast;
import redstone.multimeter.client.tutorial.TutorialListener;
import redstone.multimeter.client.tutorial.TutorialStep;

public interface TutorialInstance extends TutorialListener {

	TutorialToast createToast();

	void init();

	void tick();

	boolean isCompleted();

	TutorialStep nextStep();

}
