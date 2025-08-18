package redstone.multimeter.client.tutorial.instance;

import redstone.multimeter.client.Keybinds;
import redstone.multimeter.client.gui.element.tutorial.StagedTutorialToast;
import redstone.multimeter.client.gui.element.tutorial.TutorialToast;
import redstone.multimeter.client.gui.text.Texts;
import redstone.multimeter.client.tutorial.TutorialStep;

public class LoadMeterGroupTutorial implements StagedTutorialInstance {

	private Stage stage;

	@Override
	public TutorialToast createToast() {
		return new StagedTutorialToast(
			this,
			TutorialStep.LOAD_METER_GROUP.getName(),
			TutorialStep.LOAD_METER_GROUP.getDescription(
				Texts.keys(Keybinds.LOAD_METER_GROUP)
			)
		);
	}

	@Override
	public void onMeterGroupLoaded(int slot) {
		if (this.stage == Stage.LOAD_METER_GROUP) {
			this.updateStage(slot);
		}
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
		return TutorialStep.REMOVE_METER;
	}

	@Override
	public float getProgress() {
		return (float) this.stage.ordinal() / (Stage.values().length - 1);
	}

	private void updateStage(int slot) {
		if (this.stage == Stage.COMPLETED) {
			return;
		}

		if (!Keybinds.LOAD_METER_GROUP.isDown()) {
			this.stage = Stage.PRESS_KEYBIND;
		} else if (slot == -1) {
			this.stage = Stage.LOAD_METER_GROUP;
		} else {
			this.stage = Stage.COMPLETED;
		}
	}

	public static enum Stage {
		PRESS_KEYBIND, LOAD_METER_GROUP, COMPLETED
	}
}
