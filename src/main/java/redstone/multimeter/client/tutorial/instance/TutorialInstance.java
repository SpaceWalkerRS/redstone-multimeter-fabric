package redstone.multimeter.client.tutorial.instance;

import net.minecraft.stat.achievement.AchievementStat;

import redstone.multimeter.client.tutorial.Tutorial;
import redstone.multimeter.client.tutorial.TutorialListener;
import redstone.multimeter.client.tutorial.TutorialStep;

public abstract class TutorialInstance implements TutorialListener {

	protected final Tutorial tutorial;
	protected final AchievementStat achievement;

	protected boolean completed = false;

	protected TutorialInstance(Tutorial tutorial) {
		this.tutorial = tutorial;
		this.achievement = createAchievement();
	}

	protected abstract AchievementStat createAchievement();

	public abstract void tick();

	public void start() {
		if (achievement != null) {
			tutorial.getMinecraft().toast.setTutorial(achievement);
		}
	}

	public void stop() {
		if (achievement != null) {
			tutorial.getMinecraft().toast.clear();
		}
	}

	public boolean isCompleted() {
		return completed;
	}

	public abstract TutorialStep getNextStep();

}
