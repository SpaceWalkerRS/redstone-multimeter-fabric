package redstone.multimeter.client.tutorial.instance;

import net.minecraft.block.Blocks;
import net.minecraft.stat.achievement.AchievementStat;

import redstone.multimeter.client.Keybinds;
import redstone.multimeter.client.gui.element.tutorial.TutorialToast;
import redstone.multimeter.client.gui.screen.MultimeterScreen;
import redstone.multimeter.client.gui.screen.RSMMScreen;
import redstone.multimeter.client.gui.text.Texts;
import redstone.multimeter.client.tutorial.Tutorial;
import redstone.multimeter.client.tutorial.TutorialStep;

public class OpenMultimeterScreenTutorial extends TutorialInstance {

	public OpenMultimeterScreenTutorial(Tutorial tutorial) {
		super(tutorial);
	}

	@Override
	protected AchievementStat createAchievement() {
		return new AchievementStat("stats.rsmm.open_multimeter_screen", "rsmm.open_multimeter_screen", -1, -1, Blocks.CRAFTING_TABLE, null);
	}

	protected TutorialToast createToast() {
		return new TutorialToast(
			TutorialStep.OPEN_MULTIMETER_SCREEN.getName(),
			TutorialStep.OPEN_MULTIMETER_SCREEN.getDescription(
				Texts.keybind(Keybinds.OPEN_MULTIMETER_SCREEN)
			)
		);
	}

	@Override
	public void onScreenOpened(RSMMScreen screen) {
		if (screen instanceof MultimeterScreen) {
			completed = true;
		}
	}

	@Override
	public void tick() {
	}

	@Override
	public TutorialStep getNextStep() {
		return TutorialStep.OPEN_MULTIMETER_SCREEN;
	}
}
