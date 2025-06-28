package redstone.multimeter.client.tutorial.instance;

import net.minecraft.block.Blocks;
import net.minecraft.stat.achievement.AchievementStat;

import redstone.multimeter.client.Keybinds;
import redstone.multimeter.client.gui.text.Text;
import redstone.multimeter.client.gui.text.Texts;
import redstone.multimeter.client.meter.ClientMeterGroup;
import redstone.multimeter.client.tutorial.Tutorial;
import redstone.multimeter.client.tutorial.TutorialStep;
import redstone.multimeter.common.DimPos;
import redstone.multimeter.common.meter.Meter;

public class PlaceMeterTutorial extends StagedTutorialInstance {

	private static final Text TITLE = Texts.literal("Place A Meter");
	private static final Text DESCRIPTION = Texts.composite(
		"Look at a block and press ",
		Texts.keybind(Keybinds.TOGGLE_METER),
		" to place a meter on that block."
	);

	private Stage stage;
	private DimPos lastRequest;

	public PlaceMeterTutorial(Tutorial tutorial) {
		super(tutorial);

		findStage();
	}

	@Override
	protected AchievementStat createAchievement() {
		return new AchievementStat("stats.rsmm.place_meter", "rsmm.place_meter", -1, -1, Blocks.CRAFTING_TABLE, null);
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
