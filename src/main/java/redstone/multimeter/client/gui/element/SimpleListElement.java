package redstone.multimeter.client.gui.element;

import java.text.Collator;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.function.Predicate;

import net.minecraft.client.gui.GuiGraphics;

import redstone.multimeter.client.MultimeterClient;
import redstone.multimeter.client.gui.Texture;
import redstone.multimeter.client.gui.screen.RSMMScreen;

public class SimpleListElement extends AbstractParentElement {

	protected static final int BORDER_MARGIN_TOP = 6;
	protected static final int BORDER_MARGIN_BOTTOM = 3;
	protected static final Collator COLLATOR = Collator.getInstance();

	protected final MultimeterClient client;

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

	public SimpleListElement(MultimeterClient client, int width) {
		this(client, width, 0, 0);
	}

	public SimpleListElement(MultimeterClient client, int width, int topBorder, int bottomBorder) {
		this.client = client;

		setSpacing(2);
		setTopBorder(topBorder);
		setBottomBorder(bottomBorder);

		this.sorter = null;
		this.filter = e -> true;

		setWidth(width);
	}

	@Override
	public void render(GuiGraphics graphics, int mouseX, int mouseY) {
		if (shouldUpdateHovered) {
			mouseMove(mouseX, mouseY);
			shouldUpdateHovered = false;
		}

		if (drawBackground) {
			drawBackground(graphics);
		}

		renderList(graphics, mouseX, mouseY);

		if (drawBackground) {
			drawBorders(graphics);
		}
	}

	@Override
	public boolean isMouseOver(double mouseX, double mouseY) {
		// ignore when the mouse is hovering over the borders
		return mouseX >= getX() && mouseX <= (getX() + getWidth()) && mouseY >= minY && mouseY <= maxY;
	}

	@Override
	public void setX(int x) {
		super.setX(x);
		updateContentX();
	}

	@Override
	public void setY(int y) {
		super.setY(y);
		updateContentY();
	}

	@Override
	public int getHeight() {
		// return the height minus the borders
		return height;
	}

	@Override
	public void update() {
		sort();
		updateCoords();
		super.update();
	}

	protected void drawBackground(GuiGraphics graphics) {
		int x0 = getX();
		int y0 = getY() + topBorder;
		int x1 = getX() + getWidth();
		int y1 = getY() + getTotalHeight() - bottomBorder;

		int offsetY = getOffsetY();

		int tx0 = x0 / 2;
		int ty0 = (y0 - offsetY) / 2;
		int tx1 = x1 / 2;
		int ty1 = (y1 - offsetY) / 2;

		renderTextureColor(graphics, Texture.OPTIONS_BACKGROUND, x0, y0, x1, y1, tx0, ty0, tx1, ty1, 0xFF, 0x20, 0x20, 0x20);
	}

	protected void renderList(GuiGraphics graphics, int mouseX, int mouseY) {
		graphics.enableScissor(getX(), minY, getX() + getWidth(), maxY);

		List<Element> children = getChildren();

		for (int index = 0; index < children.size(); index++) {
			Element element = children.get(index);

			if (element.getY() + element.getHeight() < minY) {
				continue;
			}
			if (element.getY() > maxY) {
				break;
			}

			if (element.isVisible()) {
				renderElement(element, graphics, mouseX, mouseY);
			}
		}

		graphics.disableScissor();
	}

	protected void renderElement(Element element, GuiGraphics graphics, int mouseX, int mouseY) {
		element.render(graphics, mouseX, mouseY);
	}

	protected void drawBorders(GuiGraphics graphics) {
		boolean renderTop = topBorder > 0;
		boolean renderBottom = bottomBorder > 0;

		if (!renderTop && !renderBottom) {
			return;
		}

		int x = getX();
		int width = getWidth();

		int x0 = x;
		int y0;
		int x1 = x + width;
		int y1;

		int tx0 = x0 / 2;
		int ty0;
		int tx1 = x1 / 2;
		int ty1;

		if (renderTop) {
			y0 = minY - topBorder;
			y1 = minY;

			ty0 = y0 / 2;
			ty1 = y1 / 2;

			renderTextureColor(graphics, Texture.OPTIONS_BACKGROUND, x0, y0, x1, y1, tx0, ty0, tx1, ty1, 0xFF, 0x40, 0x40, 0x40);
			renderGradient(graphics, x0, y1, width, 4, 0xFF000000, 0x00000000);
		}
		if (renderBottom) {
			y0 = maxY;
			y1 = maxY + bottomBorder;

			ty0 = y0 / 2;
			ty1 = y1 / 2;

			renderTextureColor(graphics, Texture.OPTIONS_BACKGROUND, x0, y0, x1, y1, tx0, ty0, tx1, ty1, 0xFF, 0x40, 0x40, 0x40);
			renderGradient(graphics, x0, y0 - 4, width, 4, 0x00000000, 0xFF000000);
		}
	}

	public int getTopBorder() {
		return topBorder;
	}

	public int getTopBorderAndMargin() {
		return topBorder + BORDER_MARGIN_TOP;
	}

	public int getBottomBorder() {
		return bottomBorder;
	}

	public int getBottomBorderAndMargin() {
		return bottomBorder + BORDER_MARGIN_BOTTOM;
	}

	public int getSpacing() {
		return spacing;
	}

	public int getTotalHeight() {
		return getHeight() + getTopBorderAndMargin() + getBottomBorderAndMargin();
	}

	public int getTotalSpacing() {
		return spacing * (getChildren().size() - 1);
	}

	protected int getAmountOffScreen(Element element) {
		int top = element.getY();

		if (top < minY) {
			return top - minY - 1;
		}

		int bottom = top + element.getHeight();

		if (bottom > maxY) {
			return bottom - maxY + 1;
		}

		return 0;
	}

	public int getEffectiveWidth() {
		return getWidth();
	}

	private void sort() {
		if (sorter == null) {
			return;
		}

		List<Element> children = getChildren();
		Queue<Element> queue = new PriorityQueue<>(sorter);

		for (int index = 0; index < children.size(); index++) {
			queue.add(children.get(index));
		}

		children.clear();

		while (!queue.isEmpty()) {
			children.add(queue.poll());
		}
	}

	protected void updateContentX() {
		List<Element> children = getChildren();
		int x = getX();

		for (int index = 0; index < children.size(); index++) {
			children.get(index).setX(x);
		}
	}

	protected void updateContentY() {
		List<Element> children = getChildren();
		int yStart = getY() + topBorder + BORDER_MARGIN_TOP + getOffsetY();
		int y = yStart;

		for (int index = 0; index < children.size(); index++) {
			Element element = children.get(index);

			element.setY(y);
			element.setVisible(filter.test(element));

			if (element.isVisible()) {
				y += element.getHeight() + spacing;
			}
		}

		RSMMScreen screen = client.getScreen();

		height = (y - spacing) - yStart;
		minY = Math.max(getY() + topBorder, screen.getY());
		maxY = Math.min(getY() + getTotalHeight() - bottomBorder, screen.getY() + screen.getHeight());

		shouldUpdateHovered = true;
	}

	protected int getOffsetY() {
		return 0;
	}

	public boolean shouldDrawBackground() {
		return drawBackground;
	}

	public void setDrawBackground(boolean drawBackground) {
		this.drawBackground = drawBackground;
	}

	public void add(Element element) {
		addChild(element);
	}

	public void addAll(Collection<? extends Element> elements) {
		for (Element element : elements) {
			addChild(element);
		}
	}

	public void clear() {
		removeChildren();
		updateCoords();
	}

	public void updateCoords() {
		updateContentX();
		updateContentY();
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
