package redstone.multimeter.client.gui;

import com.mojang.blaze3d.platform.Window;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.item.ItemStack;

import redstone.multimeter.client.MultimeterClient;
import redstone.multimeter.client.gui.text.Text;
import redstone.multimeter.client.gui.texture.Texture;
import redstone.multimeter.client.gui.texture.TextureRegion;
import redstone.multimeter.client.gui.tooltip.Tooltip;
import redstone.multimeter.mixin.client.GuiGraphicsAccessor;
import redstone.multimeter.util.ColorUtils;

public class GuiRenderer {

	final GuiGraphics graphics;
	final FontRenderer font;

	public GuiRenderer(GuiRenderer delegate) {
		this(delegate.graphics, delegate.font);
	}

	public GuiRenderer(GuiGraphics graphics) {
		this(graphics, MultimeterClient.INSTANCE.getFontRenderer());
	}

	private GuiRenderer(GuiGraphics graphics, FontRenderer font) {
		this.graphics = graphics;
		this.font = font;

		this.font.graphics = this.graphics;
	}

	public void push() {
		this.graphics.pose().pushPose();
	}

	public void translate(double dx, double dy, double dz) {
		this.graphics.pose().translate(dx, dy, dz);
	}

	public void pop() {
		this.graphics.pose().popPose();
	}

	public void fill(int x0, int y0, int x1, int y1, int color) {
		this.graphics.fill(x0, y0, x1, y1, color);
	}

	public void gradient(int x0, int y0, int x1, int y1, int color0, int color1) {
		this.graphics.fillGradient(x0, y0, x1, y1, color0, color1);
	}

	public void highlight(int x0, int y0, int x1, int y1, int color) {
		this.graphics.fill(RenderType.guiTextHighlight(), x0, y0, x1, y1, color);
	}

	public void borders(int x0, int y0, int x1, int y1, int color) {
		this.borders(x0, y0, x1, y1, 1, color);
	}

	public void borders(int x0, int y0, int x1, int y1, int w, int color) {
		this.fill(x0    , y0    , x0 + w, y1 - w, color); // left
		this.fill(x0    , y1 - w, x1 - w, y1    , color); // bottom
		this.fill(x1 - w, y0 + w, x1    , y1    , color); // right
		this.fill(x0 + w, y0    , x1    , y0 + w, color); // top
	}

	public void tooltip(Tooltip tooltip, int mouseX, int mouseY) {
		Window window = MultimeterClient.MINECRAFT.getWindow();

		int width = this.font.width(tooltip) + 8;
		int height = this.font.height(tooltip) + 8;

		int x = mouseX + 15;
		int y = mouseY;

		if (x + width > window.getGuiScaledWidth()) {
			x = mouseX - 15 - width;
		}
		if (y + height > window.getGuiScaledHeight()) {
			y = mouseY - height;
		}

		this.tooltip(tooltip, x, y, x + width, y + height);
	}

	public void tooltip(Tooltip tooltip, int x0, int y0, int x1, int y1) {
		this.tooltip(tooltip, x0, y0, x1, y1, 0xF0100010, 0x505000FF, 0x5028007F);
	}

	public void tooltip(Tooltip tooltip, int x0, int y0, int x1, int y1, int backgroundColor, int borderColor0, int borderColor1) {
		this.push();
		this.translate(0.0D, 0.0D, 400.0D);

		// background
		this.fill(x0    , y0 + 1, x1    , y1 - 1, backgroundColor); // center, left/right outer borders
		this.fill(x0 + 1, y0    , x1 - 1, y0 + 1, backgroundColor); // top outer border
		this.fill(x0 + 1, y1 - 1, x1 - 1, y1    , backgroundColor); // bottom outer border

		// inner border
		this.gradient(x0 + 1, y0 + 2, x0 + 2, y1 - 2, borderColor0, borderColor1); // left
		this.fill    (x0 + 1, y1 - 2, x1 - 1, y1 - 1, borderColor1);               // bottom
		this.gradient(x1 - 2, y0 + 2, x1 - 1, y1 - 2, borderColor0, borderColor1); // right
		this.fill    (x0 + 1, y0 + 1, x1 - 1, y0 + 2, borderColor0);               // top

		int textX = x0 + 4;
		int textY = y0 + 4;

		for (Text line : tooltip) {
			this.drawStringWithShadow(line, textX, textY);
			textY += this.font.height() + 1;
		}

		this.pop();
	}

