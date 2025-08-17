package redstone.multimeter.client.tutorial.instance;

import redstone.multimeter.client.Keybinds;
import redstone.multimeter.client.MultimeterClient;
import redstone.multimeter.client.gui.element.tutorial.StagedTutorialToast;
import redstone.multimeter.client.gui.element.tutorial.TutorialToast;
import redstone.multimeter.client.gui.text.Texts;
import redstone.multimeter.client.meter.ClientMeterGroup;
import redstone.multimeter.client.tutorial.TutorialStep;
import redstone.multimeter.common.DimPos;
import redstone.multimeter.common.meter.Meter;

public class RemoveMeterTutorial implements StagedTutorialInstance {

	private Stage stage;
	private DimPos lastRequest;

	@Override
	public TutorialToast createToast() {
		return new StagedTutorialToast(
			this,
			TutorialStep.REMOVE_METER.getName(),
			TutorialStep.REMOVE_METER.getDescription(
				Texts.keybind(Keybinds.TOGGLE_METER)
			)
		);
	}

	@Override
	public void onJoinMeterGroup() {
		if (this.stage == Stage.JOIN_METER_GROUP) {
			this.updateStage(null);
		}
	}

	@Override
	public void onLeaveMeterGroup() {
		this.updateStage(null);
	}

	@Override
	public void onMeterGroupRefreshed() {
		if (this.stage == Stage.JOIN_METER_GROUP) {
			this.updateStage(null);
		}
	}

	@Override
	public void onMeterAdded(Meter meter) {
		if (this.stage == Stage.ADD_METER) {
			this.updateStage(null);
		}
	}

	@Override
	public void onMeterRemoveRequested(DimPos pos) {
		if (this.stage == Stage.REMOVE_METER) {
			this.lastRequest = pos;
		}
	}

	@Override
	public void onMeterRemoved(Meter meter) {
		if (this.stage == Stage.REMOVE_METER) {
			this.updateStage(meter.getPos());
		}
	}

	@Override
	public void init() {
		this.updateStage(null);
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
		return TutorialStep.NONE;
	}

	@Override
	public float getProgress() {
		return (float) this.stage.ordinal() / (Stage.values().length - 1);
	}

	private void updateStage(DimPos meterPos) {
		if (this.stage == Stage.COMPLETED) {
			return;
		}

		ClientMeterGroup meterGroup = MultimeterClient.INSTANCE.getMeterGroup();

		if (!meterGroup.isSubscribed()) {
			this.stage = Stage.JOIN_METER_GROUP;
		} else if (meterPos == null && !meterGroup.hasMeters()) {
			this.stage = Stage.ADD_METER;
		} else if (meterPos == null || !meterPos.equals(this.lastRequest)) {
			this.stage = Stage.REMOVE_METER;
		} else {
			this.stage = Stage.COMPLETED;
		}

		this.lastRequest = null;
	}

	public static enum Stage {
		JOIN_METER_GROUP, ADD_METER, REMOVE_METER, COMPLETED
	}
}
