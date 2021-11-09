package redstone.multimeter.client.gui.element;

import org.lwjgl.glfw.GLFW;

import redstone.multimeter.client.MultimeterClient;
import redstone.multimeter.client.option.Options;

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
	public void render(int mouseX, int mouseY) {
		super.render(mouseX, mouseY);
		
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
			
			renderScrollBar(isHovered(mouseX, mouseY));
		}
	}
	
	@Override
	public boolean mouseClick(double mouseX, double mouseY, int button) {
		boolean consumed = super.mouseClick(mouseX, mouseY, button);
		
		if (!consumed && button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
			scrollMode = getScrollMode(mouseX, mouseY);
			
			if (scrollMode != ScrollMode.NONE) {
				consumed = true;
			}
		}
		
		return consumed;
	}
	
	@Override
	public boolean mouseRelease(double mouseX, double mouseY, int button) {
		boolean consumed = super.mouseRelease(mouseX, mouseY, button);
		
		if (button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
			scrollMode = ScrollMode.NONE;
		}
		
		return consumed;
	}
	
	@Override
	public boolean mouseDrag(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
		boolean consumed = super.mouseDrag(mouseX, mouseY, button, deltaX, deltaY);
		
		if (!consumed && scrollMode == ScrollMode.DRAG) {
			double scroll = deltaY * (getMaxScrollAmount() + getHeight()) / scrollBarHeight;
			setScrollAmount(scrollAmount + scroll);
		}
		
		return consumed;
	}
	
	@Override
	public boolean mouseScroll(double mouseX, double mouseY, double scrollX, double scrollY) {
		boolean consumed = super.mouseScroll(mouseX, mouseY, scrollX, scrollY);
		
		if (!consumed && scrollMode == ScrollMode.NONE) {
			setScrollAmount(scrollAmount - Options.Miscellaneous.SCROLL_SPEED.get() * scrollY);
			consumed = true;
		}
		
		return consumed;
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
		scrollBarY = getY() + topBorder + 3;
		scrollBarHeight = height - 6;
		
		if (isDrawingBackground()) {
			scrollBarHeight += (BORDER_MARGIN_TOP + BORDER_MARGIN_BOTTOM);
		} else {
			scrollBarY += BORDER_MARGIN_TOP;
		}
	}
	
	protected void renderScrollBar(boolean dark) {
		renderRect(scrollBarX, scrollBarY, scrollBarWidth, scrollBarHeight, 0xFF000000); // background
		
		int visibleHeight = getHeight();
		int totalHeight = visibleHeight + (int)getMaxScrollAmount();
		
		int x = scrollBarX;
		int y = scrollBarY + (int)Math.round(scrollBarHeight * scrollAmount / totalHeight);
		int width = scrollBarWidth;
		int height = Math.round((float)scrollBarHeight * visibleHeight / totalHeight);
		
		int color0 = dark ? 0xFF555555 : 0xFF777777;
		int color1 = dark ? 0xFF999999 : 0xFFBBBBBB;
		
		renderRect(x, y, width    , height    , color0);
		renderRect(x, y, width - 1, height - 1, color1);
	}
	
	protected enum ScrollMode {
		NONE, DRAG, PULL
	}
}
