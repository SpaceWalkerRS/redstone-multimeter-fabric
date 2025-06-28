package redstone.multimeter.client.tutorial.instance;

import net.minecraft.block.Blocks;
import net.minecraft.stat.achievement.AchievementStat;

import redstone.multimeter.client.Keybinds;
import redstone.multimeter.client.MultimeterClient;
import redstone.multimeter.client.gui.text.Text;
import redstone.multimeter.client.gui.text.Texts;
import redstone.multimeter.client.meter.ClientMeterGroup;
import redstone.multimeter.client.tutorial.Tutorial;
import redstone.multimeter.client.tutorial.TutorialStep;
import redstone.multimeter.common.meter.Meter;

public class PauseHudTutorial extends StagedTutorialInstance {

	private static final Text TITLE = Texts.literal("Pause The HUD");
	private static final Text DESCRIPTION = Texts.composite(
		"Press ",
		Texts.keybind(Keybinds.PAUSE_METERS),
		" to pause the Multimeter timeline."
	);

	private Stage stage;

	public PauseHudTutorial(Tutorial tutorial) {
		super(tutorial);

		findStage();
	}

	@Override
	protected AchievementStat createAchievement() {
		return new AchievementStat("stats.rsmm.pause_hud", "rsmm.pause_hud", -1, -1, Blocks.CRAFTING_TABLE, null);
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
