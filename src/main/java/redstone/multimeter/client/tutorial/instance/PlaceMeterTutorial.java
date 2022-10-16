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

public class PlaceMeterTutorial extends StagedTutorialInstance {
	
	private static final Text TITLE = new LiteralText("Place A Meter");
	private static final Text DESCRIPTION = new LiteralText("").
													append("Look at a block and press ").
													append(TextUtils.formatKeybind(KeyBindings.TOGGLE_METER)).
													append(" to place a meter.");
	
	private Stage stage;
	private WorldPos lastRequest;
	
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
	public void onMeterAddRequested(WorldPos pos) {
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
		ClientMeterGroup meterGroup = tutorial.getMultimeterClient().getMeterGroup();
		
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
