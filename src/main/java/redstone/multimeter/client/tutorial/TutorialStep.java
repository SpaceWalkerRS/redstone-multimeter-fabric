package redstone.multimeter.client.tutorial;

import java.util.function.Supplier;

import redstone.multimeter.client.gui.text.Text;
import redstone.multimeter.client.gui.text.Texts;
import redstone.multimeter.client.option.Cyclable;
import redstone.multimeter.client.tutorial.instance.*;

public enum TutorialStep implements Cyclable<TutorialStep> {

	OPEN_OPTIONS_SCREEN("openOptionsScreen", "OPEN_OPTIONS_SCREEN", OpenOptionsScreenTutorial::new),
	JOIN_METER_GROUP("joinMeterGroup", "JOIN_METER_GROUP", JoinMeterGroupTutorial::new),
	PLACE_METER("placeMeter", "PLACE_METER", PlaceMeterTutorial::new),
	PAUSE_TIMELINE("pauseTimeline", "PAUSE_HUD", PauseTimelineTutorial::new),
	SCROLL_TIMELINE("scrollTimeline", "SCROLL_HUD", ScrollTimelineTutorial::new),
	OPEN_MULTIMETER_SCREEN("openMultimeterScreen", "OPEN_MULTIMETER_SCREEN", OpenMultimeterScreenTutorial::new),
	OPEN_METER_CONTROLS("openMeterControls", "OPEN_METER_CONTROLS", OpenMeterControlsTutorial::new),
	REMOVE_METER("removeMeter", "REMOVE_METER", RemoveMeterTutorial::new),
	NONE("none", "NONE", CompletedTutorial::new);

	private final String key;
	// used for parsing values from before RSMM 1.16
	private final String legacyKey;
	private final Supplier<TutorialInstance> factory;

	private TutorialStep(String key, String legacyKey, Supplier<TutorialInstance> factory) {
		this.key = key;
		this.legacyKey = legacyKey;
		this.factory = factory;
	}

	@Override
	public String key() {
		return this.key;
	}

	@Override
	public String legacyKey() {
		return this.legacyKey;
	}

	public Text getName() {
		return Texts.translatable("rsmm.tutorial." + this.key + ".name");
	}

	public Text getDescription(Object... args) {
		return Texts.translatable("rsmm.tutorial." + this.key + ".description", args);
	}

	public TutorialInstance createInstance() {
		return this.factory.get();
	}
}
