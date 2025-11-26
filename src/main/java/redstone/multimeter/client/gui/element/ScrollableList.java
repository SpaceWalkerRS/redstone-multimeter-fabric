package redstone.multimeter.client.gui.element;

import redstone.multimeter.client.gui.GuiRenderer;
import redstone.multimeter.client.gui.element.input.MouseEvent;
import redstone.multimeter.client.option.Options;

public class ScrollableList extends SimpleList {

	private int height;
	private int scrollBarX;
	private int scrollBarY;
	private int scrollBarWidth;
	private int scrollBarHeight;
	private double scrollAmount;
	private ScrollMode scrollMode;

	public ScrollableList(int width, int height) {
		this(width, height, 0, 0);
	}

	public ScrollableList(int width, int height, int topBorder, int bottomBorder) {
		super(width, topBorder, bottomBorder);

		this.height = height;
		this.scrollBarWidth = 6;
		this.scrollMode = ScrollMode.NONE;

		this.updateScrollBar();
	}

	@Override
	protected void renderList(GuiRenderer renderer, int mouseX, int mouseY, boolean mainPass) {
		super.renderList(renderer, mouseX, mouseY, mainPass);

		if (mainPass && this.getMaxScrollAmount() > 0.0D) {
			if (this.scrollMode == ScrollMode.PULL) {
				int visibleHeight = this.getHeight();
				int totalHeight = visibleHeight + (int)this.getMaxScrollAmount();

				int middle = this.scrollBarY + this.scrollBarHeight * ((int)this.scrollAmount + visibleHeight / 2) / totalHeight;
				int margin = 5;

				if (mouseY < (middle - margin)) {
					this.scroll(-Options.Miscellaneous.SCROLL_SPEED.get());
				} else if (mouseY > (middle + margin)) {
					this.scroll(Options.Miscellaneous.SCROLL_SPEED.get());
				}
			}

			this.renderScrollBar(renderer, this.scrollMode == ScrollMode.DRAG || (this.isHovered() && this.isMouseOverScrollBar(mouseX, mouseY)));
		}
	}

	private boolean isMouseOverScrollBar(double mouseX, double mouseY) {
		return mouseX >= this.scrollBarX && mouseX <= this.scrollBarX + this.scrollBarWidth && mouseY >= this.scrollBarY && mouseY <= this.scrollBarY + this.scrollBarHeight;
	}

	@Override
	public boolean mouseClick(MouseEvent.Click event) {
		boolean consumed = super.mouseClick(event);

		if (!consumed && event.isLeftButton()) {
			this.scrollMode = this.getScrollMode(event.mouseX(), event.mouseY());

			if (this.scrollMode != ScrollMode.NONE) {
				consumed = true;
			}
		}

		return consumed;
	}

	@Override
	public boolean mouseRelease(MouseEvent.Release event) {
		boolean consumed = super.mouseRelease(event);

		if (event.isLeftButton()) {
			this.scrollMode = ScrollMode.NONE;
		}

		return consumed;
	}

	@Override
	public boolean mouseDrag(MouseEvent.Drag event) {
		boolean consumed = super.mouseDrag(event);

		if (!consumed && this.scrollMode == ScrollMode.DRAG) {
			consumed = this.scroll(event.deltaY() * (this.getMaxScrollAmount() + this.getHeight()) / this.scrollBarHeight);
		}

		return consumed;
	}

	@Override
	public boolean mouseScroll(MouseEvent.Scroll event) {
		boolean consumed = super.mouseScroll(event);

		if (!consumed && this.scrollMode == ScrollMode.NONE) {
			consumed = this.scroll(-Options.Miscellaneous.SCROLL_SPEED.get() * event.scrollY());
		}

		return consumed;
	}

	@Override
	public void setX(int x) {
		super.setX(x);
		this.scrollBarX = (x + this.getWidth()) - (this.scrollBarWidth + 2);
	}

