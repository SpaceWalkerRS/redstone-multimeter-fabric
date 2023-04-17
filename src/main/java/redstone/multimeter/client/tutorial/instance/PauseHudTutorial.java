package redstone.multimeter.client.tutorial.instance;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;

import redstone.multimeter.client.Keybinds;
import redstone.multimeter.client.MultimeterClient;
import redstone.multimeter.client.gui.element.tutorial.StagedTutorialToast;
import redstone.multimeter.client.gui.element.tutorial.TutorialToast;
import redstone.multimeter.client.meter.ClientMeterGroup;
import redstone.multimeter.client.tutorial.Tutorial;
import redstone.multimeter.client.tutorial.TutorialStep;
import redstone.multimeter.common.meter.Meter;
import redstone.multimeter.util.TextUtils;

public class PauseHudTutorial extends StagedTutorialInstance {

	private static final Component TITLE = new TextComponent("Pause The HUD");
	private static final Component DESCRIPTION = new TextComponent("").
													append("Press ").
													append(TextUtils.formatKeybind(Keybinds.PAUSE_METERS)).
													append(" to pause the primary overview of the Multimeter HUD.");

	private Stage stage;

	public PauseHudTutorial(Tutorial tutorial) {
		super(tutorial);

		findStage();
	}

	@Override
	protected TutorialToast createToast() {
		return new StagedTutorialToast(this, TITLE, DESCRIPTION);
	}

	@Override
	public void onToggleHud(boolean enabled) {
		if (!enabled || stage == Stage.ACTIVE_HUD) {
			findStage();
		}
	}

	@Override
	public void onPauseHud(boolean paused) {
		if (stage == Stage.PAUSE_HUD && paused) {
			completed = true;
		}
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
	public void onMeterRemoved(Meter meter) {
		findStage();
	}

	@Override
	public void tick() {
	}

	@Override
	public TutorialStep getNextStep() {
		return TutorialStep.SCROLL_HUD;
	}

	@Override
	public float getProgress() {
		return completed ? 1.0F : (float)stage.ordinal() / 4;
	}

	private void findStage() {
		MultimeterClient client = tutorial.getClient();
		ClientMeterGroup meterGroup = client.getMeterGroup();

		if (!meterGroup.isSubscribed()) {
			stage = Stage.JOIN_METER_GROUP;
		} else if (!meterGroup.hasMeters()) {
			stage = Stage.ADD_METER;
		} else if (!client.isHudActive()) {
			stage = Stage.ACTIVE_HUD;
		} else {
			stage = Stage.PAUSE_HUD;
		}
	}

	public static enum Stage {
		JOIN_METER_GROUP, ADD_METER, ACTIVE_HUD, PAUSE_HUD
	}
}
