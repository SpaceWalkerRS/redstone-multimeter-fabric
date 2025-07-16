package redstone.multimeter.client.tutorial;

import java.util.function.Function;

import redstone.multimeter.client.gui.text.Text;
import redstone.multimeter.client.gui.text.Texts;
import redstone.multimeter.client.option.Cyclable;
import redstone.multimeter.client.tutorial.instance.*;

public enum TutorialStep implements Cyclable<TutorialStep> {

	OPEN_OPTIONS_SCREEN("openOptionsScreen", OpenOptionsScreenTutorial::new),
	JOIN_METER_GROUP("joinMeterGroup", JoinMeterGroupTutorial::new),
	PLACE_METER("placeMeter", PlaceMeterTutorial::new),
	PAUSE_TIMELINE("pauseTimeline", PauseTimelineTutorial::new),
	SCROLL_TIMELINE("scrollTimeline", ScrollTimelineTutorial::new),
	OPEN_MULTIMETER_SCREEN("openMultimeterScreen", OpenMultimeterScreenTutorial::new),
	OPEN_METER_CONTROLS("openMeterControls", OpenMeterControlsTutorial::new),
	REMOVE_METER("removeMeter", RemoveMeterTutorial::new),
	NONE("none", CompletedTutorial::new);

	private final String key;
	private final Function<Tutorial, TutorialInstance> factory;

	private TutorialStep(String key, Function<Tutorial, TutorialInstance> factory) {
		this.key = key;
		this.factory = factory;
	}

	@Override
	public String key() {
		return this.key;
	}

	public Text getName() {
		return Texts.translatable("rsmm.tutorial." + this.key + ".name");
	}

	public Text getDescription(Object... args) {
		return Texts.translatable("rsmm.tutorial." + this.key + ".description", args);
	}

	public TutorialInstance createInstance(Tutorial tutorial) {
		return this.factory.apply(tutorial);
	}
}
