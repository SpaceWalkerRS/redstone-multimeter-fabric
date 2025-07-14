package redstone.multimeter.client.tutorial.instance;

import redstone.multimeter.client.Keybinds;
import redstone.multimeter.client.gui.element.tutorial.TutorialToast;
import redstone.multimeter.client.gui.screen.MultimeterScreen;
import redstone.multimeter.client.gui.screen.RSMMScreen;
import redstone.multimeter.client.gui.text.Text;
import redstone.multimeter.client.gui.text.Texts;
import redstone.multimeter.client.tutorial.Tutorial;
import redstone.multimeter.client.tutorial.TutorialStep;

public class OpenMultimeterScreenTutorial extends TutorialInstance {

	private static final Text TITLE = Texts.literal("Open The Multimeter Screen");
	private static final Text DESCRIPTION = Texts.composite(
		"Press ",
		Texts.keybind(Keybinds.OPEN_MULTIMETER_SCREEN),
		" to open the Multimeter screen."
	);

	public OpenMultimeterScreenTutorial(Tutorial tutorial) {
		super(tutorial);
	}

	@Override
	protected TutorialToast createToast() {
		return new TutorialToast(TITLE, DESCRIPTION);
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
		return TutorialStep.OPEN_METER_CONTROLS;
	}
}
