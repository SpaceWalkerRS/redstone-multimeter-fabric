package redstone.multimeter.client.gui.element.tutorial;

import java.util.List;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.components.toasts.ToastComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;

import redstone.multimeter.client.MultimeterClient;

public class TutorialToast implements Toast {

	protected static final int TEXTURE_U = 0;
	protected static final int TEXTURE_V = 96;
	protected static final int TEXTURE_WIDTH = 160;
	protected static final int TEXTURE_HEIGHT = 32;
	protected static final int EDGE = 4;
	protected static final int INNER_WIDTH = TEXTURE_WIDTH - 2 * EDGE;
	protected static final int INNER_HEIGHT = TEXTURE_HEIGHT - 2 * EDGE;

	private final Component title;
	private final List<FormattedText> description;

	private final int toastWidth;
	private final int toastHeight;

	private Visibility visibility;

	public TutorialToast(Component title, Component description) {
		Font font = MultimeterClient.MINECRAFT.font;

		this.toastWidth = 200;

		this.title = title;
		this.description = font.split(description, width() - 14);

		this.toastHeight = 10 + 12 + 10 * this.description.size();

		this.visibility = Visibility.SHOW;
	}

	@Override
	public int width() {
		return toastWidth;
	}

	@Override
	public int height() {
		return toastHeight;
	}

	@Override
	public Visibility render(PoseStack poses, ToastComponent toasts, long age) {
		drawBackground(poses, toasts, age);

		Font font = MultimeterClient.MINECRAFT.font;

		float x = 7.0F;
		float y = 7.0F;

		font.draw(poses, title, x, y, 0xFF500050);

		y += 12.0F;

		for (int i = 0; i < description.size(); i++, y += 10.0F) {
			font.draw(poses, description.get(i), x, y, 0xFF000000);
		}

		drawDecoration(poses, toasts, age);

		return visibility;
	}

	protected void drawBackground(PoseStack poses, ToastComponent toasts, long age) {
		toasts.getMinecraft().getTextureManager().bind(TEXTURE);
		RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);

		int width = width();
		int height = height();

		if (width == TEXTURE_WIDTH && height == TEXTURE_HEIGHT) {
			toasts.blit(poses, 0, 0, TEXTURE_U, TEXTURE_V, TEXTURE_WIDTH, TEXTURE_HEIGHT);
		} else {
			int x = 0;
			int y = 0;
			int u = TEXTURE_U;
			int v = TEXTURE_V;
			int w = EDGE;
			int h = EDGE;

			toasts.blit(poses, x, y, u, v, w, h); // top left corner

			u += EDGE;
			w = INNER_WIDTH;

			for (x = EDGE; x < (width - w - EDGE); x += w) {
				toasts.blit(poses, x, y, u, v, w, h); // top edge
			}

			w = width - x;
			u = TEXTURE_U + TEXTURE_WIDTH - w;

			toasts.blit(poses, x, y, u, v, w, h); // top right corner

			v += EDGE;
			h = INNER_HEIGHT;

			for (y = EDGE; y < (height - h - EDGE); y += h) {
				x = 0;
				u = TEXTURE_U;
				w = EDGE;

				toasts.blit(poses, x, y, u, v, w, h); // left edge

				u += EDGE;
				w = INNER_WIDTH;

				for (x = EDGE; x < (width - w - EDGE); x += w) {
					toasts.blit(poses, x, y, u, v, w, h); // middle
				}

				w = width - x;
				u = TEXTURE_U + TEXTURE_WIDTH - w;

				toasts.blit(poses, x, y, u, v, w, h); // right edge
			}

			h = height - y;
			v = TEXTURE_V + TEXTURE_HEIGHT - h;

			x = 0;
			u = TEXTURE_U;
			w = EDGE;

			toasts.blit(poses, x, y, u, v, w, h); // bottom left corner

			u += EDGE;
			w = INNER_WIDTH;

			for (x = EDGE; x < (width - w - EDGE); x += w) {
				toasts.blit(poses, x, y, u, v, w, h); // bottom edge
			}

			w = width - x;
			u = TEXTURE_U + TEXTURE_WIDTH - w;

			toasts.blit(poses, x, y, u, v, w, h); // bottom right corner
		}
	}

	protected void drawDecoration(PoseStack poses, ToastComponent toasts, long age) {
	}

	public void hide() {
		visibility = Visibility.HIDE;
	}
}
