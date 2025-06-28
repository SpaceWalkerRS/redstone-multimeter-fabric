package redstone.multimeter.client.tutorial.instance;

import net.minecraft.block.Blocks;
import net.minecraft.stat.achievement.AchievementStat;

import redstone.multimeter.client.gui.text.Text;
import redstone.multimeter.client.gui.text.Texts;
import redstone.multimeter.client.tutorial.Tutorial;
import redstone.multimeter.client.tutorial.TutorialStep;

public class JoinMeterGroupTutorial extends TutorialInstance {

	private static final Text TITLE = Texts.literal("Join A Meter Group");
	private static final Text DESCRIPTION = Texts.literal("Use the /metergroup command to subscribe to a meter group.");

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
