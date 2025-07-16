package redstone.multimeter.client.gui.element;

import java.text.Collator;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.function.Predicate;

import redstone.multimeter.client.MultimeterClient;
import redstone.multimeter.client.gui.FontRenderer;
import redstone.multimeter.client.gui.GuiRenderer;
import redstone.multimeter.client.gui.screen.RSMMScreen;
import redstone.multimeter.client.gui.texture.Textures;

public class SimpleList extends AbstractParentElement {

	protected static final int BORDER_MARGIN_TOP = 6;
	protected static final int BORDER_MARGIN_BOTTOM = 3;
	protected static final Collator COLLATOR = Collator.getInstance();

	protected final FontRenderer font;

	private int topBorder;
	private int bottomBorder;
	private int spacing;
	private int height;
	private int minY;
	private int maxY;
	private boolean drawBackground;

	private Comparator<Element> sorter;
	private Predicate<Element> filter;

	private boolean shouldUpdateHovered;

	public SimpleList(int width) {
		this(width, 0, 0);
	}

	public SimpleList(int width, int topBorder, int bottomBorder) {
		this.font = MultimeterClient.INSTANCE.getFontRenderer();

		this.setSpacing(2);
		this.setTopBorder(topBorder);
		this.setBottomBorder(bottomBorder);

		this.sorter = null;
		this.filter = e -> true;

		this.setWidth(width);
	}

	@Override
	public void render(GuiRenderer renderer, int mouseX, int mouseY) {
		if (this.shouldUpdateHovered) {
			this.mouseMove(mouseX, mouseY);
			this.shouldUpdateHovered = false;
		}

		if (this.drawBackground) {
			this.drawBackground(renderer);
		}

		this.renderList(renderer, mouseX, mouseY);

		if (this.drawBackground) {
			this.drawBorders(renderer);
		}
	}

	@Override
	public boolean isMouseOver(double mouseX, double mouseY) {
		// ignore when the mouse is hovering over the borders
		return mouseX >= this.getX() && mouseX <= (this.getX() + this.getWidth()) && mouseY >= this.minY && mouseY <= this.maxY;
	}

	@Override
	public void setX(int x) {
		super.setX(x);
		this.updateContentX();
	}

	@Override
	public void setY(int y) {
		super.setY(y);
		this.updateContentY();
	}

	@Override
	public int getHeight() {
		// return the height minus the borders
		return this.height;
	}

	@Override
	public void update() {
		this.sort();
		this.updateCoords();
		super.update();
	}

	protected void drawBackground(GuiRenderer renderer) {
		int x0 = this.getX();
		int y0 = this.getY() + this.topBorder;
		int x1 = this.getX() + this.getWidth();
		int y1 = this.getY() + getTotalHeight() - this.bottomBorder;

		int offsetY = this.getOffsetY();

		int u0 = x0 / 2;
		int v0 = (y0 - offsetY) / 2;
		int u1 = x1 / 2;
		int v1 = (y1 - offsetY) / 2;

		renderer.blit(Textures.OPTIONS_BACKGROUND, x0, y0, x1, y1, u0, v0, u1, v1, 0xFF202020);
	}

	protected void renderList(GuiRenderer renderer, int mouseX, int mouseY) {
		renderer.pushScissor(this.getX(), this.minY, this.getX() + this.getWidth(), this.maxY);

		List<Element> children = this.getChildren();

		for (int index = 0; index < children.size(); index++) {
			Element element = children.get(index);

			if (element.getY() + element.getHeight() < this.minY) {
				continue;
			}
			if (element.getY() > this.maxY) {
				break;
			}

			if (element.isVisible()) {
				this.renderElement(renderer, element, mouseX, mouseY);
			}
		}

		renderer.popScissor();
	}

	protected void renderElement(GuiRenderer renderer, Element element, int mouseX, int mouseY) {
		element.render(renderer, mouseX, mouseY);
	}

