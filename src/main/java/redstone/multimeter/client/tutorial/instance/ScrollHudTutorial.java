package redstone.multimeter.client.tutorial.instance;

import net.minecraft.block.Blocks;
import net.minecraft.stat.achievement.AchievementStat;

import redstone.multimeter.client.MultimeterClient;
import redstone.multimeter.client.meter.ClientMeterGroup;
import redstone.multimeter.client.tutorial.Tutorial;
import redstone.multimeter.client.tutorial.TutorialStep;
import redstone.multimeter.common.meter.Meter;

public class ScrollHudTutorial extends StagedTutorialInstance {

	private static final int TIMES_SCROLLED_TARGET = 5;

	private Stage stage;
	private int timesScrolled;

	public ScrollHudTutorial(Tutorial tutorial) {
		super(tutorial);

		findStage();
	}

	@Override
	protected AchievementStat createAchievement() {
		return new AchievementStat("stats.rsmm.scroll_hud", "rsmm.scroll_hud", -1, -1, Blocks.CRAFTING_TABLE, null);
	}

	@Override
	public void onToggleHud(boolean enabled) {
		if (!enabled || stage == Stage.ACTIVE_HUD) {
			findStage();
		}
	}

	@Override
	public void onPauseHud(boolean paused) {
		if (!paused || stage == Stage.PAUSE_HUD) {
			findStage();
		}
	}

	@Override
	public void onScrollHud(int amount) {
		if (stage == Stage.SCROLL_HUD && amount != 0 && ++timesScrolled >= TIMES_SCROLLED_TARGET) {
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
		return TutorialStep.OPEN_MULTIMETER_SCREEN;
	}

	@Override
	public float getProgress() {
		float progress = completed ? 1.0F : (float)stage.ordinal() / 5;

		if (stage == Stage.SCROLL_HUD) {
			progress += (float)timesScrolled / (5 * TIMES_SCROLLED_TARGET);
		}

		return progress;
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
		} else if (!client.getHud().isPaused()) {
			stage = Stage.PAUSE_HUD;
		} else {
			stage = Stage.SCROLL_HUD;
		}

		timesScrolled = 0;
	}

	public static enum Stage {
		JOIN_METER_GROUP, ADD_METER, ACTIVE_HUD, PAUSE_HUD, SCROLL_HUD
	}
}
