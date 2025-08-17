package redstone.multimeter.client.tutorial.instance;

import redstone.multimeter.client.gui.element.tutorial.TutorialToast;
import redstone.multimeter.client.tutorial.TutorialStep;

public class JoinMeterGroupTutorial implements TutorialInstance {

	private boolean completed;

	@Override
	public void onJoinMeterGroup() {
		this.completed = true;
	}

	@Override
	public void onMeterGroupRefreshed() {
		this.completed = true;
	}

	@Override
	public TutorialToast createToast() {
		return new TutorialToast(
			TutorialStep.JOIN_METER_GROUP.getName(),
			TutorialStep.JOIN_METER_GROUP.getDescription()
		);
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
		return TutorialStep.PLACE_METER;
	}
}
