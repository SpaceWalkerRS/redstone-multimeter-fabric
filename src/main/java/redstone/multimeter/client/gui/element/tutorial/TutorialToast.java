package redstone.multimeter.client.gui.element.tutorial;

import java.util.List;

import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.components.toasts.ToastComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;

public class TutorialToast implements Toast {

	protected static final int TEXTURE_U = 0;
	protected static final int TEXTURE_V = 96;
	protected static final int TEXTURE_WIDTH = 160;
	protected static final int TEXTURE_HEIGHT = 32;
	protected static final int EDGE = 4;
	protected static final int INNER_WIDTH = TEXTURE_WIDTH - 2 * EDGE;
	protected static final int INNER_HEIGHT = TEXTURE_HEIGHT - 2 * EDGE;

	private final Component title;
	private final List<FormattedCharSequence> description;

	private final int toastWidth;
	private final int toastHeight;

	private Visibility visibility;

	public TutorialToast(Component title, Component description) {
		Minecraft client = Minecraft.getInstance();
		Font font = client.font;

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
	public Visibility render(GuiGraphics graphics, ToastComponent toasts, long age) {
		drawBackground(graphics, toasts, age);

		Minecraft client = toasts.getMinecraft();
		Font font = client.font;

		int x = 7;
		int y = 7;

		graphics.drawString(font, title, x, y, 0xFF500050, false);

		y += 12.0F;

		for (int i = 0; i < description.size(); i++, y += 10.0F) {
			graphics.drawString(font, description.get(i), x, y, 0xFF000000, false);
		}

		drawDecoration(graphics, toasts, age);

		return visibility;
	}

	protected void drawBackground(GuiGraphics graphics, ToastComponent toasts, long age) {
		RenderSystem.setShaderTexture(0, TEXTURE);
		RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);

		int width = width();
		int height = height();

		if (width == TEXTURE_WIDTH && height == TEXTURE_HEIGHT) {
			graphics.blit(TEXTURE, 0, 0, TEXTURE_U, TEXTURE_V, TEXTURE_WIDTH, TEXTURE_HEIGHT);
		} else {
			int x = 0;
			int y = 0;
			int u = TEXTURE_U;
			int v = TEXTURE_V;
			int w = EDGE;
			int h = EDGE;

			graphics.blit(TEXTURE, x, y, u, v, w, h); // top left corner

			u += EDGE;
			w = INNER_WIDTH;

			for (x = EDGE; x < (width - w - EDGE); x += w) {
				graphics.blit(TEXTURE, x, y, u, v, w, h); // top edge
			}

			w = width - x;
			u = TEXTURE_U + TEXTURE_WIDTH - w;

			graphics.blit(TEXTURE, x, y, u, v, w, h); // top right corner

			v += EDGE;
			h = INNER_HEIGHT;

			for (y = EDGE; y < (height - h - EDGE); y += h) {
				x = 0;
				u = TEXTURE_U;
				w = EDGE;

				graphics.blit(TEXTURE, x, y, u, v, w, h); // left edge

				u += EDGE;
				w = INNER_WIDTH;

				for (x = EDGE; x < (width - w - EDGE); x += w) {
					graphics.blit(TEXTURE, x, y, u, v, w, h); // middle
				}

				w = width - x;
				u = TEXTURE_U + TEXTURE_WIDTH - w;

				graphics.blit(TEXTURE, x, y, u, v, w, h); // right edge
			}

			h = height - y;
			v = TEXTURE_V + TEXTURE_HEIGHT - h;

			x = 0;
			u = TEXTURE_U;
			w = EDGE;

			graphics.blit(TEXTURE, x, y, u, v, w, h); // bottom left corner

			u += EDGE;
			w = INNER_WIDTH;

			for (x = EDGE; x < (width - w - EDGE); x += w) {
				graphics.blit(TEXTURE, x, y, u, v, w, h); // bottom edge
			}

			w = width - x;
			u = TEXTURE_U + TEXTURE_WIDTH - w;

			graphics.blit(TEXTURE, x, y, u, v, w, h); // bottom right corner
		}
	}

	protected void drawDecoration(GuiGraphics graphics, ToastComponent toasts, long age) {
	}

	public void hide() {
		visibility = Visibility.HIDE;
	}
}
