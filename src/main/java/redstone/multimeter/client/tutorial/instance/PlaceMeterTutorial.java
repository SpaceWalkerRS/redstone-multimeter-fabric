package redstone.multimeter.client.tutorial.instance;

import net.minecraft.network.chat.Component;

import redstone.multimeter.client.Keybinds;
import redstone.multimeter.client.gui.element.tutorial.StagedTutorialToast;
import redstone.multimeter.client.gui.element.tutorial.TutorialToast;
import redstone.multimeter.client.meter.ClientMeterGroup;
import redstone.multimeter.client.tutorial.Tutorial;
import redstone.multimeter.client.tutorial.TutorialStep;
import redstone.multimeter.common.DimPos;
import redstone.multimeter.common.meter.Meter;
import redstone.multimeter.util.TextUtils;

public class PlaceMeterTutorial extends StagedTutorialInstance {

	private static final Component TITLE = Component.literal("Place A Meter");
	private static final Component DESCRIPTION = Component.literal("").
													append("Look at a block and press ").
													append(TextUtils.formatKeybind(Keybinds.TOGGLE_METER)).
													append(" to place a meter.");

	private Stage stage;
	private DimPos lastRequest;

	public PlaceMeterTutorial(Tutorial tutorial) {
		super(tutorial);

		findStage();
	}

	@Override
	protected TutorialToast createToast() {
		return new StagedTutorialToast(this, TITLE, DESCRIPTION);
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
	public void onMeterAddRequested(DimPos pos) {
		if (stage == Stage.ADD_METER) {
			lastRequest = pos;
		}
	}

	@Override
	public void onMeterAdded(Meter meter) {
		if (stage == Stage.ADD_METER && meter.getPos().equals(lastRequest)) {
			lastRequest = null;
			completed = true;
		}
	}

	@Override
	public void tick() {
		
	}

	@Override
	public TutorialStep getNextStep() {
		return TutorialStep.PAUSE_HUD;
	}

	@Override
	public float getProgress() {
		return completed ? 1.0F : (float)stage.ordinal() / 2;
	}

	private void findStage() {
		ClientMeterGroup meterGroup = tutorial.getClient().getMeterGroup();

		if (!meterGroup.isSubscribed()) {
			stage = Stage.JOIN_METER_GROUP;
		} else {
			stage = Stage.ADD_METER;
		}

		lastRequest = null;
	}

	public static enum Stage {
		JOIN_METER_GROUP, ADD_METER
	}
}
