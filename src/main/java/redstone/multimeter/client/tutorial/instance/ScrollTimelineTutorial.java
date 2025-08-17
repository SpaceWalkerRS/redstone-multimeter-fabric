package redstone.multimeter.client.tutorial.instance;

import redstone.multimeter.client.Keybinds;
import redstone.multimeter.client.MultimeterClient;
import redstone.multimeter.client.gui.element.tutorial.StagedTutorialToast;
import redstone.multimeter.client.gui.element.tutorial.TutorialToast;
import redstone.multimeter.client.gui.text.Texts;
import redstone.multimeter.client.meter.ClientMeterGroup;
import redstone.multimeter.client.tutorial.TutorialStep;
import redstone.multimeter.common.meter.Meter;

public class ScrollTimelineTutorial implements StagedTutorialInstance {

	private static final int TIMES_SCROLLED_TARGET = 5;

	private Stage stage;
	private int timesScrolled;

	@Override
	public TutorialToast createToast() {
		return new StagedTutorialToast(
			this,
			TutorialStep.SCROLL_TIMELINE.getName(),
			TutorialStep.SCROLL_TIMELINE.getDescription(
				Texts.keybind(Keybinds.STEP_BACKWARD),
				Texts.keybind(Keybinds.STEP_FORWARD),
				Texts.keys(Keybinds.SCROLL_HUD, "scroll")
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
	public void onScrollHud(int amount) {
		if (this.stage == Stage.SCROLL_HUD) {
			if (amount != 0) {
				this.timesScrolled++;
			}

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

	@Override
	public float getProgress() {
		float progress = (float) this.stage.ordinal() / (Stage.values().length - 1);

		if (this.stage == Stage.SCROLL_HUD) {
			progress += (float) this.timesScrolled / (5 * TIMES_SCROLLED_TARGET);
		}

		return progress;
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
		} else if (this.timesScrolled < TIMES_SCROLLED_TARGET) {
			this.stage = Stage.SCROLL_HUD;
		} else {
			this.stage = Stage.COMPLETED;
		}

		if (this.stage != Stage.SCROLL_HUD) {
			this.timesScrolled = 0;
		}
	}

	public static enum Stage {
		JOIN_METER_GROUP, ADD_METER, ACTIVE_HUD, PAUSE_HUD, SCROLL_HUD, COMPLETED
	}
}
