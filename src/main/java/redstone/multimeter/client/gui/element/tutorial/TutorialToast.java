package redstone.multimeter.client.gui.element.tutorial;

import java.util.List;
import java.util.stream.Collectors;

import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.components.toasts.ToastComponent;

import redstone.multimeter.client.MultimeterClient;
import redstone.multimeter.client.gui.FontRenderer;
import redstone.multimeter.client.gui.GuiRenderer;
import redstone.multimeter.client.gui.text.Text;
import redstone.multimeter.client.gui.text.Texts;
import redstone.multimeter.client.gui.texture.TextureRegion;
import redstone.multimeter.client.gui.texture.TextureRegions;

public class TutorialToast implements Toast {

	protected static final TextureRegion TEXTURE = TextureRegions.TUTORIAL_TOAST;
	protected static final int EDGE = 4;

	private final Text title;
	private final List<Text> description;

	private final int toastWidth;
	private final int toastHeight;

	private Visibility visibility;

	public TutorialToast(Text title, Text description) {
		FontRenderer font = MultimeterClient.INSTANCE.getFontRenderer();

		this.toastWidth = 200;

		this.title = title;
		this.description = font.split(description.buildString(), width() - 14).stream().map(Texts::literal).collect(Collectors.toList());
//		this.description = font.split(description, width() - 14);

		this.toastHeight = 10 + 12 + 10 * this.description.size();

		this.visibility = Visibility.SHOW;
	}

	public int width() {
		return toastWidth;
	}

	public int height() {
		return toastHeight;
	}

	@Override
	public Visibility render(ToastComponent toasts, long age) {
		GuiRenderer renderer = new GuiRenderer(
		);

		drawBackground(renderer, toasts, age);

		int x = 7;
		int y = 7;

		renderer.drawString(title, x, y, 0xFF500050);

		y += 12.0F;

		for (int i = 0; i < description.size(); i++, y += 10.0F) {
			renderer.drawString(description.get(i), x, y, 0xFF000000);
		}

		drawDecoration(renderer, toasts, age);

		return visibility;
	}

	protected void drawBackground(GuiRenderer renderer, ToastComponent toasts, long age) {
		renderer.blitSpliced(TEXTURE, 0 ,0, width(), height(), EDGE);
	}

	protected void drawDecoration(GuiRenderer renderer, ToastComponent toasts, long age) {
	}

	public void hide() {
		visibility = Visibility.HIDE;
	}
}
