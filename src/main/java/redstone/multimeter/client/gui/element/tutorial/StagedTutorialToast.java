package redstone.multimeter.client.gui.element.tutorial;

import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.toast.ToastManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

import redstone.multimeter.client.tutorial.instance.StagedTutorialInstance;

public class StagedTutorialToast extends TutorialToast {
	
	private final StagedTutorialInstance tutorial;
	
	private float lastProgress;
	private float progress;
	private long ageAtLastProgressUpdate;
	
	public StagedTutorialToast(StagedTutorialInstance tutorial, Text title, Text description) {
		super(title, description);
		
		this.tutorial = tutorial;
	}
	
	@Override
	protected void drawDecoration(MatrixStack matrices, ToastManager manager, long age) {
		float newProgress = tutorial.getProgress();
		
		if (newProgress != progress) {
			lastProgress = progress;
			progress = newProgress;
			ageAtLastProgressUpdate = age;
		}
		
		int width = getWidth();
		int height = getHeight();
		int edge = EDGE - 1;
		
		float renderProgress = MathHelper.clampedLerp(lastProgress, progress, (age - ageAtLastProgressUpdate) / 100.0F);
		
		int x = edge;
		int y = height - edge;
		int w = width - 2 * edge;
		int w2 = (int)(w * renderProgress);
		int h = 1;
		
		DrawableHelper.fill(matrices, x, y, x + w, y + h, 0xFFFFFFFF);
		DrawableHelper.fill(matrices, x, y, x + w2, y + h, 0xFF500050);
	}
}
