package redstone.multimeter.client.gui.hud;

import net.minecraft.item.ItemStack;

import redstone.multimeter.client.gui.GuiRenderer;
import redstone.multimeter.client.gui.element.Element;
import redstone.multimeter.client.gui.text.Text;
import redstone.multimeter.client.gui.texture.Texture;
import redstone.multimeter.client.gui.texture.TextureRegion;
import redstone.multimeter.client.gui.tooltip.Tooltip;
import redstone.multimeter.util.ColorUtils;

public class HudRenderer extends GuiRenderer {

	private final MultimeterHud hud;

	private Element target;

	public HudRenderer(MultimeterHud hud, GuiRenderer renderer) {
		super(renderer);

		this.hud = hud;
	}

	public void render(Element element, int mouseX, int mouseY) {
		(this.target = element).render(this, mouseX, mouseY);
	}

	private int x(int x0, int x1) {
		switch (this.hud.getDirectionalityX()) {
		default:
		case LEFT_TO_RIGHT:
			return this.target.getX() + x0;
		case RIGHT_TO_LEFT:
			return (this.target.getX() + this.target.getWidth()) - x1;
		}
	}

	private int y(int y0, int y1) {
		switch (this.hud.getDirectionalityY()) {
		default:
		case TOP_TO_BOTTOM:
			return this.target.getY() + y0;
		case BOTTOM_TO_TOP:
			return (this.target.getY() + this.target.getHeight()) - y1;
		}
	}

	private int x(String s, int x) {
		return this.x(x, x + this.width(s) - 1);
	}

	private int x(Text t, int x) {
		return this.x(x, x + this.width(t) - 1);
	}

	private int y(String s, int y) {
		return this.y(y, y + this.height() - 2);
	}

	private int y(Text t, int y) {
		return this.y(y, y + this.height() - 2);
	}

	private int color(int color) {
		return ColorUtils.setAlpha(color, Math.round(ColorUtils.getAlpha(color) * hud.settings.opacity() / 100.0F));
	}

	@Override
	public void fill(int x0, int y0, int x1, int y1, int color) {
		super.fill(x(x0, x1), y(y0, y1), x(x1, x0), y(y1, y0), color(color));
	}

	@Override
	public void gradient(int x0, int y0, int x1, int y1, int color0, int color1) {
		super.gradient(x(x0, x1), y(y0, y1), x(x1, x0), y(y1, y0), color(color0), color(color1));
	}

	@Override
	public void highlight(int x0, int y0, int x1, int y1, int color) {
		super.highlight(x(x0, x1), y(y0, y1), x(x1, x0), y(y1, y0), color(color));
	}

	@Override
	public void tooltip(Tooltip tooltip, int x0, int y0, int x1, int y1) {
		super.tooltip(tooltip, x(x0, x1), y(y0, y1), x(x1, x0), y(y1, y0));
	}

	@Override
	public void tooltip(Tooltip tooltip, int x0, int y0, int x1, int y1, int backgroundColor, int borderColor0, int borderColor1) {
		super.tooltip(tooltip, x(x0, x1), y(y0, y1), x(x1, x0), y(y1, y0), color(backgroundColor), color(borderColor0), color(borderColor1));
	}

	@Override
	public void blit(Texture t, int x0, int y0, int x1, int y1) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void blit(Texture t, int x0, int y0, int x1, int y1, int color) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void blit(Texture t, int x0, int y0, int x1, int y1, int u0, int v0, int u1, int v1) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void blit(Texture t, int x0, int y0, int x1, int y1, int u0, int v0, int u1, int v1, int color) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void blit(TextureRegion t, int x0, int y0, int x1, int y1) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void blit(TextureRegion t, int x0, int y0, int x1, int y1, int color) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void pushScissor(int x0, int y0, int x1, int y1) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void popScissor() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void renderItem(ItemStack item, int x, int y) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void drawString(String s, int x, int y) {
		super.drawString(s, x(s, x), y(s, y));
	}

	@Override
	public void drawString(Text t, int x, int y) {
		super.drawString(t, x(t, x), y(t, y));
	}

	@Override
	public void drawString(String s, int x, int y, int color) {
		super.drawString(s, x(s, x), y(s, y), color);
	}

	@Override
	public void drawString(Text t, int x, int y, int color) {
		super.drawString(t, x(t, x), y(t, y), color);
	}

	@Override
	public void drawStringWithShadow(String s, int x, int y) {
		super.drawStringWithShadow(s, x(s, x), y(s, y));
	}

	@Override
	public void drawStringWithShadow(Text t, int x, int y) {
		super.drawStringWithShadow(t, x(t, x), y(t, y));
	}

	@Override
	public void drawStringWithShadow(String s, int x, int y, int color) {
		super.drawStringWithShadow(s, x(s, x), y(s, y), color);
	}

	@Override
	public void drawStringWithShadow(Text t, int x, int y, int color) {
		super.drawStringWithShadow(t, x(t, x), y(t, y), color);
	}

	@Override
	public void drawString(String s, int x, int y, int color, boolean shadow) {
		super.drawString(s, x(s, x), y(s, y), color, shadow);
	}

	@Override
	public void drawString(Text t, int x, int y, int color, boolean shadow) {
		super.drawString(t, x(t, x), y(t, y), color, shadow);
	}
}
