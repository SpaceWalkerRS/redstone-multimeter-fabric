package redstone.multimeter.client.tutorial.instance;

import redstone.multimeter.client.Keybinds;
import redstone.multimeter.client.MultimeterClient;
import redstone.multimeter.client.gui.element.tutorial.StagedTutorialToast;
import redstone.multimeter.client.gui.element.tutorial.TutorialToast;
import redstone.multimeter.client.gui.text.Texts;
import redstone.multimeter.client.meter.ClientMeterGroup;
import redstone.multimeter.client.tutorial.TutorialStep;
import redstone.multimeter.common.meter.Meter;

public class PauseTimelineTutorial implements StagedTutorialInstance {

	private Stage stage;

	@Override
	public TutorialToast createToast() {
		return new StagedTutorialToast(
			this,
			TutorialStep.PAUSE_TIMELINE.getName(),
			TutorialStep.PAUSE_TIMELINE.getDescription(
				Texts.keybind(Keybinds.PAUSE_TIMELINE)
			)
		);
	}

	@Override
	public void onToggleHud(boolean enabled) {
		if (!enabled || this.stage == Stage.ACTIVE_HUD) {
			this.updateStage();
		}
	}

	@Override
	public void onPauseHud(boolean paused) {
		if (!paused || this.stage == Stage.PAUSE_HUD) {
			this.updateStage();
		}
	}

	@Override
	public void onJoinMeterGroup() {
		if (this.stage == Stage.JOIN_METER_GROUP) {
			this.updateStage();
		}
	}

	@Override
	public void onLeaveMeterGroup() {
		this.updateStage();
	}

	@Override
	public void onMeterGroupRefreshed() {
		if (this.stage == Stage.JOIN_METER_GROUP) {
			this.updateStage();
		}
	}

	@Override
	public void onMeterAdded(Meter meter) {
		if (this.stage == Stage.ADD_METER) {
			this.updateStage();
		}
	}

	@Override
	public void onMeterRemoved(Meter meter) {
		this.updateStage();
	}

	@Override
	public void init() {
		this.updateStage();
	}

	@Override
	public void tick() {
	}

	@Override
	public boolean isCompleted() {
		return this.stage == Stage.COMPLETED;
	}

	@Override
	public TutorialStep nextStep() {
		return TutorialStep.SCROLL_TIMELINE;
	}

	@Override
	public float getProgress() {
		return (float) this.stage.ordinal() / (Stage.values().length - 1);
	}

	private void updateStage() {
		if (this.stage == Stage.COMPLETED) {
			return;
		}

		MultimeterClient client = MultimeterClient.INSTANCE;
		ClientMeterGroup meterGroup = client.getMeterGroup();

		if (!meterGroup.isSubscribed()) {
			this.stage = Stage.JOIN_METER_GROUP;
		} else if (!meterGroup.hasMeters()) {
			this.stage = Stage.ADD_METER;
		} else if (!client.isHudActive()) {
			this.stage = Stage.ACTIVE_HUD;
		} else if (!client.getHud().isPaused()) {
			this.stage = Stage.PAUSE_HUD;
		} else {
			this.stage = Stage.COMPLETED;
		}
	}

	public static enum Stage {
		JOIN_METER_GROUP, ADD_METER, ACTIVE_HUD, PAUSE_HUD, COMPLETED
	}
}
