package redstone.multimeter.client.gui;

import java.util.Stack;

import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.platform.GlStateManager.DestFactor;
import com.mojang.blaze3d.platform.GlStateManager.SourceFactor;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tessellator;

import net.minecraft.client.render.Window;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.item.ItemStack;

import redstone.multimeter.client.MultimeterClient;
import redstone.multimeter.client.gui.text.Text;
import redstone.multimeter.client.gui.texture.Texture;
import redstone.multimeter.client.gui.texture.TextureRegion;
import redstone.multimeter.client.gui.tooltip.Tooltip;
import redstone.multimeter.util.ColorUtils;

public class GuiRenderer {

	final FontRenderer font;
	private final ItemRenderer itemRenderer;
	private final ScissorStack scissorStack = new ScissorStack();

	public GuiRenderer(GuiRenderer delegate) {
		this(delegate.font);
	}

	public GuiRenderer() {
		this(MultimeterClient.INSTANCE.getFontRenderer());
	}

	private GuiRenderer(FontRenderer font) {
		this.font = font;
		this.itemRenderer = MultimeterClient.MINECRAFT.getItemRenderer();
	}

	public void pushMatrix() {
		GlStateManager.pushMatrix();;
	}

	public void translate(double dx, double dy, double dz) {
		GlStateManager.translated(dx, dy, dz);
	}

	public void popMatrix() {
		GlStateManager.popMatrix();
	}

	public void fill(int x0, int y0, int x1, int y1, int color) {
		GlStateManager.enableDepthTest();

		this.innerFill(x0, y0, x1, y1, color);
	}

	public void gradient(int x0, int y0, int x1, int y1, int color0, int color1) {
		GlStateManager.enableDepthTest();

		this.innerFill(x0, y0, x1, y1, color0, color1);
	}

	public void highlight(int x0, int y0, int x1, int y1, int color) {
		GlStateManager.enableColorLogicOp();
		GlStateManager.logicOp(GlStateManager.LogicOp.OR_REVERSE);
		GlStateManager.disableDepthTest();

		this.innerFill(x0, y0, x1, y1, color);

		GlStateManager.disableColorLogicOp();
	}

	private void innerFill(int x0, int y0, int x1, int y1, int color) {
		this.innerFill(x0, y0, x1, y1, color, color);
	}

