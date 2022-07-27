package redstone.multimeter.client.tutorial.instance;

import redstone.multimeter.client.tutorial.Tutorial;

public abstract class StagedTutorialInstance extends TutorialInstance {
	
	protected StagedTutorialInstance(Tutorial tutorial) {
		super(tutorial);
	}
	
	public abstract float getProgress();
	
}
