package redstone.multimeter.client.tutorial.instance;

import redstone.multimeter.client.Keybinds;
import redstone.multimeter.client.MultimeterClient;
import redstone.multimeter.client.gui.element.tutorial.StagedTutorialToast;
import redstone.multimeter.client.gui.element.tutorial.TutorialToast;
import redstone.multimeter.client.gui.text.Texts;
import redstone.multimeter.client.meter.ClientMeterGroup;
import redstone.multimeter.client.tutorial.TutorialStep;
import redstone.multimeter.common.meter.Meter;

public class SaveMeterGroupTutorial implements StagedTutorialInstance {

	private Stage stage;

	@Override
	public TutorialToast createToast() {
		return new StagedTutorialToast(
			this,
			TutorialStep.SAVE_METER_GROUP.getName(),
			TutorialStep.SAVE_METER_GROUP.getDescription(
				Texts.keys(Keybinds.SAVE_METER_GROUP)
			)
		);
	}

	@Override
	public void onMeterGroupSaved(int slot) {
		if (this.stage == Stage.SAVE_METER_GROUP) {
			this.updateStage(slot);
		}
	}

	@Override
	public void onJoinMeterGroup() {
		if (this.stage == Stage.JOIN_METER_GROUP) {
			this.updateStage(-1);
		}
	}

	@Override
	public void onLeaveMeterGroup() {
		this.updateStage(-1);
	}

	@Override
	public void onMeterGroupRefreshed() {
		if (this.stage == Stage.JOIN_METER_GROUP) {
			this.updateStage(-1);
		}
	}

	@Override
	public void onMeterAdded(Meter meter) {
		if (this.stage == Stage.ADD_METER) {
			this.updateStage(-1);
		}
	}

	@Override
	public void onMeterRemoved(Meter meter) {
		this.updateStage(-1);
	}

	@Override
	public void init() {
		this.updateStage(-1);
	}

	@Override
	public void tick() {
		this.updateStage(-1);
	}

	@Override
	public boolean isCompleted() {
		return this.stage == Stage.COMPLETED;
	}

	@Override
	public TutorialStep nextStep() {
		return TutorialStep.LOAD_METER_GROUP;
	}

	@Override
	public float getProgress() {
		return (float) this.stage.ordinal() / (Stage.values().length - 1);
	}

	private void updateStage(int slot) {
		if (this.stage == Stage.COMPLETED) {
			return;
		}

		ClientMeterGroup meterGroup = MultimeterClient.INSTANCE.getMeterGroup();

		if (!meterGroup.isSubscribed()) {
			this.stage = Stage.JOIN_METER_GROUP;
		} else if (!meterGroup.hasMeters()) {
			this.stage = Stage.ADD_METER;
		} else if (!Keybinds.SAVE_METER_GROUP.isDown()) {
			this.stage = Stage.PRESS_KEYBIND;
		} else if (slot == -1) {
			this.stage = Stage.SAVE_METER_GROUP;
		} else {
			this.stage = Stage.COMPLETED;
		}
	}

	public static enum Stage {
		JOIN_METER_GROUP, ADD_METER, PRESS_KEYBIND, SAVE_METER_GROUP, COMPLETED
	}
}
