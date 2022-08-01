package redstone.multimeter.client.tutorial.instance;

import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;

import redstone.multimeter.client.KeyBindings;
import redstone.multimeter.client.gui.element.tutorial.StagedTutorialToast;
import redstone.multimeter.client.gui.element.tutorial.TutorialToast;
import redstone.multimeter.client.meter.ClientMeterGroup;
import redstone.multimeter.client.tutorial.Tutorial;
import redstone.multimeter.client.tutorial.TutorialStep;
import redstone.multimeter.common.WorldPos;
import redstone.multimeter.common.meter.Meter;
import redstone.multimeter.util.TextUtils;

public class RemoveMeterTutorial extends StagedTutorialInstance {
	
	private static final Text TITLE = new LiteralText("Remove A Meter");
	private static final Text DESCRIPTION = new LiteralText("").
													append("Look at a meter and press ").
													append(TextUtils.formatKeybind(KeyBindings.TOGGLE_METER)).
													append(" to remove it.");
	
	private Stage stage;
	private WorldPos lastRequest;
	
	public RemoveMeterTutorial(Tutorial tutorial) {
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
	public void onMeterAdded(Meter meter) {
		if (stage == Stage.ADD_METER) {
			findStage();
		}
	}
	
	@Override
	public void onMeterRemoveRequested(WorldPos pos) {
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
		ClientMeterGroup meterGroup = tutorial.getMultimeterClient().getMeterGroup();
		
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
