package redstone.multimeter.client.gui.element.tutorial;

import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.toasts.ToastComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

import redstone.multimeter.client.tutorial.instance.StagedTutorialInstance;

public class StagedTutorialToast extends TutorialToast {

	private final StagedTutorialInstance tutorial;

	private float lastProgress;
	private float progress;
	private long ageAtLastProgressUpdate;

	public StagedTutorialToast(StagedTutorialInstance tutorial, Component title, Component description) {
		super(title, description);

		this.tutorial = tutorial;
	}

	@Override
	protected void drawDecoration(ToastComponent toasts, long age) {
		float newProgress = tutorial.getProgress();

		if (newProgress != progress) {
			lastProgress = progress;
			progress = newProgress;
			ageAtLastProgressUpdate = age;
		}

		int width = width();
		int height = height();
		int edge = EDGE - 1;

		float renderProgress = (float)Mth.clampedLerp(lastProgress, progress, (age - ageAtLastProgressUpdate) / 100.0F);

		int x = edge;
		int y = height - edge;
		int w = width - 2 * edge;
		int w2 = (int)(w * renderProgress);
		int h = 1;

		GuiComponent.fill(x, y, x + w, y + h, 0xFFFFFFFF);
		GuiComponent.fill(x, y, x + w2, y + h, 0xFF500050);
	}
}
