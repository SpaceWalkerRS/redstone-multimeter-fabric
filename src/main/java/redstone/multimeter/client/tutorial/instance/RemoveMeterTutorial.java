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

public class RemoveMeterTutorial extends StagedTutorialInstance {

	private static final Text TITLE = Texts.literal("Remove A Meter");
	private static final Text DESCRIPTION = Texts.composite(
		"Look at a block with a meter on it and press ",
		Texts.keybind(Keybinds.TOGGLE_METER),
		" to remove it."
	);

	private Stage stage;
	private DimPos lastRequest;

	public RemoveMeterTutorial(Tutorial tutorial) {
		super(tutorial);

		findStage();
	}

	@Override
	protected AchievementStat createAchievement() {
		return new AchievementStat("stats.rsmm.remove_meter", "rsmm.remove_meter", -1, -1, Blocks.CRAFTING_TABLE, null);
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
