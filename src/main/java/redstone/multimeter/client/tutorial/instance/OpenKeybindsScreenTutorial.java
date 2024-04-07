package redstone.multimeter.client.tutorial.instance;

import net.minecraft.block.Blocks;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.options.ControlsOptionsScreen;
import net.minecraft.stat.achievement.AchievementStat;

import redstone.multimeter.client.tutorial.Tutorial;
import redstone.multimeter.client.tutorial.TutorialStep;

public class OpenKeybindsScreenTutorial extends TutorialInstance {

	public OpenKeybindsScreenTutorial(Tutorial tutorial) {
		super(tutorial);
	}

	@Override
	protected AchievementStat createAchievement() {
		return new AchievementStat("stats.rsmm.open_keybinds_screen", "rsmm.open_keybinds_screen", -1, -1, Blocks.CRAFTING_TABLE, null);
	}

	@Override
	public void onScreenOpened(Screen screen) {
		if (screen instanceof ControlsOptionsScreen) {
			completed = true;
		}
	}

	@Override
	public void tick() {
	}

	@Override
	public TutorialStep getNextStep() {
		return TutorialStep.OPEN_OPTIONS_SCREEN;
	}
}
