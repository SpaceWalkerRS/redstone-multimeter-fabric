package redstone.multimeter.client.tutorial.instance;

import redstone.multimeter.client.gui.element.tutorial.TutorialToast;
import redstone.multimeter.client.tutorial.Tutorial;
import redstone.multimeter.client.tutorial.TutorialListener;
import redstone.multimeter.client.tutorial.TutorialStep;

public abstract class TutorialInstance implements TutorialListener {

	protected final Tutorial tutorial;
	protected final TutorialToast toast;

	protected boolean completed = false;

	protected TutorialInstance(Tutorial tutorial) {
		this.tutorial = tutorial;
		this.toast = createToast();
	}

	protected abstract TutorialToast createToast();

	public abstract void tick();

	public void start() {
		if (toast != null) {
			tutorial.getMinecraft().getToastManager().addToast(toast);
		}
	}

	public void stop() {
		if (toast != null) {
			toast.hide();
		}
	}

	public boolean isCompleted() {
		return completed;
	}

	public abstract TutorialStep getNextStep();

}
