package redstone.multimeter.client.tutorial;

import java.util.function.Function;

import redstone.multimeter.client.option.Cyclable;
import redstone.multimeter.client.tutorial.instance.*;

public enum TutorialStep implements Cyclable<TutorialStep> {

	OPEN_OPTIONS_SCREEN("open options screen", OpenOptionsScreenTutorial::new),
	JOIN_METER_GROUP("join meter group", JoinMeterGroupTutorial::new),
	PLACE_METER("place meter", PlaceMeterTutorial::new),
	PAUSE_HUD("pause hud", PauseHudTutorial::new),
	SCROLL_HUD("scroll hud", ScrollHudTutorial::new),
	OPEN_MULTIMETER_SCREEN("open multimeter screen", OpenMultimeterScreenTutorial::new),
	OPEN_METER_CONTROLS("open meter controls", OpenMeterControlsTutorial::new),
	REMOVE_METER("remove meter", RemoveMeterTutorial::new),
	NONE("none", CompletedTutorial::new);

	private final String name;
	private final Function<Tutorial, TutorialInstance> factory;

	private TutorialStep(String name, Function<Tutorial, TutorialInstance> factory) {
		this.name = name;
		this.factory = factory;
	}

	@Override
	public String getName() {
		return name;
	}

	public TutorialInstance createInstance(Tutorial tutorial) {
		return factory.apply(tutorial);
	}
}
