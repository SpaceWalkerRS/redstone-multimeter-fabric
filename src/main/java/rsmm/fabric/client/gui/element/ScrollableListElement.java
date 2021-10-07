package rsmm.fabric.client.gui.element;

import org.lwjgl.glfw.GLFW;

import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;

import rsmm.fabric.client.MultimeterClient;
import rsmm.fabric.client.option.Options;

public class ScrollableListElement extends SimpleListElement {
	
	private int height;
	private int scrollBarX;
	private int scrollBarY;
	private int scrollBarWidth;
	private int scrollBarHeight;
	private double scrollAmount;
	private ScrollMode scrollMode;
	
	public ScrollableListElement(MultimeterClient client, int width, int height) {
		this(client, width, height, 0, 0);
	}
	
	public ScrollableListElement(MultimeterClient client, int width, int height, int topBorder, int bottomBorder) {
		super(client, width, topBorder, bottomBorder);
		
		this.height = height - (topBorder + bottomBorder);
		this.scrollBarWidth = 6;
		this.scrollBarX = (getX() + getWidth()) - (scrollBarWidth + 2);
		this.scrollMode = ScrollMode.NONE;
		
		updateScrollBar();
	}
	
	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		super.render(matrices, mouseX, mouseY, delta);
		
		if (getMaxScrollAmount() > 0.0D) {
			if (scrollMode == ScrollMode.PULL) {
				int screenHeight = getHeight();
				int totalHeight = screenHeight + (int)getMaxScrollAmount();
				
				int middle = scrollBarY + scrollBarHeight * ((int)scrollAmount + screenHeight / 2) / totalHeight;
				int margin = 5;
				
				if (mouseY < (middle - margin)) {
					setScrollAmount(scrollAmount - Options.Miscellaneous.SCROLL_SPEED.get());
				} else if (mouseY > (middle + margin)) {
					setScrollAmount(scrollAmount + Options.Miscellaneous.SCROLL_SPEED.get());
				}
			}
			
			renderScrollBar(matrices, isHovered(mouseX, mouseY));
		}
	}
	
	@Override
	public boolean mouseClick(double mouseX, double mouseY, int button) {
		boolean success = super.mouseClick(mouseX, mouseY, button);
		
		if (!success && button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
			scrollMode = getScrollMode(mouseX, mouseY);
			
			if (scrollMode != ScrollMode.NONE) {
				success = true;
			}
		}
		
		return success;
	}
	
	@Override
	public boolean mouseRelease(double mouseX, double mouseY, int button) {
		boolean success = super.mouseRelease(mouseX, mouseY, button);
		
		if (button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
			scrollMode = ScrollMode.NONE;
		}
		
		return success;
	}
	
	@Override
	public boolean mouseDrag(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
		boolean success = super.mouseDrag(mouseX, mouseY, button, deltaX, deltaY);
		
		if (!success && scrollMode == ScrollMode.DRAG) {
			double scroll = deltaY * (getMaxScrollAmount() + getHeight()) / scrollBarHeight;
			setScrollAmount(scrollAmount + scroll);
		}
		
		return success;
	}
	
	@Override
	public boolean mouseScroll(double mouseX, double mouseY, double amount) {
		boolean success = super.mouseScroll(mouseX, mouseY, amount);
		
		if (!success && scrollMode == ScrollMode.NONE) {
			setScrollAmount(scrollAmount - Options.Miscellaneous.SCROLL_SPEED.get() * amount);
			success = true;
		}
		
		return success;
	}
	
	@Override
	public int getHeight() {
		return height;
	}
	
	@Override
	public void onChangedX(int x) {
		super.onChangedX(x);
		scrollBarX = (x + getWidth()) - (scrollBarWidth + 2);
	}
	
	@Override
	public void onChangedY(int y) {
		super.onChangedY(y);
		updateScrollBar();
	}
	
	@Override
	protected int getOffsetY() {
		return -(int)scrollAmount;
	}
	
	@Override
	public void setDrawBackground(boolean drawBackground) {
		super.setDrawBackground(drawBackground);
		updateScrollBar();
	}
	
	public int getEffectiveWidth() {
		return scrollBarX - getX();
	}
	
	protected double getMaxScrollAmount() {
		double amount = getTotalSpacing() - getHeight();
		
		for (IElement element : getChildren()) {
			amount += element.getHeight();
		}
		if (amount < 0.0D) {
			amount = 0.0D;
		}
		
		return amount;
	}
	
	protected void setScrollAmount(double amount) {
		double prevScrollAmount = scrollAmount;
		scrollAmount = amount;
		
		if (scrollAmount < 0.0D) {
			scrollAmount = 0.0D;
		}
		
		double maxAmount = getMaxScrollAmount();
		
		if (scrollAmount > maxAmount) {
			scrollAmount = maxAmount;
		}
		
		if (scrollAmount != prevScrollAmount) {
			updateContentY();
		}
	}
	
	protected ScrollMode getScrollMode(double mouseX, double mouseY) {
		int left = scrollBarX;
		int right = scrollBarX + scrollBarWidth;
		int top = scrollBarY;
		int bot = scrollBarY + scrollBarHeight;
		
		if (mouseX < left || mouseX > right || mouseY < top || mouseY > bot) {
			return ScrollMode.NONE;
		}
		
		int screenHeight = getHeight();
		int totalHeight = screenHeight + (int)getMaxScrollAmount();
		
		int barTop = scrollBarY + scrollBarHeight * (int)scrollAmount / totalHeight;
		int barBot = scrollBarY + scrollBarHeight * ((int)scrollAmount + getHeight()) / totalHeight;
		
		if (mouseY >= barTop && mouseY <= barBot) {
			return ScrollMode.DRAG;
		}
		
		return ScrollMode.PULL;
	}
	
	private void updateScrollBar() {
		scrollBarY = getY() + topBorder + 4;
		scrollBarHeight = height - 8;
		
		if (isDrawingBackground()) {
			scrollBarHeight += (BORDER_MARGIN_TOP + BORDER_MARGIN_BOTTOM);
		} else {
			scrollBarY += BORDER_MARGIN_TOP;
		}
	}
	
	protected void renderScrollBar(MatrixStack matrices, boolean dark) {
		RenderSystem.disableTexture();
		RenderSystem.setShader(() -> GameRenderer.getPositionColorShader());
		
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBuffer();
		
		int screenHeight = getHeight();
		int totalHeight = screenHeight + (int)getMaxScrollAmount();
		
		int bgLeft = scrollBarX;
		int bgRight = scrollBarX + scrollBarWidth;
		int bgTop = scrollBarY;
		int bgBot = scrollBarY + scrollBarHeight;
		
		int barLeft = bgLeft;
		int barRight = bgRight;
		int barTop = scrollBarY + scrollBarHeight * (int)scrollAmount / totalHeight;
		int barBot = scrollBarY + scrollBarHeight * ((int)scrollAmount + getHeight()) / totalHeight;
		
		int z = 0;
		
		int color1 = dark ? 0x55 : 0x77;
		int color2 = dark ? 0x99 : 0xBB;
		
		bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
		
		bufferBuilder.vertex(bgLeft , bgBot, z).color(0, 0, 0, 255).next();
		bufferBuilder.vertex(bgRight, bgBot, z).color(0, 0, 0, 255).next();
		bufferBuilder.vertex(bgRight, bgTop, z).color(0, 0, 0, 255).next();
		bufferBuilder.vertex(bgLeft , bgTop, z).color(0, 0, 0, 255).next();
		
		bufferBuilder.vertex(barLeft     , barBot    , z).color(color1, color1, color1, 255).next();
		bufferBuilder.vertex(barRight    , barBot    , z).color(color1, color1, color1, 255).next();
		bufferBuilder.vertex(barRight    , barTop    , z).color(color1, color1, color1, 255).next();
		bufferBuilder.vertex(barLeft     , barTop    , z).color(color1, color1, color1, 255).next();
		bufferBuilder.vertex(barLeft     , barBot - 1, z).color(color2, color2, color2, 255).next();
		bufferBuilder.vertex(barRight - 1, barBot - 1, z).color(color2, color2, color2, 255).next();
		bufferBuilder.vertex(barRight - 1, barTop    , z).color(color2, color2, color2, 255).next();
		bufferBuilder.vertex(barLeft     , barTop    , z).color(color2, color2, color2, 255).next();
		
		tessellator.draw();
		
		RenderSystem.enableTexture();
	}
	
	protected enum ScrollMode {
		NONE, DRAG, PULL
	}
}
