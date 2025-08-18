package redstone.multimeter.client.tutorial;

import net.minecraft.client.Minecraft;
import net.minecraft.client.tutorial.TutorialSteps;

import redstone.multimeter.client.MultimeterClient;
import redstone.multimeter.client.gui.element.tutorial.TutorialToast;
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

	private TutorialInstance step;
	private TutorialToast toast;
	private int time;
	private int cooldown;

	public Tutorial(MultimeterClient client) {
		this.minecraft = client.getMinecraft();
		this.client = client;
	}

	@Override
	public void onScreenOpened(RSMMScreen screen) {
		if (this.step != null && !this.step.isCompleted()) {
			this.step.onScreenOpened(screen);
		}
	}

	@Override
	public void onToggleHud(boolean enabled) {
		if (this.step != null && !this.step.isCompleted()) {
			this.step.onToggleHud(enabled);
		}
	}

	@Override
	public void onToggleFocusMode(boolean enabled) {
		if (this.step != null && !this.step.isCompleted()) {
			this.step.onToggleFocusMode(enabled);
		}
	}

	@Override
	public void onPauseHud(boolean paused) {
		if (this.step != null && !this.step.isCompleted()) {
			this.step.onPauseHud(paused);
		}
	}

	@Override
	public void onScrollHud(int amount) {
		if (this.step != null && !this.step.isCompleted()) {
			this.step.onScrollHud(amount);
		}
	}

	@Override
	public void onMeterGroupSaved(int slot) {
		if (this.step != null && !this.step.isCompleted()) {
			this.step.onMeterGroupSaved(slot);
		}
	}

	@Override
	public void onMeterGroupLoaded(int slot) {
		if (this.step != null && !this.step.isCompleted()) {
			this.step.onMeterGroupLoaded(slot);
		}
	}

	@Override
	public void onMeterControlsOpened() {
		if (this.step != null && !this.step.isCompleted()) {
			this.step.onMeterControlsOpened();
		}
	}

	@Override
	public void onJoinMeterGroup() {
		if (this.step != null && !this.step.isCompleted()) {
			this.step.onJoinMeterGroup();
		}
	}

	@Override
	public void onLeaveMeterGroup() {
		if (this.step != null && !this.step.isCompleted()) {
			this.step.onLeaveMeterGroup();
		}
	}

	@Override
	public void onMeterGroupRefreshed() {
		if (this.step != null && !this.step.isCompleted()) {
			this.step.onMeterGroupRefreshed();
		}
	}

	@Override
	public void onMeterAddRequested(DimPos pos) {
		if (this.step != null && !this.step.isCompleted()) {
			this.step.onMeterAddRequested(pos);
		}
	}

	@Override
	public void onMeterAdded(Meter meter) {
		if (this.step != null && !this.step.isCompleted()) {
			this.step.onMeterAdded(meter);
		}
	}

	@Override
	public void onMeterRemoveRequested(DimPos pos) {
		if (this.step != null && !this.step.isCompleted()) {
			this.step.onMeterRemoveRequested(pos);
		}
	}

	@Override
	public void onMeterRemoved(Meter meter) {
		if (this.step != null && !this.step.isCompleted()) {
			this.step.onMeterRemoved(meter);
		}
	}

	private boolean isEnabled() {
		return this.client.isConnected() && this.minecraft.world != null && this.minecraft.options.tutorialStep == TutorialSteps.NONE;
	}

	public void tick() {
		if (this.isEnabled()) {
			if (this.step == null) {
				this.init();
			} else if (this.cooldown >= 0) {
				if (this.cooldown == 0) {
					this.startStep();
				} else {
					this.cooldown--;
				}
			} else if (this.step.isCompleted()) {
				TutorialStep nextStep = this.step.nextStep();

				if (nextStep != null) {
					this.nextStep(nextStep);
				}
			} else if (this.time++ > MAX_TIME) {
				this.nextStep(TutorialStep.NONE);
			} else {
				this.step.tick();
			}
		} else {
			this.reset();
		}
	}

	private void init() {
		this.step = Options.Hidden.TUTORIAL_STEP.get().createInstance();
		this.initStep();
	}

	private void reset() {
		this.resetStep();
		this.step = null;
	}

	private void nextStep(TutorialStep step) {
		if (step == Options.Hidden.TUTORIAL_STEP.get()) {
			return;
		}

		this.resetStep();

		Options.Hidden.TUTORIAL_STEP.set(step);
		Options.validate();
		this.minecraft.options.save();

		this.initStep();
	}

	private void initStep() {
		this.step = Options.Hidden.TUTORIAL_STEP.get().createInstance();
		this.toast = this.step.createToast();

		this.step.init();

		this.time = 0;
		this.cooldown = COOLDOWN;
	}

	private void startStep() {
		if (this.toast != null) {
			this.minecraft.getToasts().add(this.toast);
		}

		this.time = 0;
		this.cooldown = -1;
	}

	private void resetStep() {
		if (this.toast != null) {
			this.toast.hide();
		}

		this.time = -1;
		this.cooldown = -1;
	}
}
