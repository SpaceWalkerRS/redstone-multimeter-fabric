package redstone.multimeter.client.gui;

import java.util.Stack;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Matrix4f;

import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.world.item.ItemStack;

import redstone.multimeter.client.MultimeterClient;
import redstone.multimeter.client.gui.text.Text;
import redstone.multimeter.client.gui.texture.Texture;
import redstone.multimeter.client.gui.texture.TextureRegion;
import redstone.multimeter.client.gui.tooltip.Tooltip;

public class GuiRenderer extends GuiComponent {

	final PoseStack poses;
	final FontRenderer font;
	private final ItemRenderer itemRenderer;
	private final ScissorStack scissorStack = new ScissorStack();

	public GuiRenderer(GuiRenderer delegate) {
		this(delegate.poses, delegate.font);
	}

	public GuiRenderer(PoseStack poses) {
		this(poses, MultimeterClient.INSTANCE.getFontRenderer());
	}

	private GuiRenderer(PoseStack poses, FontRenderer font) {
		this.poses = poses;
		this.font = font;
		this.itemRenderer = MultimeterClient.MINECRAFT.getItemRenderer();

		this.font.poses = this.poses;
	}

	public void pushMatrix() {
		this.poses.pushPose();
	}

	public void translate(double dx, double dy, double dz) {
		this.poses.translate(dx, dy, dz);
	}

	public void popMatrix() {
		this.poses.popPose();
	}

	public void fill(int x0, int y0, int x1, int y1, int color) {
		RenderSystem.enableDepthTest();

		GuiComponent.fill(this.poses, x0, y0, x1, y1, color);
	}

	public void gradient(int x0, int y0, int x1, int y1, int color0, int color1) {
		RenderSystem.enableDepthTest();

		GuiComponent.fillGradient(this.poses, x0, y0, x1, y1, color0, color1, 0);
	}

	public void highlight(int x0, int y0, int x1, int y1, int color) {
		RenderSystem.enableColorLogicOp();
		RenderSystem.logicOp(GlStateManager.LogicOp.OR_REVERSE);
		RenderSystem.disableDepthTest();

		GuiComponent.fill(this.poses, x0, y0, x1, y1, color);

		RenderSystem.disableColorLogicOp();
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

		int windowWidth = window.getGuiScaledWidth();
		int windowHeight = window.getGuiScaledHeight();
		int tooltipWidth = this.font.width(tooltip) + 8;
		int tooltipHeight = this.font.height(tooltip) + 8;

		int x = mouseX + 15;
		int y = mouseY;

		if (x + tooltipWidth > windowWidth) {
			x = Math.max(0, windowWidth - tooltipWidth);
			y += 15;
		}
		if (y + tooltipHeight > windowHeight) {
			y = Math.max(0, windowHeight - tooltipHeight);
		}

		this.tooltip(tooltip, x, y, x + tooltipWidth, y + tooltipHeight);
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

	private void innerBlit(Texture t, int x0, int x1, int y0, int y1, float u0, float u1, float v0, float v1, int color) {
		RenderSystem.setShader(GameRenderer::getPositionColorTexShader);
		RenderSystem.setShaderTexture(0, t.location);
		RenderSystem.enableDepthTest();

		Matrix4f pose = this.poses.last().pose();

		Tesselator tesselator = Tesselator.getInstance();
		BufferBuilder builder = tesselator.getBuilder();

		builder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR_TEX);
		builder.vertex(pose, x0, y1, 0).color(color).uv(u0, v1).endVertex();
		builder.vertex(pose, x1, y1, 0).color(color).uv(u1, v1).endVertex();
		builder.vertex(pose, x1, y0, 0).color(color).uv(u1, v0).endVertex();
		builder.vertex(pose, x0, y0, 0).color(color).uv(u0, v0).endVertex();

		BufferUploader.drawWithShader(builder.end());
	}

	public void pushScissor(int x0, int y0, int x1, int y1) {
		this.applyScissor(this.scissorStack.push(new ScissorBox(x0, y0, x1 - x0, y1 - y0)));
	}

	public void popScissor() {
		this.applyScissor(this.scissorStack.pop());
	}

	private void applyScissor(ScissorBox box) {
		if (box == null) {
			RenderSystem.disableScissor();
		} else {
			Window window = MultimeterClient.MINECRAFT.getWindow();

			int windowHeight = window.getHeight();
			double windowScale = window.getGuiScale();

			int x = (int)(box.x * windowScale);
			int y = (int)(windowHeight - (box.y + box.height) * windowScale);
			int width = (int)(box.width * windowScale);
			int height = (int)(box.height * windowScale);

			RenderSystem.enableScissor(x, y, Math.max(0, width), Math.max(0, height));
		}
	}

	public void renderItem(ItemStack item, int x, int y) {
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