	public void blit(Texture t, int x0, int y0, int x1, int y1) {
		this.blit(t, x0, y0, x1, y1, 0, 0, t.width, t.height);
	}

	public void blit(Texture t, int x0, int y0, int x1, int y1, int color) {
		this.blit(t, x0, y0, x1, y1, 0, 0, t.width, t.height, color);
	}

	public void blit(Texture t, int x0, int y0, int x1, int y1, int u0, int v0, int u1, int v1) {
		this.blit(t, x0, y0, x1, y1, u0, v0, u1, v1, 0xFFFFFFFF);
	}

	public void blit(Texture t, int x0, int y0, int x1, int y1, int u0, int v0, int u1, int v1, int color) {
		((GuiGraphicsAccessor) this.graphics).rsmm$innerBlit(
			t.location,
			x0,
			x1,
			y0,
			y1,
			0,
			u0 / (float) t.width,
			u1 / (float) t.width,
			v0 / (float) t.height,
			v1 / (float) t.height,
			ColorUtils.getRed(color) / (float) 0xFF,
			ColorUtils.getGreen(color) / (float) 0xFF,
			ColorUtils.getBlue(color) / (float) 0xFF,
			ColorUtils.getAlpha(color) / (float) 0xFF
		);
	}

	public void blit(TextureRegion t, int x0, int y0, int x1, int y1) {
		this.blit(t.texture, x0, y0, x1, y1, t.x, t.y, t.width, t.height);
	}

	public void blit(TextureRegion t, int x0, int y0, int x1, int y1, int color) {
		this.blit(t.texture, x0, y0, x1, y1, t.x, t.y, t.width, t.height, color);
	}

	public void enableScissor(int x0, int y0, int x1, int y1) {
		this.graphics.enableScissor(x0, y0, x1, y1);
	}

	public void disableScissor() {
		this.graphics.disableScissor();
	}

	public void renderItem(ItemStack item, int x, int y) {
		this.graphics.renderFakeItem(item, x, y);
	}

	public void drawString(String s, int x, int y) {
		this.font.draw(s, x, y);
	}

	public void drawString(Text t, int x, int y) {
		this.font.draw(t, x, y);
	}

	public void drawString(String s, int x, int y, int color) {
		this.font.draw(s, x, y, color);
	}

	public void drawString(Text t, int x, int y, int color) {
		this.font.draw(t, x, y, color);
	}

	public void drawStringWithShadow(String s, int x, int y) {
		this.font.drawWithShadow(s, x, y);
	}

	public void drawStringWithShadow(Text t, int x, int y) {
		this.font.drawWithShadow(t, x, y);
	}

	public void drawStringWithShadow(String s, int x, int y, int color) {
		this.font.drawWithShadow(s, x, y, color);
	}

	public void drawStringWithShadow(Text t, int x, int y, int color) {
		this.font.drawWithShadow(t, x, y, color);
	}

	public void drawString(String s, int x, int y, int color, boolean shadow) {
		this.font.draw(s, x, y, color, shadow);
	}

	public void drawString(Text t, int x, int y, int color, boolean shadow) {
		this.font.draw(t, x, y, color, shadow);
	}

	public int width(String s) {
		return this.font.width(s);
	}

	public int width(Text t) {
		return this.font.width(t);
	}

	public int height() {
		return this.font.height();
	}
}
