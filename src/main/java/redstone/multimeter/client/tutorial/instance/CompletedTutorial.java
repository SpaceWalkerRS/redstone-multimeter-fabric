package redstone.multimeter.client.tutorial.instance;

import net.minecraft.stat.achievement.AchievementStat;

import redstone.multimeter.client.tutorial.Tutorial;
import redstone.multimeter.client.tutorial.TutorialStep;

public class CompletedTutorial extends TutorialInstance {

	public CompletedTutorial(Tutorial tutorial) {
		super(tutorial);

		this.completed = true;
	}

	@Override
	protected AchievementStat createAchievement() {
		return null;
	}

	@Override
	public void tick() {
	}

	@Override
	public TutorialStep getNextStep() {
		return TutorialStep.NONE;
	}
}
