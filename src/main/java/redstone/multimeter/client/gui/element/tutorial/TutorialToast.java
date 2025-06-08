package redstone.multimeter.client.gui.element.tutorial;

import java.util.List;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.components.toasts.ToastManager;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;

import redstone.multimeter.client.MultimeterClient;

public class TutorialToast implements Toast {

	protected static final ResourceLocation TEXTURE = ResourceLocation.withDefaultNamespace("toast/tutorial");
	protected static final int EDGE = 4;

	private final Component title;
	private final List<FormattedCharSequence> description;

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
	public Visibility getWantedVisibility() {
		return visibility;
	}

	@Override
	public void update(ToastManager toasts, long age) {
	}

	@Override
	public void render(GuiGraphics graphics, Font font, long age) {
		drawBackground(graphics, font, age);

		int x = 7;
		int y = 7;

		graphics.drawString(font, title, x, y, 0xFF500050, false);

		y += 12.0F;

		for (int i = 0; i < description.size(); i++, y += 10.0F) {
			graphics.drawString(font, description.get(i), x, y, 0xFF000000, false);
		}

		drawDecoration(graphics, font, age);
	}

	protected void drawBackground(GuiGraphics graphics, Font toasts, long age) {
		graphics.blitSprite(RenderType::guiTextured, TEXTURE, 0 ,0, width(), height());
	}

	protected void drawDecoration(GuiGraphics graphics, Font toasts, long age) {
	}

	public void hide() {
		visibility = Visibility.HIDE;
	}
}
