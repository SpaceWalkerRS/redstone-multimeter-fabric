package redstone.multimeter.client.tutorial.instance;

import redstone.multimeter.client.Keybinds;
import redstone.multimeter.client.MultimeterClient;
import redstone.multimeter.client.gui.element.tutorial.StagedTutorialToast;
import redstone.multimeter.client.gui.element.tutorial.TutorialToast;
import redstone.multimeter.client.gui.text.Texts;
import redstone.multimeter.client.meter.ClientMeterGroup;
import redstone.multimeter.client.tutorial.TutorialStep;
import redstone.multimeter.common.meter.Meter;

public class OpenMeterControlsTutorial implements StagedTutorialInstance {

	private Stage stage;

	@Override
	public TutorialToast createToast() {
		return new StagedTutorialToast(
			this,
			TutorialStep.OPEN_METER_CONTROLS.getName(),
			TutorialStep.OPEN_METER_CONTROLS.getDescription(
				Texts.keybind(Keybinds.OPEN_METER_CONTROLS)
			)
		);
	}

	@Override
	public void onMeterControlsOpened() {
		if (this.stage == Stage.OPEN_METER_CONTROLS) {
			this.updateStage(true);
		}
	}

	@Override
	public void onJoinMeterGroup() {
		if (this.stage == Stage.JOIN_METER_GROUP) {
			this.updateStage(false);
		}
	}

	@Override
	public void onLeaveMeterGroup() {
		this.updateStage(false);
	}

	@Override
	public void onMeterGroupRefreshed() {
		if (this.stage == Stage.JOIN_METER_GROUP) {
			this.updateStage(false);
		}
	}

	@Override
	public void onMeterAdded(Meter meter) {
		if (this.stage == Stage.ADD_METER) {
			this.updateStage(false);
		}
	}

	@Override
	public void onMeterRemoved(Meter meter) {
		this.updateStage(false);
	}

	@Override
	public void init() {
		this.updateStage(false);
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
		return TutorialStep.REMOVE_METER;
	}

	@Override
	public float getProgress() {
		return (float) this.stage.ordinal() / (Stage.values().length - 1);
	}

	private void updateStage(boolean meterControls) {
		if (this.stage == Stage.COMPLETED) {
			return;
		}

		ClientMeterGroup meterGroup = MultimeterClient.INSTANCE.getMeterGroup();

		if (!meterGroup.isSubscribed()) {
			this.stage = Stage.JOIN_METER_GROUP;
		} else if (!meterGroup.hasMeters()) {
			this.stage = Stage.ADD_METER;
		} else if (!meterControls) {
			this.stage = Stage.OPEN_METER_CONTROLS;
		} else {
			this.stage = Stage.COMPLETED;
		}
	}

	public enum Stage {
		JOIN_METER_GROUP, ADD_METER, OPEN_METER_CONTROLS, COMPLETED
	}
}