	protected void drawBorders(GuiRenderer renderer) {
		boolean renderTop = this.topBorder > 0;
		boolean renderBottom = this.bottomBorder > 0;

		if (!renderTop && !renderBottom) {
			return;
		}

		int x = this.getX();
		int width = this.getWidth();

		int x0 = x;
		int y0;
		int x1 = x + width;
		int y1;

		int tx0 = x0 / 2;
		int ty0;
		int tx1 = x1 / 2;
		int ty1;

		if (renderTop) {
			y0 = this.minY - this.topBorder;
			y1 = this.minY;

			ty0 = y0 / 2;
			ty1 = y1 / 2;

			renderer.blit(Textures.OPTIONS_BACKGROUND, x0, y0, x1, y1, tx0, ty0, tx1, ty1, 0xFF404040);
			renderer.gradient(x0, y1, x1, y1 + 4, 0xFF000000, 0x00000000);
		}
		if (renderBottom) {
			y0 = this.maxY;
			y1 = this.maxY + this.bottomBorder;

			ty0 = y0 / 2;
			ty1 = y1 / 2;

			renderer.blit(Textures.OPTIONS_BACKGROUND, x0, y0, x1, y1, tx0, ty0, tx1, ty1, 0xFF404040);
			renderer.gradient(x0, y0 - 4, x1, y0, 0x00000000, 0xFF000000);
		}
	}

	public int getTopBorder() {
		return this.topBorder;
	}

	public int getTopBorderAndMargin() {
		return this.topBorder + BORDER_MARGIN_TOP;
	}

	public int getBottomBorder() {
		return this.bottomBorder;
	}

	public int getBottomBorderAndMargin() {
		return this.bottomBorder + BORDER_MARGIN_BOTTOM;
	}

	public int getSpacing() {
		return this.spacing;
	}

	public int getTotalHeight() {
		return this.getHeight() + this.getTopBorderAndMargin() + this.getBottomBorderAndMargin();
	}

	public int getTotalSpacing() {
		return this.spacing * (this.getChildren().size() - 1);
	}

	protected int getAmountOffScreen(Element element) {
		int top = element.getY();

		if (top < this.minY) {
			return top - this.minY - 1;
		}

		int bottom = top + element.getHeight();

		if (bottom > this.maxY) {
			return bottom - this.maxY + 1;
		}

		return 0;
	}

	public int getEffectiveWidth() {
		return this.getWidth();
	}

	private void sort() {
		if (this.sorter == null) {
			return;
		}

		List<Element> children = this.getChildren();
		Queue<Element> queue = new PriorityQueue<>(this.sorter);

		for (int index = 0; index < children.size(); index++) {
			queue.add(children.get(index));
		}

		children.clear();

		while (!queue.isEmpty()) {
			children.add(queue.poll());
		}
	}

	protected void updateContentX() {
		List<Element> children = this.getChildren();
		int x = this.getX();

		for (int index = 0; index < children.size(); index++) {
			children.get(index).setX(x);
		}
	}

	protected void updateContentY() {
		List<Element> children = this.getChildren();
		int yStart = this.getY() + this.getTopBorderAndMargin() + this.getOffsetY();
		int y = yStart;

		for (int index = 0; index < children.size(); index++) {
			Element element = children.get(index);

			element.setY(y);
			element.setVisible(this.filter.test(element));

			if (element.isVisible()) {
				y += element.getHeight() + this.spacing;
			}
		}

		RSMMScreen screen = MultimeterClient.INSTANCE.getScreen();

		this.height = (y - this.spacing) - yStart;
		this.minY = Math.max(this.getY() + this.topBorder, screen.getY());
		this.maxY = Math.min(this.getY() + this.getTotalHeight() - this.bottomBorder, screen.getY() + screen.getHeight());

		this.shouldUpdateHovered = true;
	}

	protected int getOffsetY() {
		return 0;
	}

	public boolean shouldDrawBackground() {
		return this.drawBackground;
	}

	public void setDrawBackground(boolean drawBackground) {
		this.drawBackground = drawBackground;
	}

	public void add(Element element) {
		this.addChild(element);
	}

	public void addAll(Collection<? extends Element> elements) {
		for (Element element : elements) {
			this.addChild(element);
		}
	}

	public void clear() {
		this.removeChildren();
		this.updateCoords();
	}

	public void updateCoords() {
		this.updateContentX();
		this.updateContentY();
	}

	public void setTopBorder(int topBorder) {
		if (topBorder < 0) {
			topBorder = 0;
		}

		this.topBorder = topBorder - BORDER_MARGIN_TOP;
	}

	public void setBottomBorder(int bottomBorder) {
		if (bottomBorder < 0) {
			bottomBorder = 0;
		}

		this.bottomBorder = bottomBorder - BORDER_MARGIN_BOTTOM;
	}

	public void setSpacing(int spacing) {
		if (spacing < 0) {
			spacing = 0;
		}

		this.spacing = spacing;
	}

	public void setSorter(Comparator<Element> sorter) {
		this.sorter = sorter;
	}

	public void setFilter(Predicate<Element> filter) {
		if (filter == null) {
			filter = e -> true;
		}

		this.filter = filter;
	}
}
