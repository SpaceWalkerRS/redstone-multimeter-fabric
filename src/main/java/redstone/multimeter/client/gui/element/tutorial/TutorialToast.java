package redstone.multimeter.client.gui.element.tutorial;

import java.util.List;

import com.mojang.blaze3d.platform.GlStateManager;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.toast.Toast;
import net.minecraft.client.gui.toast.ToastGui;
import net.minecraft.client.render.TextRenderer;
import net.minecraft.text.Text;

public class TutorialToast implements Toast {

	protected static final int TEXTURE_U = 0;
	protected static final int TEXTURE_V = 96;
	protected static final int TEXTURE_WIDTH = 160;
	protected static final int TEXTURE_HEIGHT = 32;
	protected static final int EDGE = 4;
	protected static final int INNER_WIDTH = TEXTURE_WIDTH - 2 * EDGE;
	protected static final int INNER_HEIGHT = TEXTURE_HEIGHT - 2 * EDGE;

	private final Text title;
	private final List<String> description;

	private final int toastWidth;
	private final int toastHeight;

	private Visibility visibility;

	public TutorialToast(Text title, Text description) {
		Minecraft client = Minecraft.getInstance();
		TextRenderer textRenderer = client.textRenderer;

		this.toastWidth = 200;

		this.title = title;
		this.description = textRenderer.split(description.getFormattedString(), width() - 14);

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
	public Visibility render(ToastGui toasts, long animationTime) {
		drawBackground(toasts, animationTime);

		Minecraft minecraft = toasts.getMinecraft();
		TextRenderer textRenderer = minecraft.textRenderer;

		float x = 7.0F;
		float y = 7.0F;

		textRenderer.draw(title.getFormattedString(), x, y, 0xFF500050);

		y += 12.0F;

		for (int i = 0; i < description.size(); i++, y += 10.0F) {
			textRenderer.draw(description.get(i), x, y, 0xFF000000);
		}

		drawDecoration(toasts, animationTime);

		return visibility;
	}

	protected void drawBackground(ToastGui toasts, long animationTime) {
		toasts.getMinecraft().getTextureManager().bind(TOASTS_LOCATION);
		GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);

		int width = width();
		int height = height();

		if (width == TEXTURE_WIDTH && height == TEXTURE_HEIGHT) {
			toasts.drawTexture(0, 0, TEXTURE_U, TEXTURE_V, TEXTURE_WIDTH, TEXTURE_HEIGHT);
		} else {
			int x = 0;
			int y = 0;
			int u = TEXTURE_U;
			int v = TEXTURE_V;
			int w = EDGE;
			int h = EDGE;

			toasts.drawTexture(x, y, u, v, w, h); // top left corner

			u += EDGE;
			w = INNER_WIDTH;

			for (x = EDGE; x < (width - w - EDGE); x += w) {
				toasts.drawTexture(x, y, u, v, w, h); // top edge
			}

			w = width - x;
			u = TEXTURE_U + TEXTURE_WIDTH - w;

			toasts.drawTexture(x, y, u, v, w, h); // top right corner

			v += EDGE;
			h = INNER_HEIGHT;

			for (y = EDGE; y < (height - h - EDGE); y += h) {
				x = 0;
				u = TEXTURE_U;
				w = EDGE;

				toasts.drawTexture(x, y, u, v, w, h); // left edge

				u += EDGE;
				w = INNER_WIDTH;

				for (x = EDGE; x < (width - w - EDGE); x += w) {
					toasts.drawTexture(x, y, u, v, w, h); // middle
				}

				w = width - x;
				u = TEXTURE_U + TEXTURE_WIDTH - w;

				toasts.drawTexture(x, y, u, v, w, h); // right edge
			}

			h = height - y;
			v = TEXTURE_V + TEXTURE_HEIGHT - h;

			x = 0;
			u = TEXTURE_U;
			w = EDGE;

			toasts.drawTexture(x, y, u, v, w, h); // bottom left corner

			u += EDGE;
			w = INNER_WIDTH;

			for (x = EDGE; x < (width - w - EDGE); x += w) {
				toasts.drawTexture(x, y, u, v, w, h); // bottom edge
			}

			w = width - x;
			u = TEXTURE_U + TEXTURE_WIDTH - w;

			toasts.drawTexture(x, y, u, v, w, h); // bottom right corner
		}
	}

	protected void drawDecoration(ToastGui toasts, long animationTime) {
	}

	public void hide() {
		visibility = Visibility.HIDE;
	}
}
