package redstone.multimeter.client.tutorial.instance;

import net.minecraft.block.Blocks;
import net.minecraft.stat.achievement.AchievementStat;

import redstone.multimeter.client.tutorial.Tutorial;
import redstone.multimeter.client.tutorial.TutorialStep;

public class JoinMeterGroupTutorial extends TutorialInstance {

	public JoinMeterGroupTutorial(Tutorial tutorial) {
		super(tutorial);
	}

	@Override
	public void onJoinMeterGroup() {
		completed = true;
	}

	@Override
	public void onMeterGroupRefreshed() {
		completed = true;
	}

	@Override
	protected AchievementStat createAchievement() {
		return new AchievementStat("stats.rsmm.join_meter_group", "rsmm.join_meter_group", -1, -1, Blocks.CRAFTING_TABLE, null);
	}

	@Override
	public void tick() {
	}

	@Override
	public TutorialStep getNextStep() {
		return TutorialStep.PLACE_METER;
	}
}
