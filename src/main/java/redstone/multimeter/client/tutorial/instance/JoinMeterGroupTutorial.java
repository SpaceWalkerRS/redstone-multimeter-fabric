package redstone.multimeter.client.tutorial.instance;

import net.minecraft.text.Text;

import redstone.multimeter.client.gui.element.tutorial.TutorialToast;
import redstone.multimeter.client.tutorial.Tutorial;
import redstone.multimeter.client.tutorial.TutorialStep;

public class JoinMeterGroupTutorial extends TutorialInstance {
	
	private static final Text TITLE = Text.literal("Join A Meter Group");
	private static final Text DESCRIPTION = Text.literal("Use the /metergroup command to subscribe to a meter group.");
	
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
	protected TutorialToast createToast() {
		return new TutorialToast(TITLE, DESCRIPTION);
	}
	
	@Override
	public void tick() {
		
	}
	
	@Override
	public TutorialStep getNextStep() {
		return TutorialStep.PLACE_METER;
	}
}
