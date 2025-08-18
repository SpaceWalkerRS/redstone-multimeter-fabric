package redstone.multimeter.client.tutorial.instance;

import redstone.multimeter.client.Keybinds;
import redstone.multimeter.client.MultimeterClient;
import redstone.multimeter.client.gui.element.tutorial.TutorialToast;
import redstone.multimeter.client.gui.text.Texts;
import redstone.multimeter.client.meter.ClientMeterGroup;
import redstone.multimeter.client.tutorial.TutorialStep;
import redstone.multimeter.common.meter.Meter;

public class FocusModeTutorial implements TutorialInstance {

	private Stage stage;

	@Override
	public TutorialToast createToast() {
		return new TutorialToast(
			TutorialStep.ENABLE_FOCUS_MODE.getName(),
			TutorialStep.ENABLE_FOCUS_MODE.getDescription(
				Texts.keybind(Keybinds.TOGGLE_FOCUS_MODE)
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
	public void onToggleFocusMode(boolean enabled) {
		if (!enabled || this.stage == Stage.ENABLE_FOCUS_MODE) {
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
		return TutorialStep.OPEN_MULTIMETER_SCREEN;
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
		} else if (!client.getHud().isFocusMode()) {
			this.stage = Stage.ENABLE_FOCUS_MODE;
		} else {
			this.stage = Stage.COMPLETED;
		}
	}

	public static enum Stage {
		JOIN_METER_GROUP, ADD_METER, ACTIVE_HUD, ENABLE_FOCUS_MODE, COMPLETED
	}
}
