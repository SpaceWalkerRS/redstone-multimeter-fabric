package redstone.multimeter.client.tutorial.instance;

import net.minecraft.text.Text;

import redstone.multimeter.client.KeyBindings;
import redstone.multimeter.client.gui.element.tutorial.StagedTutorialToast;
import redstone.multimeter.client.gui.element.tutorial.TutorialToast;
import redstone.multimeter.client.meter.ClientMeterGroup;
import redstone.multimeter.client.tutorial.Tutorial;
import redstone.multimeter.client.tutorial.TutorialStep;
import redstone.multimeter.common.meter.Meter;
import redstone.multimeter.util.TextUtils;

public class OpenMeterControlsTutorial extends StagedTutorialInstance {
	
	private static final Text TITLE = Text.literal("Open Meter Controls");
	private static final Text DESCRIPTION = Text.literal("").
													append("Open a meter's controls through the Multimeter screen or by looking at it and pressing ").
													append(TextUtils.formatKey(KeyBindings.OPEN_METER_CONTROLS));
	
	private Stage stage;
	
	public OpenMeterControlsTutorial(Tutorial tutorial) {
		super(tutorial);
		
		findStage();
	}
	
	@Override
	protected TutorialToast createToast() {
		return new StagedTutorialToast(this, TITLE, DESCRIPTION);
	}
	
	@Override
	public void onMeterControlsOpened() {
		if (stage == Stage.OPEN_METER_CONTROLS) {
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
		return TutorialStep.OPEN_OPTIONS_SCREEN;
	}
	
	@Override
	public float getProgress() {
		return completed ? 1.0F : (float)stage.ordinal() / 3;
	}
	
	private void findStage() {
		ClientMeterGroup meterGroup = tutorial.getMultimeterClient().getMeterGroup();
		
		if (!meterGroup.isSubscribed()) {
			stage = Stage.JOIN_METER_GROUP;
		} else if (!meterGroup.hasMeters()) {
			stage = Stage.ADD_METER;
		} else {
			stage = Stage.OPEN_METER_CONTROLS;
		}
	}
	
	public static enum Stage {
		JOIN_METER_GROUP, ADD_METER, OPEN_METER_CONTROLS
	}
}
