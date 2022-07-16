package redstone.multimeter.client.tutorial.instance;

import net.minecraft.text.Text;

import redstone.multimeter.client.KeyBindings;
import redstone.multimeter.client.gui.element.tutorial.TutorialToast;
import redstone.multimeter.client.gui.screen.OptionsScreen;
import redstone.multimeter.client.gui.screen.RSMMScreen;
import redstone.multimeter.client.tutorial.Tutorial;
import redstone.multimeter.client.tutorial.TutorialStep;
import redstone.multimeter.util.TextUtils;

public class OpenOptionsScreenTutorial extends TutorialInstance {
	
	private static final Text TITLE = Text.literal("Open The Options Screen");
	private static final Text DESCRIPTION = Text.literal("").
													append("Press ").
													append(TextUtils.formatKey(KeyBindings.OPEN_OPTIONS_MENU)).
													append(" to open the options menu.");
	
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
		return TutorialStep.NONE;
	}
}
