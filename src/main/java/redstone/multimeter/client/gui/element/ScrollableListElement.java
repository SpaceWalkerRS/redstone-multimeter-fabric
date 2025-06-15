package redstone.multimeter.client.gui.element;

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

		this.height = height;
		this.scrollBarWidth = 6;
		this.scrollMode = ScrollMode.NONE;

		updateScrollBar();
	}

	@Override
	protected void renderList(int mouseX, int mouseY) {
		super.renderList(mouseX, mouseY);

		if (getMaxScrollAmount() > 0.0D) {
			if (scrollMode == ScrollMode.PULL) {
				int visibleHeight = getHeight();
				int totalHeight = visibleHeight + (int)getMaxScrollAmount();

				int middle = scrollBarY + scrollBarHeight * ((int)scrollAmount + visibleHeight / 2) / totalHeight;
				int margin = 5;

				if (mouseY < (middle - margin)) {
					scroll(-Options.Miscellaneous.SCROLL_SPEED.get());
				} else if (mouseY > (middle + margin)) {
					scroll(Options.Miscellaneous.SCROLL_SPEED.get());
				}
			}

			renderScrollBar(scrollMode == ScrollMode.DRAG || (isHovered() && isMouseOverScrollBar(mouseX, mouseY)));
		}
	}

	private boolean isMouseOverScrollBar(double mouseX, double mouseY) {
		return mouseX >= scrollBarX && mouseX <= scrollBarX + scrollBarWidth && mouseY >= scrollBarY && mouseY <= scrollBarY + scrollBarHeight;
	}

	@Override
	public boolean mouseClick(double mouseX, double mouseY, int button) {
		boolean consumed = super.mouseClick(mouseX, mouseY, button);

		if (!consumed && button == MOUSE_BUTTON_LEFT) {
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

		if (button == MOUSE_BUTTON_LEFT) {
			scrollMode = ScrollMode.NONE;
		}

		return consumed;
	}

	@Override
	public boolean mouseDrag(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
		boolean consumed = super.mouseDrag(mouseX, mouseY, button, deltaX, deltaY);

		if (!consumed && scrollMode == ScrollMode.DRAG) {
			consumed = scroll(deltaY * (getMaxScrollAmount() + getHeight()) / scrollBarHeight);
		}

		return consumed;
	}

	@Override
	public boolean mouseScroll(double mouseX, double mouseY, double scrollX, double scrollY) {
		boolean consumed = super.mouseScroll(mouseX, mouseY, scrollX, scrollY);

		if (!consumed && scrollMode == ScrollMode.NONE) {
			consumed = scroll(-Options.Miscellaneous.SCROLL_SPEED.get() * scrollY);
		}

		return consumed;
	}

	@Override
	public void setX(int x) {
		super.setX(x);
		scrollBarX = (x + getWidth()) - (scrollBarWidth + 2);
	}

	@Override
	public void setY(int y) {
		super.setY(y);
		updateScrollBar();
	}

	@Override
	public int getHeight() {
		return height - getTopBorderAndMargin() - getBottomBorderAndMargin();
	}

	@Override
	public int getEffectiveWidth() {
		return (scrollBarX - 2) - getX();
	}

	@Override
	protected void updateContentY() {
		super.updateContentY();
		updateScrollBar();
		validateScrollAmount();
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

	protected double getMaxScrollAmount() {
		double amount = getTotalSpacing() - getHeight();

		for (Element element : getChildren()) {
			if (element.isVisible()) {
				amount += element.getHeight();
			}
		}
		if (amount < 0.0D) {
			amount = 0.0D;
		}

		return amount;
	}

	protected boolean scroll(double amount) {
		return setScrollAmount(scrollAmount + amount);
	}

	protected boolean setScrollAmount(double amount) {
		double prevScroll = scrollAmount;
		scrollAmount = amount;

		if (scrollAmount < 0.0D) {
			scrollAmount = 0.0D;
		}

		double maxAmount = getMaxScrollAmount();

		if (scrollAmount > maxAmount) {
			scrollAmount = maxAmount;
		}

		if (scrollAmount != prevScroll) {
			updateContentY();
		}

		return scrollAmount != prevScroll;
	}

	protected boolean validateScrollAmount() {
		return setScrollAmount(scrollAmount);
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
		scrollBarY = getY() + getTopBorder() + 3;
		scrollBarHeight = getHeight() - 6;

		if (shouldDrawBackground()) {
			scrollBarHeight += (BORDER_MARGIN_TOP + BORDER_MARGIN_BOTTOM);
		} else {
			scrollBarY += BORDER_MARGIN_TOP;
		}
	}

	protected void renderScrollBar(boolean light) {
		renderRect(scrollBarX, scrollBarY, scrollBarWidth, scrollBarHeight, 0xFF000000); // background

		int visibleHeight = getHeight();
		int totalHeight = visibleHeight + (int)getMaxScrollAmount();

		int x = scrollBarX;
		int y = scrollBarY + (int)Math.round(scrollBarHeight * scrollAmount / totalHeight);
		int width = scrollBarWidth;
		int height = Math.round((float)scrollBarHeight * visibleHeight / totalHeight);

		int color0 = light ? 0xFF777777 : 0xFF555555;
		int color1 = light ? 0xFFBBBBBB : 0xFF999999;

		renderRect(x, y, width, height, color0);
		renderRect(x, y, width - 1, height - 1, color1);
	}

	protected enum ScrollMode {
		NONE, DRAG, PULL
	}
}
