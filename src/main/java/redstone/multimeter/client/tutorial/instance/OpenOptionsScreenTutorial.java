package redstone.multimeter.client.tutorial.instance;

import redstone.multimeter.client.Keybinds;
import redstone.multimeter.client.gui.element.tutorial.TutorialToast;
import redstone.multimeter.client.gui.screen.OptionsScreen;
import redstone.multimeter.client.gui.screen.RSMMScreen;
import redstone.multimeter.client.gui.text.Text;
import redstone.multimeter.client.gui.text.Texts;
import redstone.multimeter.client.tutorial.Tutorial;
import redstone.multimeter.client.tutorial.TutorialStep;

public class OpenOptionsScreenTutorial extends TutorialInstance {

	private static final Text TITLE = Texts.literal("Open The Options Screen");
	private static final Text DESCRIPTION = Texts.composite(
		"Press ",
		Texts.keybind(Keybinds.OPEN_OPTIONS_MENU),
		" to open the options menu, or access it through Mod Menu."
	);

	public OpenOptionsScreenTutorial(Tutorial tutorial) {
		super(tutorial);
	}

	@Override
	protected TutorialToast createToast() {
		return new TutorialToast(TITLE, DESCRIPTION);
	}

	@Override
	public void onScreenOpened(RSMMScreen screen) {
		if (screen instanceof OptionsScreen) {
			completed = true;
		}
	}

	@Override
	public void tick() {
	}

	@Override
	public TutorialStep getNextStep() {
		return TutorialStep.JOIN_METER_GROUP;
	}
}
