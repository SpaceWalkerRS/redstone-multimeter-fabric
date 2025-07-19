package redstone.multimeter.client.tutorial;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;

import redstone.multimeter.client.MultimeterClient;
import redstone.multimeter.client.gui.screen.RSMMScreen;
import redstone.multimeter.client.option.Options;
import redstone.multimeter.client.tutorial.instance.TutorialInstance;
import redstone.multimeter.common.DimPos;
import redstone.multimeter.common.meter.Meter;

public class Tutorial implements TutorialListener {

	private static final int MAX_TIME = 3 * 60 * 20;
	private static final int COOLDOWN = 24;

	private final Minecraft minecraft;
	private final MultimeterClient client;

	private TutorialInstance instance;
	private int time;
	private int cooldown;

	public Tutorial(MultimeterClient client) {
		this.minecraft = client.getMinecraft();
		this.client = client;

		this.cooldown = 5 * COOLDOWN;
	}

	public Minecraft getMinecraft() {
		return minecraft;
	}

	public MultimeterClient getClient() {
		return client;
	}

	@Override
	public void onScreenOpened(Screen screen) {
		if (instance != null && !instance.isCompleted()) {
			instance.onScreenOpened(screen);
		}
	}

	@Override
	public void onScreenOpened(RSMMScreen screen) {
		if (instance != null && !instance.isCompleted()) {
			instance.onScreenOpened(screen);
		}
	}

	@Override
	public void onToggleHud(boolean enabled) {
		if (instance != null && !instance.isCompleted()) {
			instance.onToggleHud(enabled);
		}
	}

	@Override
	public void onPauseHud(boolean paused) {
		if (instance != null && !instance.isCompleted()) {
			instance.onPauseHud(paused);
		}
	}

	@Override
	public void onScrollHud(int amount) {
		if (instance != null && !instance.isCompleted()) {
			instance.onScrollHud(amount);
		}
	}

	@Override
	public void onMeterControlsOpened() {
		if (instance != null && !instance.isCompleted()) {
			instance.onMeterControlsOpened();
		}
	}

	@Override
	public void onJoinMeterGroup() {
		if (instance != null && !instance.isCompleted()) {
			instance.onJoinMeterGroup();
		}
	}

	@Override
	public void onLeaveMeterGroup() {
		if (instance != null && !instance.isCompleted()) {
			instance.onLeaveMeterGroup();
		}
	}

	@Override
	public void onMeterGroupRefreshed() {
		if (instance != null && !instance.isCompleted()) {
			instance.onMeterGroupRefreshed();
		}
	}

	@Override
	public void onMeterAddRequested(DimPos pos) {
		if (instance != null && !instance.isCompleted()) {
			instance.onMeterAddRequested(pos);
		}
	}

	@Override
	public void onMeterAdded(Meter meter) {
		if (instance != null && !instance.isCompleted()) {
			instance.onMeterAdded(meter);
		}
	}

	@Override
	public void onMeterRemoveRequested(DimPos pos) {
		if (instance != null && !instance.isCompleted()) {
			instance.onMeterRemoveRequested(pos);
		}
	}

	@Override
	public void onMeterRemoved(Meter meter) {
		if (instance != null && !instance.isCompleted()) {
			instance.onMeterRemoved(meter);
		}
	}

	public void tick() {
		if (canDoTutorial()) {
			if (instance == null) {
				if (cooldown < 0) {
					start();
				} else {
					cooldown--;
				}
			} else if (instance.isCompleted()) {
				TutorialStep nextStep = instance.getNextStep();

				if (nextStep != null) {
					advance(nextStep);
				}
			} else if (time++ > MAX_TIME) {
				advance(TutorialStep.NONE);
			} else {
				instance.tick();
			}
		} else {
			stop();
		}
	}

	public void reset() {
		advance(Options.Hidden.TUTORIAL_STEP.getDefault());
	}

	public void advance(TutorialStep step) {
		if (step == Options.Hidden.TUTORIAL_STEP.get()) {
			return;
		}

		Options.Hidden.TUTORIAL_STEP.set(step);
		Options.validate();
		minecraft.options.save();;

		stop();
	}

	private boolean canDoTutorial() {
		// TODO: implement tutorial toasts
		return false /*client.isConnected() && minecraft.world != null*/;
	}

	private void start() {
		stop();

		instance = Options.Hidden.TUTORIAL_STEP.get().createInstance(this);
		instance.start();

		time = 0;
	}

	private void stop() {
		if (instance != null) {
			instance.stop();
			instance = null;
			time = -1;
			cooldown = COOLDOWN;
		}
	}
}