	private void innerFill(int x0, int y0, int x1, int y1, int color0, int color1) {
		int a0 = ColorUtils.getAlpha(color0);
		int r0 = ColorUtils.getRed(color0);
		int g0 = ColorUtils.getGreen(color0);
		int b0 = ColorUtils.getBlue(color0);

		int a1 = ColorUtils.getAlpha(color1);
		int r1 = ColorUtils.getRed(color1);
		int g1 = ColorUtils.getGreen(color1);
		int b1 = ColorUtils.getBlue(color1);

		GlStateManager.enableBlend();
		GlStateManager.disableTexture();
		GlStateManager.blendFuncSeparate(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA, SourceFactor.ONE, DestFactor.ZERO);
		GlStateManager.shadeModel(GL11.GL_SMOOTH);

		Tessellator tesselator = Tessellator.getInstance();
		BufferBuilder builder = tesselator.getBuilder();

		builder.begin(GL11.GL_QUADS, DefaultVertexFormat.POSITION_COLOR);
		builder.vertex(x0, y0, 0.0F).color(r0, g0, b0, a0).nextVertex();
		builder.vertex(x0, y1, 0.0F).color(r1, g1, b1, a1).nextVertex();
		builder.vertex(x1, y1, 0.0F).color(r1, g1, b1, a1).nextVertex();
		builder.vertex(x1, y0, 0.0F).color(r0, g0, b0, a0).nextVertex();

		tesselator.end();

		GlStateManager.enableTexture();
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
		Window window = new Window(MultimeterClient.MINECRAFT);

		int width = this.font.width(tooltip) + 8;
		int height = this.font.height(tooltip) + 8;

		int x = mouseX + 15;
		int y = mouseY;

		if (x + width > window.getWidth()) {
			x = mouseX - 15 - width;
		}
		if (y + height > window.getHeight()) {
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
		this.innerBlit(
			t,
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
		this.blit(t.texture, x0, y0, x1, y1, t.x, t.y, t.width, t.height);
	}

	public void blit(TextureRegion t, int x0, int y0, int x1, int y1, int color) {
		this.blit(t.texture, x0, y0, x1, y1, t.x, t.y, t.width, t.height, color);
	}

	private void innerBlit(Texture t, int x0, int x1, int y0, int y1, float u0, float u1, float v0, float v1, int color) {
		MultimeterClient.MINECRAFT.getTextureManager().bind(t.location);
		GlStateManager.enableTexture();
		GlStateManager.enableDepthTest();

		int r = ColorUtils.getRed(color);
		int g = ColorUtils.getGreen(color);
		int b = ColorUtils.getBlue(color);
		int a = ColorUtils.getAlpha(color);

		Tessellator tesselator = Tessellator.getInstance();
		BufferBuilder builder = tesselator.getBuilder();

		builder.begin(GL11.GL_QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
		builder.vertex(x0, y1, 0).texture(u0, v1).color(r, g, b, a).nextVertex();
		builder.vertex(x1, y1, 0).texture(u1, v1).color(r, g, b, a).nextVertex();
		builder.vertex(x1, y0, 0).texture(u1, v0).color(r, g, b, a).nextVertex();
		builder.vertex(x0, y0, 0).texture(u0, v0).color(r, g, b, a).nextVertex();

		tesselator.end();
	}

	public void pushScissor(int x0, int y0, int x1, int y1) {
		this.applyScissor(this.scissorStack.push(new ScissorBox(x0, y0, x1 - x0, y1 - y0)));
	}

	public void popScissor() {
		this.applyScissor(this.scissorStack.pop());
	}

	private void applyScissor(ScissorBox box) {
		if (box == null) {
			GL11.glDisable(GL11.GL_SCISSOR_TEST);
		} else {
			RenderTarget target = MultimeterClient.MINECRAFT.getRenderTarget();
			Window window = new Window(MultimeterClient.MINECRAFT);

			int windowHeight = target.viewHeight;
			double windowScale = window.getScale();

			int x = (int)(box.x * windowScale);
			int y = (int)(windowHeight - (box.y + box.height) * windowScale);
			int width = (int)(box.width * windowScale);
			int height = (int)(box.height * windowScale);

			GL11.glEnable(GL11.GL_SCISSOR_TEST);
			GL11.glScissor(x, y, Math.max(0, width), Math.max(0, height));
		}
	}

	public void renderItem(ItemStack item, int x, int y) {
		Lighting.turnOnGui();
		GlStateManager.enableTexture();

		this.itemRenderer.renderGuiItem(item, x, y);
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

	private static class ScissorStack {

		private final Stack<ScissorBox> boxes = new Stack<>();

		public ScissorBox push(ScissorBox box) {
			if (!boxes.isEmpty()) {
				box = boxes.peek().intersection(box);
			}

			return boxes.push(box);
		}

		public ScissorBox pop() {
			if (boxes.isEmpty()) {
				throw new IllegalStateException("popping empty scissor stack");
			} else{
				boxes.pop();
				return boxes.isEmpty() ? null : boxes.peek();
			}
		}
	}

	private static class ScissorBox {

		private static final ScissorBox EMPTY = new ScissorBox(0, 0, 0, 0);

		private final int x;
		private final int y;
		private final int width;
		private final int height;

		public ScissorBox(int x, int y, int width, int height) {
			this.x = x;
			this.y = y;
			this.width = width;
			this.height = height;
		}

		public ScissorBox intersection(ScissorBox o) {
			int x0 = Math.max(x, o.x);
			int y0 = Math.max(y, o.y);
			int x1 = Math.min(x + width, o.x + o.width);
			int y1 = Math.min(y + height, o.y + o.height);

			return (x0 == x1 || y0 == y1) ? EMPTY : new ScissorBox(x0, y0, x1 - x0, y1 - y0);
		}
	}
}
