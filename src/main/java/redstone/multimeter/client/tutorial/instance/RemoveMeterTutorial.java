package redstone.multimeter.client.tutorial.instance;

import redstone.multimeter.client.Keybinds;
import redstone.multimeter.client.gui.element.tutorial.StagedTutorialToast;
import redstone.multimeter.client.gui.element.tutorial.TutorialToast;
import redstone.multimeter.client.gui.text.Texts;
import redstone.multimeter.client.meter.ClientMeterGroup;
import redstone.multimeter.client.tutorial.Tutorial;
import redstone.multimeter.client.tutorial.TutorialStep;
import redstone.multimeter.common.DimPos;
import redstone.multimeter.common.meter.Meter;

public class RemoveMeterTutorial extends StagedTutorialInstance {

	private Stage stage;
	private DimPos lastRequest;

	public RemoveMeterTutorial(Tutorial tutorial) {
		super(tutorial);

		findStage();
	}

	@Override
	protected TutorialToast createToast() {
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
		if (stage == Stage.JOIN_METER_GROUP) {
			findStage();
		}
	}

	@Override
	public void onLeaveMeterGroup() {
		findStage();
	}

	@Override
	public void onMeterGroupRefreshed() {
		findStage();
	}

	@Override
	public void onMeterAdded(Meter meter) {
		if (stage == Stage.ADD_METER) {
			findStage();
		}
	}

	@Override
	public void onMeterRemoveRequested(DimPos pos) {
		if (stage == Stage.REMOVE_METER) {
			lastRequest = pos;
		}
	}

	@Override
	public void onMeterRemoved(Meter meter) {
		if (stage == Stage.REMOVE_METER && meter.getPos().equals(lastRequest)) {
			lastRequest = null;
			completed = true;
		}
	}

	@Override
	public void tick() {
	}

	@Override
	public TutorialStep getNextStep() {
		return TutorialStep.NONE;
	}

	@Override
	public float getProgress() {
		return completed ? 1.0F : (float)stage.ordinal() / 3;
	}

	private void findStage() {
		ClientMeterGroup meterGroup = tutorial.getClient().getMeterGroup();

		if (!meterGroup.isSubscribed()) {
			stage = Stage.JOIN_METER_GROUP;
		} else if (!meterGroup.hasMeters()) {
			stage = Stage.ADD_METER;
		} else {
			stage = Stage.REMOVE_METER;
		}

		lastRequest = null;
	}

	public static enum Stage {
		JOIN_METER_GROUP, ADD_METER, REMOVE_METER
	}
}