	@Override
	public void setY(int y) {
		super.setY(y);
		this.updateScrollBar();
	}

	@Override
	public int getHeight() {
		return this.height - this.getTopBorderAndMargin() - this.getBottomBorderAndMargin();
	}

	@Override
	public int getEffectiveWidth() {
		return (this.scrollBarX - 2) - this.getX();
	}

	@Override
	protected void updateContentY() {
		super.updateContentY();
		this.updateScrollBar();
		this.validateScrollAmount();
	}

	@Override
	protected int getOffsetY() {
		return -(int)this.scrollAmount;
	}

	@Override
	public void setDrawBackground(boolean drawBackground) {
		super.setDrawBackground(drawBackground);
		this.updateScrollBar();
	}

	protected double getMaxScrollAmount() {
		double amount = this.getTotalSpacing() - this.getHeight();

		for (Element element : this.getChildren()) {
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
		return this.setScrollAmount(this.scrollAmount + amount);
	}

	protected boolean setScrollAmount(double amount) {
		double prevScroll = this.scrollAmount;
		this.scrollAmount = amount;

		if (this.scrollAmount < 0.0D) {
			this.scrollAmount = 0.0D;
		}

		double maxAmount = this.getMaxScrollAmount();

		if (this.scrollAmount > maxAmount) {
			this.scrollAmount = maxAmount;
		}

		if (this.scrollAmount != prevScroll) {
			this.updateContentY();
		}

		return this.scrollAmount != prevScroll;
	}

	protected boolean validateScrollAmount() {
		return this.setScrollAmount(this.scrollAmount);
	}

	protected ScrollMode getScrollMode(double mouseX, double mouseY) {
		int left = this.scrollBarX;
		int right = this.scrollBarX + this.scrollBarWidth;
		int top = this.scrollBarY;
		int bot = this.scrollBarY + this.scrollBarHeight;

		if (mouseX < left || mouseX > right || mouseY < top || mouseY > bot) {
			return ScrollMode.NONE;
		}

		int screenHeight = this.getHeight();
		int totalHeight = screenHeight + (int)this.getMaxScrollAmount();

		int barTop = this.scrollBarY + this.scrollBarHeight * (int)this.scrollAmount / totalHeight;
		int barBot = this.scrollBarY + this.scrollBarHeight * ((int)this.scrollAmount + this.getHeight()) / totalHeight;

		if (mouseY >= barTop && mouseY <= barBot) {
			return ScrollMode.DRAG;
		}

		return ScrollMode.PULL;
	}

	private void updateScrollBar() {
		this.scrollBarY = getY() + this.getTopBorder() + 3;
		this.scrollBarHeight = this.getHeight() - 6;

		if (this.shouldDrawBackground()) {
			this.scrollBarHeight += (BORDER_MARGIN_TOP + BORDER_MARGIN_BOTTOM);
		} else {
			this.scrollBarY += BORDER_MARGIN_TOP;
		}
	}

	protected void renderScrollBar(GuiRenderer renderer, boolean light) {
		renderer.fill(this.scrollBarX, this.scrollBarY, this.scrollBarX + this.scrollBarWidth, this.scrollBarY + this.scrollBarHeight, 0xFF000000); // background

		int visibleHeight = this.getHeight();
		int totalHeight = visibleHeight + (int)this.getMaxScrollAmount();

		int x = this.scrollBarX;
		int y = this.scrollBarY + (int)Math.round(this.scrollBarHeight * this.scrollAmount / totalHeight);
		int width = this.scrollBarWidth;
		int height = Math.round((float)this.scrollBarHeight * visibleHeight / totalHeight);

		int color0 = light ? 0xFF777777 : 0xFF555555;
		int color1 = light ? 0xFFBBBBBB : 0xFF999999;

		renderer.fill(x, y, x + width, y + height, color0);
		renderer.fill(x, y, x + width - 1, y + height - 1, color1);
	}

	protected enum ScrollMode {
		NONE, DRAG, PULL
	}
}
