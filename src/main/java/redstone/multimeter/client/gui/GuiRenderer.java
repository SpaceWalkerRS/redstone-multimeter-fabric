package redstone.multimeter.client.gui;

import com.mojang.blaze3d.platform.Window;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.world.item.ItemStack;

import redstone.multimeter.client.MultimeterClient;
import redstone.multimeter.client.gui.text.Text;
import redstone.multimeter.client.gui.texture.Texture;
import redstone.multimeter.client.gui.texture.TextureRegion;
import redstone.multimeter.client.gui.tooltip.Tooltip;
import redstone.multimeter.mixin.client.GuiGraphicsAccessor;

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

		DepthOverride.reset();
	}

	public void pushMatrix() {
		this.graphics.pose().pushMatrix();
		DepthOverride.push();
	}

	public void translate(double dx, double dy, double dz) {
		this.graphics.pose().translate((float) dx, (float) dy);
		DepthOverride.translate((float) dz);
	}

	public void popMatrix() {
		this.graphics.pose().popMatrix();
		DepthOverride.pop();
	}

	public void fill(int x0, int y0, int x1, int y1, int color) {
		this.graphics.fill(x0, y0, x1, y1, color);
	}

	public void gradient(int x0, int y0, int x1, int y1, int color0, int color1) {
		this.graphics.fillGradient(x0, y0, x1, y1, color0, color1);
	}

	public void highlight(int x0, int y0, int x1, int y1, int color) {
		this.graphics.fill(RenderPipelines.GUI_INVERT, x0, y0, x1, y1, 0xFFFFFFFF);
		this.graphics.fill(RenderPipelines.GUI_TEXT_HIGHLIGHT, x0, y0, x1, y1, color);
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
		this.pushMatrix();
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

		this.popMatrix();
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
			RenderPipelines.GUI_TEXTURED,
			t.location,
			x0,
			x1,
			y0,
			y1,
			u0 / (float) t.width,
			u1 / (float) t.width,
			v0 / (float) t.height,
			v1 / (float) t.height,
			color
		);
	}

	public void blit(TextureRegion t, int x0, int y0, int x1, int y1) {
		this.blit(t.texture, x0, y0, x1, y1, t.x, t.y, t.x + t.width, t.y + t.height);
	}

	public void blit(TextureRegion t, int x0, int y0, int x1, int y1, int color) {
		this.blit(t.texture, x0, y0, x1, y1, t.x, t.y, t.x + t.width, t.y + t.height, color);
	}

	public void blitSpliced(Texture t, int x0, int y0, int x1, int y1, int edge) {
		this.blitSpliced(t, x0, y0, x1, y1, 0, 0, t.width, t.height, edge);
	}

	public void blitSpliced(Texture t, int x0, int y0, int x1, int y1, int edge, int color) {
		this.blitSpliced(t, x0, y0, x1, y1, 0, 0, t.width, t.height, edge, color);
	}

	public void blitSpliced(Texture t, int x0, int y0, int x1, int y1, int u0, int v0, int u1, int v1, int edge) {
		this.blitSpliced(t, x0, y0, x1, y1, u0, v0, u1, v1, edge, 0xFFFFFFFF);
	}

	public void blitSpliced(Texture t, int x0, int y0, int x1, int y1, int u0, int v0, int u1, int v1, int edge, int color) {
		boolean mw = (x1 - x0) == (u1 - u0);
		boolean mh = (y1 - y0) == (v1 - v0);

		if (mw && mh) {
			this.blitTiled(t, x0, y0, x1, y1, u0, v0, u1, v1, color);
		} else if (mw) {
			this.blitTiled(t, x0, y0       , x1, y0 + edge, u0, v0       , u1, v0 + edge, color); // top
			this.blitTiled(t, x0, y0 + edge, x1, y1 - edge, u0, v0 + edge, u1, v1 - edge, color); // middle
			this.blitTiled(t, x0, y1 - edge, x1, y1       , u0, v1 - edge, u1, v1       , color); // bottom
		} else if (mh) {
			this.blitTiled(t, x0       , y0, x0 + edge, y1, u0       , v0, u0 + edge, v1, color); // left
			this.blitTiled(t, x0 + edge, y0, x1 - edge, y1, u0 + edge, v0, u1 - edge, v1, color); // middle
			this.blitTiled(t, x1 - edge, y0, x1       , y1, u1 - edge, v0, u1       , v1, color); // right
		} else {
			this.blitTiled(t, x0       , y0       , x0 + edge, y0 + edge, u0       , v0       , u0 + edge, v0 + edge, color); // top-left
			this.blitTiled(t, x0 + edge, y0       , x1 - edge, y0 + edge, u0 + edge, v0       , u1 - edge, v0 + edge, color); // top
			this.blitTiled(t, x1 - edge, y0       , x1       , y0 + edge, u1 - edge, v0       , u1       , v0 + edge, color); // top-right
			this.blitTiled(t, x0       , y0 + edge, x0 + edge, y1 - edge, u0       , v0 + edge, u0 + edge, v1 - edge, color); // left
			this.blitTiled(t, x0 + edge, y0 + edge, x1 - edge, y1 - edge, u0 + edge, v0 + edge, u1 - edge, v1 - edge, color); // middle
			this.blitTiled(t, x1 - edge, y0 + edge, x1       , y1 - edge, u1 - edge, v0 + edge, u1       , v1 - edge, color); // right
			this.blitTiled(t, x0       , y1 - edge, x0 + edge, y1       , u0       , v1 - edge, u0 + edge, v1       , color); // bottom-left
			this.blitTiled(t, x0 + edge, y1 - edge, x1 - edge, y1       , u0 + edge, v1 - edge, u1 - edge, v1       , color); // bottom
			this.blitTiled(t, x1 - edge, y1 - edge, x1       , y1       , u1 - edge, v1 - edge, u1       , v1       , color); // bottom-right
		}
	}

	public void blitSpliced(TextureRegion t, int x0, int y0, int x1, int y1, int edge) {
		this.blitSpliced(t.texture, x0, y0, x1, y1, t.x, t.y, t.x + t.width, t.y + t.height, edge);
	}

	public void blitSpliced(TextureRegion t, int x0, int y0, int x1, int y1, int edge, int color) {
		this.blitSpliced(t.texture, x0, y0, x1, y1, t.x, t.y, t.x + t.width, t.y + t.height, edge, color);
	}

	public void blitTiled(Texture t, int x0, int y0, int x1, int y1) {
		this.blitTiled(t, x0, y0, x1, y1, 0, 0, t.width, t.height);
	}

	public void blitTiled(Texture t, int x0, int y0, int x1, int y1, int color) {
		this.blitTiled(t, x0, y0, x1, y1, 0, 0, t.width, t.height, color);
	}

	public void blitTiled(Texture t, int x0, int y0, int x1, int y1, int u0, int v0, int u1, int v1) {
		this.blitTiled(t, x0, y0, x1, y1, u0, v0, u1, v1, 0xFFFFFFFF);
	}

	public void blitTiled(Texture t, int x0, int y0, int x1, int y1, int u0, int v0, int u1, int v1, int color) {
		int xs = x0;
		int ys = y0;
		int xf = x1;
		int yf = y1;

		for (x0 = xs; x0 < xf; x0 = x1) {
			x1 = Math.min(x0 + (u1 - u0), xf);
			u1 = Math.min(u1, u0 + (x1 - x0));

			for (y0 = ys; y0 < yf; y0 = y1) {
				y1 = Math.min(y0 + (v1 - v0), yf);
				v1 = Math.min(v1, v0 + (y1 - y0));

				this.blit(t, x0, y0, x1, y1, u0, v0, u1, v1, color);
			} 
		}
	}

	public void blitTiled(TextureRegion t, int x0, int y0, int x1, int y1) {
		this.blitTiled(t.texture, x0, y0, x1, y1, t.x, t.y, t.x + t.width, t.y + t.height);
	}

	public void blitTiled(TextureRegion t, int x0, int y0, int x1, int y1, int color) {
		this.blitTiled(t.texture, x0, y0, x1, y1, t.x, t.y, t.x + t.width, t.y + t.height, color);
	}

	public void pushScissor(int x0, int y0, int x1, int y1) {
		this.graphics.enableScissor(x0, y0, x1, y1);
	}

	public void popScissor() {
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
